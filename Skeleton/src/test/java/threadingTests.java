import static org.junit.jupiter.api.Assertions.assertTrue;

import scheduling.TiredThread;

public class threadingTests {
    
    public static void main(String[] args) {
        
    }



    public void testFatigueCalculation() throws InterruptedException {
        // Fatigue factor is random 0.5-1.5 in specification, 
        // but current implementation constructor uses provided factor [cite: 325, 382]
        TiredThread thread = new TiredThread(1, 1.0);
        thread.start();

        double initialFatigue = thread.getFatigue();
        
        // Run a dummy task to increase timeUsed
        thread.newTask(() -> {
            try { Thread.sleep(50); } catch (InterruptedException e) {}
        });

        // Wait for task to finish [cite: 393, 394]
        while(thread.isBusy()) { Thread.onSpinWait(); }

        assertTrue(thread.getFatigue() > initialFatigue, "Fatigue should increase after work");
        thread.shutdown();
        thread.join();
    }
}
