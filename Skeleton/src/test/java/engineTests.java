import memory.SharedMatrix;
import memory.SharedVector;
import scheduling.TiredExecutor;;


public void testSimpleAddition() {
    LinearAlgebraEngine engine = new LinearAlgebraEngine(2);

    // Create Matrix A: [[1, 2], [3, 4]]
    double[][] dataA = {{1.0, 2.0}, {3.0, 4.0}};
    SharedMatrix matrixA = new SharedMatrix(dataA);

    // Create Matrix B: [[5, 6], [7, 8]]
    double[][] dataB = {{5.0, 6.0}, {7.0, 8.0}};
    SharedMatrix matrixB = new SharedMatrix(dataB);

    // Build the tree
    ComputationNode nodeA = new ComputationNode(ComputationNodeType.MATRIX, matrixA);
    ComputationNode nodeB = new ComputationNode(ComputationNodeType.MATRIX, matrixB);
    ComputationNode addNode = new ComputationNode(ComputationNodeType.ADD);
    addNode.addChild(nodeA);
    addNode.addChild(nodeB);

    // Run
    ComputationResult result = engine.loadAndCompute(addNode);
    SharedMatrix resultMat = result.getMatrix();

    // Verify: Result should be [[6, 8], [10, 12]]
    assertEquals(6.0, resultMat.get(0).get(0));
    assertEquals(12.0, resultMat.get(1).get(1));
}