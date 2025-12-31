package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        executor = new TiredExecutor(numThreads);
    }

    /*
    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        if (computationRoot.getNodeType() == ComputationNodeType.MATRIX) {
            return computationRoot;
        }
        List<ComputationNode> children = computationRoot.getChildren();
        ComputationNodeType operand = computationRoot.getNodeType();
        leftMatrix = new sharedMatrix(run(children.get(0)).getMatrix()); // start from the bottom
        if (operand == ComputationNodeType.NEGATE) {
            executor.submitAll(createNegateTasks());
        } else if (operand == ComputationNodeType.TRANSPOSE) {
            executor.submitAll(createTransposeTasks());
        } else {
            for (int i = 1; i < children.size(); i++) {
                rightMatrix = new SharedMatrix(run(children.get(i)).getMatrix()); // start from the bottom
                List<Runnable> tasks;
                if (operand == ComputationNodeType.ADD) {
                    tasks = createAddTasks();
                } else if (operand == ComputationNodeType.MULTIPLY) {
                    tasks = createMultiplyTasks();
                } else {
                    throw new Exception("Undefined operand");
                }
                executor.submitAll(tasks);
            }
        }
        return leftMatrix;
    }
    */

    public ComputationNode run(ComputationNode computationRoot) {
        while (computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
            //System.out.println("ran");
            ComputationNode almostLeaf = computationRoot.findResolvable(); // finds the first "calculatable" node (all of its children are matrices)
            loadAndCompute(almostLeaf);
        }
        //System.out.println("done");
        System.out.println(getWorkerReport());
        return computationRoot;
    }


    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        List<ComputationNode> children = node.getChildren();
        ComputationNodeType operand = node.getNodeType();
        leftMatrix = new SharedMatrix(children.get(0).getMatrix()); /// FUCK YOU
        if (operand == ComputationNodeType.NEGATE) {
            if (children.size() > 1) {
                throw new IllegalArgumentException("Negate is an unary operand but it was given more than 1 matrix to work with");
            }
            //System.out.println("negate");
            executor.submitAll(createNegateTasks());
            //System.out.println("done negate");
        } else if (operand == ComputationNodeType.TRANSPOSE) {
            if (children.size() > 1) {
                throw new IllegalArgumentException("Transpose is an unary operand but it was given more than 1 matrix to work with");
            }
            //System.out.println("transpose");
            executor.submitAll(createTransposeTasks());
        } else {
            if (children.size() == 1) {
                throw new IllegalArgumentException("Add/Mult are binary operands but they were given only 1 matrix");
            }
            //System.out.println("add or mult");
            for (int i = 1; i < children.size(); i++) {
                rightMatrix = new SharedMatrix(children.get(i).getMatrix());
                List<Runnable> tasks;
                if (operand == ComputationNodeType.ADD) {
                    tasks = createAddTasks();
                } else if (operand == ComputationNodeType.MULTIPLY) {
                    tasks = createMultiplyTasks();
                } else {
                    break;
                }
                executor.submitAll(tasks);
            }
        }
        // submitAll waits for every task to finish so leftMatrix at this point is the final result
        node.resolve(leftMatrix.readRowMajor());
    }
    

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int index = i; // i cant be used directly because it's not final (dumb)
            Runnable task = () -> {
                SharedVector leftRowOrCol = leftMatrix.get(index);
                SharedVector rightRowOrCol = rightMatrix.get(index);
                leftRowOrCol.add(rightRowOrCol);
            };
            tasks.add(task);
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int index = i; // i cant be used directly because it's not final (dumb)
            Runnable task = () -> {
                SharedVector leftRowOrCol = leftMatrix.get(index);
                leftRowOrCol.vecMatMul(rightMatrix);
            };
            tasks.add(task);
        }
        return tasks;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int index = i; // i cant be used directly because it's not final (dumb)
            Runnable task = () -> {
                SharedVector leftRowOrCol = leftMatrix.get(index);
                leftRowOrCol.negate();
            };
            tasks.add(task);
        }
        //System.out.println(tasks.size());
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        //System.out.println("CALLLED FOR TRANSPOSE %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        //System.out.println(leftMatrix.toString());
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int index = i; // i cant be used directly because it's not final (dumb)
            Runnable task = () -> {
                SharedVector leftRowOrCol = leftMatrix.get(index);
                leftRowOrCol.transpose();
            };
            tasks.add(task);
        }
        return tasks;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}
