import java.util.Arrays;
import java.util.List;
import parser.ComputationNode;
import parser.ComputationNodeType;
import spl.lae.LinearAlgebraEngine;

public class testEngine {
    
    public static void main(String[] args) throws InterruptedException {
        testMatrixAddition();
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
    }
}
