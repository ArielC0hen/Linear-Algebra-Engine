// L5-type-equations
import * as R from "ramda";
import * as A from "./L5-ast";
import * as S from "./L5-substitution-adt";
import * as TC from "./L5-typecheck";
import * as V from "./L5-value";
import * as T from "./TExp";
import * as Res from "../shared/result";
import * as Opt from "../shared/optional";
import { isEmpty, first, rest, cons, isNonEmptyList } from "../shared/list";
import { parse as p } from "../shared/parser";
import { format } from "../shared/format";
import { isBoolean, isNumber, isString } from "../shared/type-predicates";

// ============================================================n
// Pool ADT
// A pool represents a map from Exp to TExp
// It is implemented as a list of pairs (Exp TExp).
// When a new Exp is added to a pool, a fresh Tvar
// is allocated for it.
export type PoolItem = {e: A.Exp, te: T.TExp}
export type Pool = PoolItem[];

export const makeEmptyPool = () => [];

// Purpose: construct a pool with one additional pair
//          [exp, fresh-tvar]
// @Pre: exp is not already in pool.
export const extendPool = (exp: A.Exp, pool: Pool): Pool =>
    cons({e: exp, te: T.makeFreshTVar()}, pool);

// Purpose: construct a pool with one additional pair
//          [VarRef(var), texp]
//          from a VarDecl(var, texp) declaration.
// @Pre: var is not already in pool - which means
// that all bound variables have been renamed with distinct names.
const extendPoolVarDecl = (vd: A.VarDecl, pool: Pool): Pool =>
    cons({e: A.makeVarRef(vd.var), te: vd.texp}, pool);

export const inPool = (pool: Pool, e: A.Exp): Opt.Optional<T.TExp> => {
    const exp = R.find((item) => 
        item.e === e
        || (A.isVarRef(item.e) && A.isVarRef(e) && item.e.var === e.var)
        || (A.isLitExp(item.e) && A.isLitExp(e) && item.e.val === e.val)
        // (modified for HW3 - no need to edit!)
    , pool);
    return exp ? Opt.makeSome(R.prop('te')(exp)) : Opt.makeNone();
}

// Map a function over a list of expressions to accumulate
// matching sub-expressions into a pool.
// fun should construct a new pool given a new expression from exp-list
// that has not yet been seen before.
const reducePool = (fun: (e: A.Exp, pool: Pool) => Pool, exps: A.Exp[], result: Pool): Pool =>
    isNonEmptyList<A.Exp>(exps) ?
        Opt.maybe(inPool(result, first(exps)),
                  _ => reducePool(fun, rest(exps), result),
                  () => reducePool(fun, rest(exps), fun(first(exps), result))) :
    result;

const reducePoolVarDecls = (fun: (e: A.VarDecl, pool: Pool) => Pool, vds: A.VarDecl[], result: Pool): Pool =>
    isNonEmptyList<A.VarDecl>(vds) ?
        Opt.maybe(inPool(result, A.makeVarRef(first(vds).var)),
                  _ => reducePoolVarDecls(fun, rest(vds), result),
                  () => reducePoolVarDecls(fun, rest(vds), fun(first(vds), result))) :
    result;

// Purpose: Traverse the abstract syntax tree L5-exp
//          and collect all sub-expressions into a Pool of fresh type variables.
// Example:
// bind(p('(+ x 1)'), (s) => mapv(parseL5Exp(s), (e) => TE.expToPool(e))) =>
// Ok([[AppExp(PrimOp(+), [VarRef(x), NumExp(1)]), TVar(16)],
//     [NumExp(1), TVar(15)],
//     [VarRef(x), TVar(14)],
//     [PrimOp(+), TVar(13)]])
export const expToPool = (exp: A.Exp): Pool => {
    const findVars = (e: A.Exp, pool: Pool): Pool =>
        A.isAtomicExp(e) ? extendPool(e, pool) :
        A.isProcExp(e) ? extendPool(e, reducePool(findVars, e.body, reducePoolVarDecls(extendPoolVarDecl, e.args, pool))) :
        A.isLitExp(e) && V.isEmptySExp(e.val) ?
            extendPool(e, pool) : // empty list just give it a generic type and move on
        A.isLitExp(e) && V.isCompoundSExp(e.val) ? (() => {
            const headLit = A.makeLitExp(e.val.val1); // car
            const tailLit = A.makeLitExp(e.val.val2); // cdr
            const poolTail = findVars(tailLit, pool);
            const combinedPool = findVars(headLit, poolTail);
            return extendPool(e, combinedPool);
        }) () :
        makeEmptyPool();
    return findVars(exp, makeEmptyPool());
};

// ========================================================
// Equations ADT
export type Equation = {left: T.TExp, right: T.TExp}
export const makeEquation = (l: T.TExp, r: T.TExp): Equation => ({left: l, right: r});

export const safeLast = <T extends any>(list: readonly T[]): Opt.Optional<T> => {
    const last = R.last(list);
    return last ? Opt.makeSome(last) : Opt.makeNone();
}

// Purpose: flatten a list of lists into a flat list of items
// Example:
// flatten([[1,2], [], [3]]) => [1,2,3]
// flatten([]) => []
// flatten([[]]) => []
export const flatten = <T>(listOfLists: readonly T[][]): T[] => R.chain(R.identity, listOfLists);

// Constructor for equations for a Scheme expression:
// this constructor implements the second step of the type-inference-equations
// algorithm -- derive equations for all composite sub expressions of a
// given L5 expression. Its input is a pool of pairs (L5-exp Tvar).
// A Scheme expression is mapped to a pool with L5-exp->pool

// Purpose: Return a set of equations for a given Exp encoded as a pool