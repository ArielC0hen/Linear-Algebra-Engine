import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import memory.SharedMatrix;
import parser.ComputationNode;
import scheduling.TiredExecutor;
import scheduling.TiredThread;
import spl.lae.LinearAlgebraEngine;

public class testEngine {
    
    public static void main(String[] args) throws InterruptedException {
        shutdownTester();
        newTaskTester();
        fatigueCalculationTester();
        compareToTester();  
        /////////////  
        submitAllTester();
    }

    public void testMatrixAddition() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

        System.out.println("---Check 1 (addition)---");

        System.out.println("standard");
        double[][] c1m1 = {{1, 2}, {3, 4}};
        double[][] c1m2 = {{5, 6}, {7, 8}};

        // 2. Build Leaf Nodes
        ComputationNode c1cn1 = new ComputationNode(c1m1);
        ComputationNode c1cn2 = new ComputationNode(c1m2);
        List<ComputationNode> c1children = Arrays.asList(c1cn1, c1cn2);
        ComputationNode root = new ComputationNode("ADD", c1children);
        leafA.setMatrix(matrixA); // Assuming you have a setter for leaf values
        
        ComputationNode leafB = new ComputationNode("MATRIX", new ArrayList<>());
        leafB.setMatrix(matrixB);

        // 3. Build Root Node (ADD)
        List<ComputationNode> children = Arrays.asList(c1cn1, c1cn2);
        ComputationNode root = new ComputationNode("ADD", children);

        // 4. Run and Verify
        ComputationResult result = engine.loadAndCompute(root);
        SharedMatrix resMat = result.getMatrix();

        // Expected: [[6, 8], [10, 12]]
        assertEquals(6.0, resMat.get(0).get(0), 0.001);
        assertEquals(12.0, resMat.get(1).get(1), 0.001);
    }

}
