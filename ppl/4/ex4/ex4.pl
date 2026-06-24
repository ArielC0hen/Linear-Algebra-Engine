/*
 * **********************************************
 * Printing result depth
 *
 * You can enlarge it, if needed.
 * **********************************************
 */
maximum_printing_depth(100).

/* helpers */
list([]).
list([X|Xs]) :- list(Xs).

member(X, [X|_]).
member(X,[_|Ys]) :- member(X, Ys).


append([], Xs, Xs):-list(Xs).
append([X | Xs], Ys, [X | Zs]) :- append(Xs, Ys, Zs).

prefix(Xs, Ys) :- append(Xs, _Zs, Ys).

suffix(Xs, Ys) :- append(_Zs, Xs, Ys).



:- current_prolog_flag(toplevel_print_options, A),
   (select(max_depth(_), A, B), ! ; A = B),
   maximum_printing_depth(MPD),
   set_prolog_flag(toplevel_print_options, [max_depth(MPD)|B]).

% Signature: sub_list(Sublist, List)/2
% Purpose: All elements in Sublist appear in List in the same order.
% Precondition: List is fully instantiated (queries do not include variables in their second argument).

sub_list(Xs,Ys) :- suffix(Rs, Ys), prefix(Xs, Rs).






% Signature: swap_list(List, InversedList)/2
% Purpose: InversedList is the ‘mirror’ representation of List, i.e, each item in the list is recursively replaced with the item at the position, with refers to the beginning and the end of the list.   

/* expensive recursion
swap_list([],[]).
swap_list([X|Xs],Ys) :- swap_list(Zs, Xs) , append(Zs,[X],Ys). 
*/

/* the second argument will function as accumalator */
swap_list(Xs,Ys) :- swap_list1(Xs,[],Ys).
swap_list1([], As, As).
swap_list1([X|Xs], As, Ys) :- swap_list1(Xs, [X|As], Ys).





% Signature: sub_tree(Subtree, Tree)/2
% Purpose: Tree contains Subtree.

sub_tree(T, T).
sub_tree(T,tree(_, Left, Right)) :- sub_tree(T,Left). 
sub_tree(T,tree(_, Left, Right)) :- sub_tree(T,Right). 



% Signature: swap_tree(Tree, InversedTree)/2
% Purpose: InversedTree is the 'mirror' representation of Tree.

swap_tree(tree(Lf,void,void), tree(Lf,void,void)).

swap_tree(tree(Lf,Left,Right), tree(Lf,NewLeft,NewRight)) :- swap_tree(Left,N)