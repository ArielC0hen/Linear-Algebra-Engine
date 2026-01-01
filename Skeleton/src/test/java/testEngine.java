import java.util.Arrays;
import java.util.List;
import parser.ComputationNode;
import parser.ComputationNodeType;
import spl.lae.LinearAlgebraEngine;

public class testEngine {
    
    public static void main(String[] args) throws InterruptedException {
        testMatrixAddition();
        //testMatrixMultiplication();
    }

    public static void testMatrixMultiplication() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(3);

        System.out.println("---Check 2 (multiplication)---");

        System.out.println("standard 2x2");
        double[][] m1 = {{1, 2}, {3, 4}};
        double[][] m2 = {{5, 6}, {7, 8}};
        ComputationNode cn1 = new ComputationNode(m1);
        ComputationNode cn2 = new ComputationNode(m2);
        List<ComputationNode> children = Arrays.asList(cn1, cn2);
        ComputationNode r = new ComputationNode(ComputationNodeType.MULTIPLY, children);
        double[][] res = engine.run(r).getMatrix();
        double[][] expected = {{19, 22}, {43, 50}};
        if (Arrays.deepEquals(res, expected)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("multiply by zero matrix");
        double[][] m3 = {{1, 2}, {3, 4}};
        double[][] m4 = {{0, 0}, {0, 0}};
        ComputationNode cn3 = new ComputationNode(m3);
        ComputationNode cn4 = new ComputationNode(m4);
        List<ComputationNode> children2 = Arrays.asList(cn3, cn4);
        ComputationNode r2 = new ComputationNode(ComputationNodeType.MULTIPLY, children2);
        double[][] res2 = engine.run(r2).getMatrix();
        double[][] expected2 = {{0, 0}, {0, 0}};
        if (Arrays.deepEquals(res2, expected2)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("non square multiplication");
        double[][] m5 = {{1, 2, 3}, {4, 5, 6}};
        double[][] m6 = {{7, 8}, {9, 10}, {11, 12}};
        ComputationNode cn5 = new ComputationNode(m5);
        ComputationNode cn6 = new ComputationNode(m6);
        List<ComputationNode> children3 = Arrays.asList(cn5, cn6);
        ComputationNode r3 = new ComputationNode(ComputationNodeType.MULTIPLY, children3);
        double[][] res3 = engine.run(r3).getMatrix();
        double[][] expected3 = {{58, 64}, {139, 154}};
        if (Arrays.deepEquals(res3, expected3)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("different dimensions (exception expected)");
        double[][] m7 = {{1, 2}, {3, 4}};
        double[][] m8 = {{5, 6, 7}, {8, 9, 10}};
        ComputationNode cn7 = new ComputationNode(m7);
        ComputationNode cn8 = new ComputationNode(m8);
        List<ComputationNode> children4 = Arrays.asList(cn7, cn8);
        ComputationNode r4 = new ComputationNode(ComputationNodeType.MULTIPLY, children4);
        double[][] res4 = engine.run(r4).getMatrix();
        System.out.println("^THERE SHOULD BE AN EXCEPTION HERE^");
    }

    public static void testMatrixAddition() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(3);

        System.out.println("---Check 1 (addition)---");

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
        System.out.println("^THERE SHOULD BE AN EXCEPTION HERE^");

        System.out.println("1 argument (exception expected)");
        double[][] c4m1 = {{1, 2}, {3, 4}};
        ComputationNode c4cn1 = new ComputationNode(c4m1);
        List<ComputationNode> c4children = Arrays.asList(c4cn1);
        ComputationNode c4r = new ComputationNode(ComputationNodeType.ADD, c4children);
        try {
            double[][] c4res = engine.run(c4r).getMatrix();
            System.out.println("Fail.. (no exception thrown");
        } catch (Exception e) {
            System.out.println("Success! (exception thrown");
        }
        System.out.println("^THERE SHOULD BE AN EXCEPTION HERE^");   
    }
}
