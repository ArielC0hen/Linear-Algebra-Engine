import static org.junit.jupiter.api.Assertions.assertTrue;

import scheduling.TiredThread;

public class threadingTests {
    
    public static void main(String[] args) {
        
    }



    public void testFatigueCalculation() throws InterruptedException {
        TiredThread thread = new TiredThread(1, 1.0);
        thread.start();

        double initialFatigue = thread.getFatigue();
        
        thread.newTask(() -> {
            try { 
                Thread.sleep(50); 
            } catch (InterruptedException e) {}
        });

        // Wait for task to finish [cite: 393, 394]
        while(thread.isBusy()) { Thread.onSpinWait(); }

        assertTrue(thread.getFatigue() > initialFatigue, "Fatigue should increase after work");
        thread.shutdown();
        thread.join();
    }
}
