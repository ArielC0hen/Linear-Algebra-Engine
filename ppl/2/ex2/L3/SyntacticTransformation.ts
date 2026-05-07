import { ClassExp, ProcExp, Exp, Program, makeProcExp, makeAppExp, makePrimOp, makeLitExp, makeIfExp, makeVarRef } from "./L3-ast";
import { Result, makeFailure } from "../shared/result";
import { makeSymbolSExp } from "./L3-value";

/*
Purpose: Transform ClassExp to ProcExp
Signature: class2proc(classExp)
Type: ClassExp => ProcExp
*/
export const class2proc = (exp: ClassExp): ProcExp => {
    const fields = exp.fields;
    const methods = exp.fields;
    const ifs = (methods: Binding[]): CExp => methods.length === 0 
        ? makeLitExp(makeSymbolSExp("error"))
        : makeIfExp( 
            makeAppExp(
                makePrimOp("eq?"),
                [
                    makeVarRef("msg"),
                    makeLitExp(makeSymbolSExp(methods[0].var.var.var))
                ]
            ),
            methods[0].val,
            ifs(methods.slice(1))

        );
    makeProcExp(fields)
}



/*
Purpose: Transform all class forms in the given AST to procs
Signature: transform(AST)
Type: [Exp | Program] => Result<Exp | Program>
*/

export const transform = (exp: Exp | Program): Result<Exp | Program> =>
    //@TODO
    makeFailure("ToDo");
