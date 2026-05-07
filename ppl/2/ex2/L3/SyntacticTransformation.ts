import { ClassExp, ProcExp, Exp, Program, makeProcExp, makeAppExp, makePrimOp, makeLitExp, makeIfExp } from "./L3-ast";
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
                
            )

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
