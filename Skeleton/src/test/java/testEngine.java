import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import memory.SharedMatrix;
import parser.ComputationNode;
import parser.ComputationNodeType;
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
    

    public static void submitAllTester() throws InterruptedException {
        System.out.println("Testing submitAll in TiredExecutor");
        TiredExecutor executor = new TiredExecutor(3);
        long startTime = System.currentTimeMillis();
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < 3; i++) {
            Runnable task = () -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            };
            tasks.add(task);
        }
        executor.submitAll(tasks);
        long duration = System.currentTimeMillis() - startTime;
        if (duration >= 100) {
            System.out.println("Success! (waited for the tasks to finish)");
        } else {
            System.out.println("Fail... (didn't wait for the tasks to finish)");
        }
        executor.shutdown();
    }


    public static void newTaskTester() throws InterruptedException {
        System.out.println("Testing newTask in TiredThread");
        TiredThread thread = new TiredThread(1, 1.0);
        thread.start();
        AtomicBoolean taskRan = new AtomicBoolean(false);
        thread.newTask(() -> {
            taskRan.set(true);
        });
        Thread.sleep(100); 
        if (taskRan.get()) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail..");
        }  
        thread.shutdown();
        thread.join();
    }

    public static void compareToTester() throws InterruptedException {
        System.out.println("Testing compareTo in TiredThread");
        TiredThread thread1 = new TiredThread(1, 1.0);
        TiredThread thread2 = new TiredThread(2, 1.0);
        thread1.start(); 
        thread2.start();
        thread1.newTask(() -> {
            try { 
                Thread.sleep(30); 
            } catch (InterruptedException e) {}
        });
        thread2.newTask(() -> {
            try { 
                Thread.sleep(150); 
            } catch (InterruptedException e) {}
        });
        while(!thread1.isBusy() && !thread2.isBusy()) { 
            Thread.sleep(20);
        }
        while(thread1.isBusy() || thread2.isBusy()) { 
            Thread.sleep(20);
        }
        Thread.sleep(100);
        if (thread1.compareTo(thread2) < 0) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
        thread1.shutdown();
        thread2.shutdown();
        thread1.join();
        thread2.join();
    }

    public static void fatigueCalculationTester() throws InterruptedException {
        System.out.println("Testing fatigue calculation in TiredThread");
        TiredThread thread = new TiredThread(1, 1.0);
        thread.start();
        double initFatigue = thread.getFatigue();        
        thread.newTask(() -> {
            try { 
                Thread.sleep(50); 
            } catch (InterruptedException e) {}
        });  
        while (!thread.isBusy()) {
            Thread.sleep(10);
        }
        while (thread.isBusy()) {
            Thread.sleep(10);
        }
        if (thread.getFatigue() > initFatigue) {
            System.out.println("Success! (fatigue increased)");
        } else {
            System.out.println("Fail... (fatigue stayed the same after use)");
        }
        thread.shutdown();
        thread.join();
    }

    public static void shutdownTester() throws InterruptedException {
        System.out.println("Testing shutdown in TiredThread");
        TiredThread thread = new TiredThread(1, 1.0);
        thread.start(); 
        thread.shutdown();
        thread.join(1000);
        if (!thread.isAlive()) {
            System.out.println("Success! (thread terminated)");
        } else {
          System.out.println("Fail... (thread is still running)");  
        }
    }

}
