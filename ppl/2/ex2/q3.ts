import { BoolExp, CExp, Exp, isBoolExp, isNumExp, Program } from './L3/L3-ast';
import { Result, makeFailure, makeOk} from './shared/result';

/*
Purpose: Transform L2 AST to Python program string
Signature: l2ToPython(l2AST)
Type: [Parsed | Error] => Result<string>
*/

const convertPrimOp = (op: string) : string => {
    return op;
}

const CExpToPython = (exp: BoolExp) : Result<string> => {
    if (isNumExp(exp)) {
        return makeOk(exp.val.toString());
    } 
    else if (isBoolExp(exp)) {
        if (exp.val) {
            return makeOk("True");
        } else {
            return makeOk("False");
        }
    }
    else if ()
}
export const l2ToPython = (exp: Exp | Program): Result<string>  => 
    makeFailure("TODO");