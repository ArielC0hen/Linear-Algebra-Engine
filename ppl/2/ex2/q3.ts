import { map } from 'ramda';
import { BoolExp, CExp, Exp, isAppExp, isBoolExp, isDefineExp, isIfExp, isNumExp, isPrimOp, isProcExp, isProgram, isStrExp, isVarRef, Program, VarDecl } from './L3/L3-ast';
import { Result, bind, makeFailure, makeOk, mapResult} from './shared/result';

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
                    (altStr) => makeOk(`(${thenStr} if ${testStr} else ${altStr}`)
                )
            )
        );
    }
    else if (isProcExp(exp)) {
        const args = map (
            (a : VarDecl) => a.var,
            exp.args
        ).join(', ');
        const body = exp.body[0]; // we are allowed to assume body contains a single exp
        return bind(
            CExpToPython(body),
            (bodyStr) => makeOk(`(lambda ${args} : ${bodyStr})`)
        );
    }
    else if (isAppExp(exp)) {
        return bind(
            CExpToPython(exp.rator),
            (ratorStr) => bind(
                mapResult(
                    CExpToPython,
                    exp.rands
                ),
                (randsStrs) => {
                    if (isPrimOp(exp.rator)) {
                        const op = exp.rator.op;
                        if (op === "not") {
                            return makeOk(`(not ${randsStrs[0]})`);
                        }
                        else if (op === "number?" || op === "boolean") {
                            const toString = randsStrs.join(`${convertPrimOp(op)}`);
                            return makeOk(toString);
                        }
                        else {
                            const converted = convertPrimOp(op);
                            return makeOk(`${randsStrs.join(` ${converted} `)}`);
                        }
                    }
                    else {
                        return makeOk(`${ratorStr}(${randsStrs.join(',')})`);
                    }
                }
            )
        );
    }
    else {
        return makeFailure("Unknown expression");
    }
}

const expToPython = (exp: Exp): Result<string> => {
    if (isDefineExp(exp)) {
        return bind(
            CExpToPython(exp.val),
            (valStr) => makeOk(`${exp.var.var} = ${valStr}`)
        );
    } else {
        return CExpToPython(exp);
    }
}


export const l2ToPython = (exp: Exp | Program): Result<string>  => {
    if (isProgram(exp)) {
        return bind(
            mapResult(expToPython, exp.exps),
            (expStrs) => makeOk(expStrs.join("\n"))
        );
    } else {
        return expToPython(exp);
    }
}