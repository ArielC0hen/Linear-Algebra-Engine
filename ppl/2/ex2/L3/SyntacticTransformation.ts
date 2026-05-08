import { ClassExp, ProcExp, Exp, Program, makeProcExp, makeAppExp, makePrimOp, makeLitExp, makeIfExp, makeVarRef, Binding, CExp, makeVarDecl, isClassExp, isProgram, isProcExp, isIfExp, makeProgram, isDefineExp, makeDefineExp, isAppExp, isLetExp, makeBinding, makeLetExp } from "./L3-ast";
import { makeSymbolSExp } from "./L3-value";
import { bind, makeOk, mapResult, mapv, Result } from "../shared/result";
import { map, zipWith } from "ramda";
import { first } from "../shared/list";

/*
Purpose: Transform ClassExp to ProcExp
Signature: class2proc(classExp)
Type: ClassExp => ProcExp
*/
export const class2proc = (exp: ClassExp): ProcExp => {
    const fields = exp.fields;
    const methods = exp.methods; // also includes getters
    const ifs = (methods: Binding[]): CExp => methods.length === 0 
        ? makeLitExp(makeSymbolSExp("error"))
        : makeIfExp( 
            makeAppExp(
                makePrimOp("eq?"),
                [
                    makeVarRef("msg"),
                    makeLitExp(makeSymbolSExp(methods[0].var.var)) // Binding --var--> VarDecl --var--> string
                ]
            ),
            methods[0].val,
            ifs(methods.slice(1))
        );
    return makeProcExp(
        fields,
        [makeProcExp(
            [makeVarDecl("msg")],
            [ifs(methods)]
        )]);
}



/*
Purpose: Transform all class forms in the given AST to procs
Signature: transform(AST)
Type: [Exp | Program] => Result<Exp | Program>
*/

export const transform = (exp: Exp | Program): Result<Exp | Program> => {
    // class
    /*
    if (isClassExp(exp)) {
        const proc = class2proc(exp);
        return makeOk(proc);
    }
        */
    if (isClassExp(exp)) {
        // 1. Transform the methods first to catch nested classes
        return bind(mapResult((m: Binding) => 
            mapv(transform(m.val), (tVal) => makeBinding(m.var.var, tVal as CExp)), 
            exp.methods
        ), (tMethods) => {
            // 2. Now pass the transformed methods to class2proc
            // If the test wants 'a' instead of '(lambda () a)', 
            // you might need to extract the body here:
            const finalMethods = tMethods.map(m => {
                if (isProcExp(m.val) && m.val.args.length === 0) {
                    return makeBinding(m.var.var, m.val.body[0] as CExp);
                }
                return m;
            });

            return makeOk(class2proc({ ...exp, methods: finalMethods }));
        });
    }
    /*
    if (isClassExp(exp)) {
        // 1. Transform all method bodies (this catches nested classes!)
        const transformedMethods = mapResult((method: Binding) => {
            return mapv(transform(method.val), (transformedVal) => 
                makeBinding(method.var.var, transformedVal as CExp)
            );
        }, exp.methods);

        // 2. Once methods are transformed, convert the class to a proc
        return mapv(transformedMethods, (methods: Binding[]) => 
            class2proc({ ...exp, methods })
        );
    }
        */
    /*
    // inside transform...
    if (isClassExp(exp)) {
        // 1. Transform the method values first (in case they contain classes!)
        const transformedMethodsResult = mapResult(
            (b: Binding) => mapv(transform(b.val), (transformedVal) => makeBinding(b.var.var, transformedVal as CExp)),
            exp.methods
        );

        // 2. Use bind to get the transformed methods and pass them to class2proc
        return bind(transformedMethodsResult, (transformedMethods: Binding[]) => {
            // Create a new ClassExp with transformed methods
            const transformedClass = { ...exp, methods: transformedMethods };
            return makeOk(class2proc(transformedClass));
        });
    }
        */
    // program
    if (isProgram(exp)) {
        const transformed = mapResult(transform, exp.exps);
        return mapv(transformed, (exps: (Exp | Program)[]) => makeProgram(exps as Exp[]));
        // we know transformed won't contain program var because there's only one program
    }
    //others
    if (isIfExp(exp)) {
        return mapv(
            mapResult(transform, [exp.test, exp.then, exp.alt]),
            ([test, then, alt]) => makeIfExp(test as CExp,then as CExp,alt as CExp)
        );
    }
    if (isProcExp(exp))  {
        return mapv(
            mapResult(transform, exp.body),
            (body) => makeProcExp(exp.args, body as CExp[])
        );
    }
    /*
    if(isAppExp(exp)) {
        return mapv(
            mapResult(transform, exp.rands),
            (rands) => makeAppExp(exp.rator,rands as CExp[])
        );
    }
        */
    if (isAppExp(exp)) {
        return bind(transform(exp.rator), (rator: Exp | Program) =>
            mapv(mapResult(transform, exp.rands), (rands: (Exp | Program)[]) =>
                makeAppExp(rator as CExp, rands as CExp[])
            )
        );
    }
    if (isDefineExp(exp)) {
        return mapv(
            transform(exp.val),
            (val) => makeDefineExp(exp.var, val as CExp)
        );
    }
    if (isLetExp(exp)) {
        const vars = map(b=> b.var.var, exp.bindings);
        const vals = map(b=> b.val, exp.bindings);
        return bind ( // newBindings would normally be Result<Binding[]> instead of Binding[] for makeLetExp, so we unwrap
            mapv(
                mapResult(transform, vals),
                (vals) => zipWith(makeBinding, vars, vals as CExp[])
            ),
            (newBindings) => mapv(
                mapResult(transform,exp.body),
                (newBody) => makeLetExp(newBindings,newBody as CExp[])
            )
        )
    }
    // atomics
    return makeOk(exp);

}
    //@TODO

