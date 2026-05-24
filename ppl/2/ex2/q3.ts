import { BoolExp, CExp, Exp, isBoolExp, isIfExp, isNumExp, isPrimOp, isStrExp, isVarRef, Program } from './L3/L3-ast';
import { Result, bind, makeFailure, makeOk} from './shared/result';

/*
Purpose: Transform L2 AST to Python program string
Signature: l2ToPython(l2AST)
Type: [Parsed | Error] => Result<string>
*/

const convertPrimOp = (op: string) : string => {
    return op;
}

const CExpToPython = (exp: CExp) : Result<string> => {
    if (isNumExp(exp)) {
        return makeOk(exp.val.toString());
    } 
    else if (isBoolExp(exp)) {
        return makeOk(exp.val ? "True" : "False");
    }
    else if (isStrExp(exp)) {
        return makeOk(exp.val);
    }
    else if (isPrimOp(exp)) {
        return makeOk(convertPrimOp(exp.op));
    }
    else if (isVarRef(exp)) {
        return makeOk(exp.var);
    }
    else if (isIfExp(exp)) {
        return bind(
            CExpToPython(exp.test),
            (testStr) => bind(
                CExpToPython(exp.then),
                (thenStr) => bind(
                    CExpToPython(exp.alt),
                    (altStr) => makeOk('(${thenStr} if ${')
                )
            )
        );
    }
    return makeOk("");
}
export const l2ToPython = (exp: Exp | Program): Result<string>  => 
    makeFailure("TODO");