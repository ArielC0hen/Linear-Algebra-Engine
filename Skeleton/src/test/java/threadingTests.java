import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import scheduling.TiredThread;

public class threadingTests {
    
    public static void main(String[] args) {
        
    }



    public void testFatigueCalculation() throws InterruptedException {
        TiredThread thread = new TiredThread(1, 1.0);
        thread.start();
        double initFatigue = thread.getFatigue();        
        thread.newTask(() -> {
            try { 
                Thread.sleep(50); 
            } catch (InterruptedException e) {}
        });        
        while(thread.isBusy()) { 
            Thread.sleep(3); 
        }
        if (thread.getFatigue() > initFatigue) {
            System.out.println("Success! (fatigue increased)");
        } else {
            System.out.println("Fail... (fatigue stayed the same after use)");
        }
        thread.shutdown();
        thread.join();
    }

    public void testShutdown() throws InterruptedException {
        TiredThread thread = new TiredThread(1, 1.0);
        thread.start(); 
        thread.shutdown(); // Sends POISON_PILL [cite: 390]
        thread.join(1000);
        if (thread.isAlive()) {
            
        } else {
            
        }
        assertFalse(thread.isAlive(), "Thread should terminate after shutdown");
    }

}
