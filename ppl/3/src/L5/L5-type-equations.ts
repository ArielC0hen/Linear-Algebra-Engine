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

// ============================================================
// Pool ADT
export type PoolItem = {e: A.Exp, te: T.TExp}
export type Pool = PoolItem[];

export const makeEmptyPool = () => [];

export const extendPool = (exp: A.Exp, pool: Pool): Pool =>
    cons({e: exp, te: T.makeFreshTVar()}, pool);

const extendPoolVarDecl = (vd: A.VarDecl, pool: Pool): Pool =>
    cons({e: A.makeVarRef(vd.var), te: vd.texp}, pool);

export const inPool = (pool: Pool, e: A.Exp): Opt.Optional<T.TExp> => {
    const exp = R.find((item) => 
        item.e === e
        || (A.isVarRef(item.e) && A.isVarRef(e) && item.e.var === e.var)
        || (A.isLitExp(item.e) && A.isLitExp(e) && R.equals(item.e.val, e.val))
    , pool);
    return exp ? Opt.makeSome(R.prop('te')(exp)) : Opt.makeNone();
}

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

// 3(a) Modify expToPool to handle compound and empty lists deeply
export const expToPool = (exp: A.Exp): Pool => {
    const findVars = (e: A.Exp, pool: Pool): Pool =>
        A.isAtomicExp(e) ? extendPool(e, pool) :
        A.isProcExp(e) ? extendPool(e, reducePool(findVars, e.body, reducePoolVarDecls(extendPoolVarDecl, e.args, pool))) :
        A.isAppExp(e) ? extendPool(e, reducePool(findVars, cons(e.rator, e.rands), pool)) :
        A.isLitExp(e) && V.isEmptySExp(e.val) ? extendPool(e, pool) : 
        A.isLitExp(e) && V.isCompoundSExp(e.val) ? (() => {
            const headLit = A.makeLitExp(e.val.val1);
            const tailLit = A.makeLitExp(e.val.val2);
            const poolTail = findVars(tailLit, pool);
            const combinedPool = findVars(headLit, poolTail);
            return extendPool(e, combinedPool);
        })() :
        extendPool(e, pool);
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

export const flatten = <T>(listOfLists: readonly T[][]): T[] => R.chain(R.identity, listOfLists);

export const poolToEquations = (pool: Pool): Opt.Optional<Equation[]> => {
    const poolWithoutVars = R.filter(R.propSatisfies(R.complement(A.isVarRef), 'e'), pool);
    return Opt.mapv(Opt.mapOptional((e: A.Exp) => makeEquationsFromExp(e, pool), R.map(R.prop('e'), poolWithoutVars)), 
                    (eqns: Equation[][]) => flatten(eqns));
};

// 3(b) Modify makeEquationsFromExp to enforce homogeneous list parameters
export const makeEquationsFromExp = (exp: A.Exp, pool: Pool): Opt.Optional<Equation[]> =>
    A.isAppExp(exp) ? Opt.bind(inPool(pool, exp.rator), (rator: T.TExp) =>
                        Opt.bind(Opt.mapOptional((e) => inPool(pool, e), exp.rands), (rands: T.TExp[]) =>
                            Opt.mapv(inPool(pool, exp), (e: T.TExp) => 
                                [makeEquation(rator, T.makeProcTExp(rands, e))]))) :
    
    A.isProcExp(exp) ? Opt.bind(inPool(pool, exp), (left: T.TExp) =>
                            Opt.mapv(Opt.bind(safeLast(exp.body), (last: A.CExp) => inPool(pool, last)), (ret: T.TExp) =>
                                [makeEquation(left, T.makeProcTExp(R.map((vd) => vd.texp, exp.args), ret))])) :
    
    A.isLitExp(exp) ? (
        V.isEmptySExp(exp.val) ?
            Opt.mapv(inPool(pool, exp), (left: T.TExp) => [makeEquation(left, T.makeListTExp(T.makeFreshTVar()))]) :
        V.isCompoundSExp(exp.val) ?
            Opt.bind(inPool(pool, exp), (left: T.TExp) => {
                const headLit = A.makeLitExp((exp.val as V.CompoundSExp).val1);
                const tailLit = A.makeLitExp((exp.val as V.CompoundSExp).val2);
                return Opt.bind(inPool(pool, headLit), (headTE: T.TExp) => 
                    Opt.mapv(inPool(pool, tailLit), (tailTE: T.TExp) => [
                        makeEquation(left, tailTE),
                        makeEquation(tailTE, T.makeListTExp(headTE))
                    ]));
            }) :
        isNumber(exp.val) ? Opt.mapv(inPool(pool, exp), (left: T.TExp) => [makeEquation(left, T.makeNumTExp())]) :
        isBoolean(exp.val) ? Opt.mapv(inPool(pool, exp), (left: T.TExp) => [makeEquation(left, T.makeBoolTExp())]) :
        isString(exp.val) ? Opt.mapv(inPool(pool, exp), (left: T.TExp) => [makeEquation(left, T.makeStrTExp())]) :
        Opt.mapv(inPool(pool, exp), (left: T.TExp) => [])
    ) :
    
    A.isNumExp(exp) ? Opt.mapv(inPool(pool, exp), (left: T.TExp) => [makeEquation(left, T.makeNumTExp())]) :
    A.isBoolExp(exp) ? Opt.mapv(inPool(pool, exp), (left: T.TExp) => [makeEquation(left, T.makeBoolTExp())]) :
    A.isStrExp(exp) ? Opt.mapv(inPool(pool, exp), (left: T.TExp) => [makeEquation(left, T.makeStrTExp())]) :
    A.isPrimOp(exp) ? Opt.bind(inPool(pool, exp), (left: T.TExp) =>
                        Opt.mapv(Res.resultToOptional(TC.typeofPrim(exp)), (right: T.TExp) =>
                            [makeEquation(left, right)])) :
    Opt.makeNone();

// ========================================================
export const inferType = (exp: A.Exp): Opt.Optional<T.TExp> => {
    const pool = expToPool(exp);
    const equations = poolToEquations(pool);
    const sub = Opt.bind(equations, (eqns: Equation[]) => Res.resultToOptional(solveEquations(eqns)));
    const texp = inPool(pool, exp);
    return Opt.bind(sub, (sub: S.Sub) =>
                Opt.mapv(texp, (texp: T.TExp) =>
                    S.applySub(sub, texp)))
};

export const infer = (exp: string): Res.Result<string> =>
    Res.bind(p(exp), (x) =>
        Res.bind(A.parseL5Exp(x), (exp: A.Exp) => 
            Opt.maybe(inferType(exp),
                (te: T.TExp) => T.unparseTExp(te),
                () => Res.makeFailure("Infer type failed"))));

// ========================================================
// Type Equation Solving
export const solveEquations = (equations: Equation[]): Res.Result<S.Sub> =>
    solve(equations, S.makeEmptySub());

const solve = (equations: Equation[], sub: S.Sub): Res.Result<S.Sub> => {
    if (!isNonEmptyList<Equation>(equations)) {
        return Res.makeOk(sub);
    }

    const solveVarEq = (tvar: T.TVar, texp: T.TExp): Res.Result<S.Sub> =>
        Res.bind(S.extendSub(sub, tvar, texp), sub2 => solve(rest(equations), sub2));

    const bothSidesEqualVars = (eq: Equation): boolean =>
        T.isTVar(eq.left) && T.isTVar(eq.right) && T.eqTVar(eq.left, eq.right);
    
    const handleBothSidesAtomic = (l: T.AtomicTExp , r: T.AtomicTExp): Res.Result<S.Sub> =>
        T.eqAtomicTExp(l, r) ? solve(rest(equations), sub) : Res.makeFailure(`Non-equal atomic types`);

    const eq = makeEquation(S.applySub(sub, first(equations).left),
                            S.applySub(sub, first(equations).right));

    return bothSidesEqualVars(eq) ? solve(rest(equations), sub) :
           T.isTVar(eq.left) ? solveVarEq(eq.left, eq.right) :
           T.isTVar(eq.right) ? solveVarEq(eq.right, eq.left) :
           T.isAtomicTExp(eq.left) && T.isAtomicTExp(eq.right) ? handleBothSidesAtomic(eq.left , eq.right) :
           T.isCompoundTExp(eq.left) && T.isCompoundTExp(eq.right) && canUnify(eq) ?
                solve(R.concat(rest(equations), splitEquation(eq)), sub) :
           Res.makeFailure(`Incompatible types ${format(eq)}`);
};

// 3(c) Modify canUnify to approve list vs list type pairings
const canUnify = (eq: Equation): boolean =>
    T.isProcTExp(eq.left) && T.isProcTExp(eq.right) ? (eq.left.paramTEs.length === eq.right.paramTEs.length) :
    T.isListTExp(eq.left) && T.isListTExp(eq.right) ? true :
    false;

// 3(d) Modify splitEquation to break list types down to item elements
const splitEquation = (eq: Equation): Equation[] =>
    (T.isProcTExp(eq.left) && T.isProcTExp(eq.right)) ?
        R.zipWith(makeEquation,
                  cons(eq.left.returnTE, eq.left.paramTEs),
                  cons(eq.right.returnTE, eq.right.paramTEs)) :
    (T.isListTExp(eq.left) && T.isListTExp(eq.right)) ? 
        [makeEquation(eq.left.itemTE, eq.right.itemTE)] :
    [];