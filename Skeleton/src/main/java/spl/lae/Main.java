package spl.lae;
import java.io.IOException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
      System.out.println("BDIKA");
      if (args.length != 3) {
        throw new IllegalArgumentException("Wrong format!!!!!!!! Please use: <number of threads> <path/to/input/file> <path/to/output/file>");
      }
      int numThreads = 1;
      try {
        numThreads = Integer.parseInt(args[0]);
      } catch (Exception e) {
        throw new IllegalArgumentException("Argument 1 should be a number");
      }
      String inputPath = args[1];
      String outputPath = args[2];
      System.out.println("Hello");
      InputParser parser = new InputParser();
      LinearAlgebraEngine engine = new LinearAlgebraEngine(numThreads);
      try {
        ComputationNode root = parser.parse(inputPath);
        ComputationNode resultNode = engine.run(root);
        double[][] result = resultNode.getMatrix();
        System.out.println(engine.getWorkerReport());
        OutputWriter.write(result, outputPath);
      } catch (Exception e) {
        OutputWriter.write(e.getMessage(), outputPath);
      }
      System.out.println("DONE");
    }
}