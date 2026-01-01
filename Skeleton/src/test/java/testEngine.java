import java.util.Arrays;
import java.util.List;
import parser.ComputationNode;
import parser.ComputationNodeType;
import spl.lae.LinearAlgebraEngine;

public class testEngine {
    
    public static void main(String[] args) throws InterruptedException {
        testMatrixNegate();
        testMatrixAddition();
        testMatrixMultiplication();
        testMatrixTranspose();
    }

    public static void testMatrixTranspose() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(3);

        System.out.println("---Check 1 (transpose)---");

        System.out.println("standard");
        double[][] c1m1 = {{1, 2}, {3, 4}};
        ComputationNode c1cn1 = new ComputationNode(c1m1);
        List<ComputationNode> c1children = Arrays.asList(c1cn1);
        ComputationNode c1r = new ComputationNode(ComputationNodeType.TRANSPOSE, c1children);
        double[][] c1res = engine.run(c1r).getMatrix();
        double[][] c1a = {{1, 3}, {2, 4}};
        if (Arrays.deepEquals(c1res, c1a)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("empty matrix");
        double[][] c2m1 = {{}};
        ComputationNode c2cn1 = new ComputationNode(c2m1);
        List<ComputationNode> c2children = Arrays.asList(c2cn1);
        ComputationNode c2r = new ComputationNode(ComputationNodeType.TRANSPOSE, c2children);
        double[][] c2res = engine.run(c2r).getMatrix();
        double[][] c2a = {{}};
        if (Arrays.deepEquals(c2res, c2a)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("non-square matrix");
        double[][] c3m1 = {{1, 2, 3}, {4, 5, 6}};
        ComputationNode c3cn1 = new ComputationNode(c3m1);
        List<ComputationNode> c3children = Arrays.asList(c3cn1);
        ComputationNode c3r = new ComputationNode(ComputationNodeType.TRANSPOSE, c3children);
        double[][] c3res = engine.run(c3r).getMatrix();
        double[][] c3a = {{1, 4}, {2, 5}, {3, 6}};
        if (Arrays.deepEquals(c3res, c3a)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("2 arguments (exception expected)");
        double[][] c4m1 = {{1, 2}, {3, 4}};
        double[][] c4m2 = {{5, 6}, {7, 8}};
        ComputationNode c4cn1 = new ComputationNode(c4m1);
        ComputationNode c4cn2 = new ComputationNode(c4m2);
        List<ComputationNode> c4children = Arrays.asList(c4cn1, c4cn2);
        ComputationNode c4r = new ComputationNode(ComputationNodeType.TRANSPOSE, c4children);
        try {
            double[][] c4res = engine.run(c4r).getMatrix();
            System.out.println("Fail.. (no exception thrown)");
        } catch (Exception e) {
            System.out.println("Success! (exception thrown)");
        }
    }


    public static void testMatrixNegate() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(3);

        System.out.println("---Check 2 (negate)---");

        System.out.println("standard");
        double[][] c1m1 = {{1, 2}, {3, 4}};
        ComputationNode c1cn1 = new ComputationNode(c1m1);
        List<ComputationNode> c1children = Arrays.asList(c1cn1);
        ComputationNode c1r = new ComputationNode(ComputationNodeType.NEGATE, c1children);
        double[][] c1res = engine.run(c1r).getMatrix();
        double[][] c1a = {{-1, -2}, {-3, -4}};
        if (Arrays.deepEquals(c1res, c1a)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("empty matrix");
        double[][] c2m1 = {{}};
        ComputationNode c2cn1 = new ComputationNode(c2m1);
        List<ComputationNode> c2children = Arrays.asList(c2cn1);
        ComputationNode c2r = new ComputationNode(ComputationNodeType.NEGATE, c2children);
        double[][] c2res = engine.run(c2r).getMatrix();
        double[][] c2a = {{}};
        if (Arrays.deepEquals(c2res, c2a)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("2 arguments (exception expetced)");
        double[][] c3m1 = {{1, 2}, {3, 4}};
        double[][] c3m2 = {{5, 6}, {7, 8}};
        ComputationNode c3cn1 = new ComputationNode(c3m1);
        ComputationNode c3cn2 = new ComputationNode(c3m2);
        List<ComputationNode> c3children = Arrays.asList(c3cn1, c3cn2);
        ComputationNode c3r = new ComputationNode(ComputationNodeType.NEGATE, c3children);
        try {
            double[][] c3res = engine.run(c3r).getMatrix();
            System.out.println("Fail.. (no exception thrown)");
        } catch (Exception e) {
            System.out.println("Success! (exception thrown)");
        } ;
    }

    public static void testMatrixMultiplication() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(3);

        System.out.println("---Check 4 (multiplication)---");

        System.out.println("standard 2x2");
        double[][] c1m1 = {{1, 2}, {3, 4}};
        double[][] c1m2 = {{5, 6}, {7, 8}};
        ComputationNode c1cn1 = new ComputationNode(c1m1);
        ComputationNode c1cn2 = new ComputationNode(c1m2);
        List<ComputationNode> c1children = Arrays.asList(c1cn1, c1cn2);
        ComputationNode c1r = new ComputationNode(ComputationNodeType.MULTIPLY, c1children);
        double[][] c1res = engine.run(c1r).getMatrix();
        double[][] c1a = {{19, 22}, {43, 50}};
        if (Arrays.deepEquals(c1res, c1a)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("multiply by zero matrix");
        double[][] c2m1 = {{1, 2}, {3, 4}};
        double[][] c2m2 = {{0, 0}, {0, 0}};
        ComputationNode c2cn1 = new ComputationNode(c2m1);
        ComputationNode c2cn2 = new ComputationNode(c2m2);
        List<ComputationNode> c2children = Arrays.asList(c2cn1, c2cn2);
        ComputationNode c2r = new ComputationNode(ComputationNodeType.MULTIPLY, c2children);
        double[][] c2res = engine.run(c2r).getMatrix();
        double[][] c2a = {{0, 0}, {0, 0}};
        if (Arrays.deepEquals(c2res, c2a)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("non square multiplication");
        double[][] c3m1 = {{1, 2, 3}, {4, 5, 6}};
        double[][] c3m2 = {{7, 8}, {9, 10}, {11, 12}};
        ComputationNode c3cn1 = new ComputationNode(c3m1);
        ComputationNode c3cn2 = new ComputationNode(c3m2);
        List<ComputationNode> c3children = Arrays.asList(c3cn1, c3cn2);
        ComputationNode c3r = new ComputationNode(ComputationNodeType.MULTIPLY, c3children);
        double[][] c3res = engine.run(c3r).getMatrix();
        double[][] c3a = {{58, 64}, {139, 154}};
        if (Arrays.deepEquals(c3res, c3a)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("wrong dimensions (exception expected)");
        double[][] c4m1 = {{1, 2}, {3, 4}};
        double[][] c4m2 = {{5, 6, 7}, {8, 9, 10}};
        ComputationNode c4cn1 = new ComputationNode(c4m1);
        ComputationNode c4cn2 = new ComputationNode(c4m2);
        List<ComputationNode> c4children = Arrays.asList(c4cn1, c4cn2);
        ComputationNode c4r = new ComputationNode(ComputationNodeType.MULTIPLY, c4children);
        double[][] c4res = engine.run(c4r).getMatrix();
        System.out.println("^THERE SHOULD BE AN EXCEPTION HERE^");

        System.out.println("1 argument (exception expected)");
        double[][] c5m1 = {{1, 2}, {3, 4}};
        ComputationNode c5cn1 = new ComputationNode(c5m1);
        List<ComputationNode> c5children = Arrays.asList(c5cn1);
        ComputationNode c5r = new ComputationNode(ComputationNodeType.MULTIPLY, c5children);
        try {
            double[][] c5res = engine.run(c5r).getMatrix();
            System.out.println("Fail.. (no exception thrown)");
        } catch (Exception e) {
            System.out.println("Success! (exception thrown)");
        } 
    }

    public static void testMatrixAddition() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(3);

        System.out.println("---Check 3 (addition)---");

        System.out.println("standard");
        double[][] c1m1 = {{1, 2}, {3, 4}};
        double[][] c1m2 = {{5, 6}, {7, 8}};
        ComputationNode c1cn1 = new ComputationNode(c1m1);
        ComputationNode c1cn2 = new ComputationNode(c1m2);
        List<ComputationNode> c1children = Arrays.asList(c1cn1, c1cn2);
        ComputationNode c1r = new ComputationNode(ComputationNodeType.ADD, c1children);
        double[][] c1res = engine.run(c1r).getMatrix();
        double[][] c1a = {{6, 8}, {10, 12}};
        if (Arrays.deepEquals(c1res, c1a)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("empty matrices");
        double[][] c2m1 = {{}};
        double[][] c2m2 = {{}};
        ComputationNode c2cn1 = new ComputationNode(c2m1);
        ComputationNode c2cn2 = new ComputationNode(c2m2);
        List<ComputationNode> c2children = Arrays.asList(c2cn1, c2cn2);
        ComputationNode c2r = new ComputationNode(ComputationNodeType.ADD, c2children);
        double[][] c2res = engine.run(c2r).getMatrix();
        double[][] c2a = {{}};
        if (Arrays.deepEquals(c2res, c2a)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("different dimensions (exception expected)");
        double[][] c3m1 = {{1, 2}, {3, 4}};
        double[][] c3m2 = {{5, 6, 7}, {8, 9, 10}};
        ComputationNode c3cn1 = new ComputationNode(c3m1);
        ComputationNode c3cn2 = new ComputationNode(c3m2);
        List<ComputationNode> c3children = Arrays.asList(c3cn1, c3cn2);
        ComputationNode c3r = new ComputationNode(ComputationNodeType.ADD, c3children);
        double[][] c3res = engine.run(c3r).getMatrix();
        System.out.println("^THERE SHOULD BE AN EXCEPTION HERE^"); // can't catch exceptions in different threads :(

        System.out.println("1 argument (exception expected)");
        double[][] c4m1 = {{1, 2}, {3, 4}};
        ComputationNode c4cn1 = new ComputationNode(c4m1);
        List<ComputationNode> c4children = Arrays.asList(c4cn1);
        ComputationNode c4r = new ComputationNode(ComputationNodeType.ADD, c4children);
        try {
            double[][] c4res = engine.run(c4r).getMatrix();
            System.out.println("Fail.. (no exception thrown)");
        } catch (Exception e) {
            System.out.println("Success! (exception thrown)");
        }  
    }
}
