import { ClassExp, ProcExp, Exp, Program, makeProcExp, makeAppExp, makePrimOp, makeLitExp, makeIfExp, makeVarRef, Binding, CExp, makeVarDecl, isClassExp } from "./L3-ast";
import { Result, bind, makeFailure } from "../shared/result";
import { makeSymbolSExp } from "./L3-value";

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

export const transform = (exp: Exp | Program): Result<Exp | Program> =>
    //@TODO
    if (isClassExp(exp)) {
        return bind(class2proc, exp);
    }
