import { ClassExp, ProcExp, Exp, Program, makeProcExp, makeAppExp, makePrimOp, makeLitExp, makeIfExp, makeVarRef, Binding, CExp, makeVarDecl, isClassExp, isProgram, isProcExp, isIfExp, makeProgram } from "./L3-ast";
import { makeSymbolSExp } from "./L3-value";
import { bind, makeOk, mapResult, mapv, Result } from "../shared/result";
import { map } from "ramda";

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
    if (isClassExp(exp)) {
        const proc = class2proc(exp);
        return makeOk(proc);
    }
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
            (body) => makeProcExp()
        );
    }
}
    //@TODO

