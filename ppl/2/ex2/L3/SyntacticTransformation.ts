import { ClassExp, ProcExp, Exp, Program, makeProcExp, makeAppExp, makePrimOp } from "./L3-ast";
import { Result, makeFailure } from "../shared/result";

/*
Purpose: Transform ClassExp to ProcExp
Signature: class2proc(classExp)
Type: ClassExp => ProcExp
*/
export const class2proc = (exp: ClassExp): ProcExp => {
    const fields = exp.fields;
    const methods = exp.fields;
    const 
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
