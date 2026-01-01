import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import scheduling.TiredThread;

public class threadingTests {
    
    public static void main(String[] args) {
        
    }


    public void testCompareTo() {
        TiredThread thread1 = new TiredThread(1, 1.0);
        TiredThread thread2 = new TiredThread(1, 1.0);
        thread1.start(); 
        thread2.start();
        thread1.newTask(() -> {
            try { 
                Thread.sleep(50); 
            } catch (InterruptedException e) {}
        });
        thread1.newTask(() -> {
            try { 
                Thread.sleep(100); 
            } catch (InterruptedException e) {}
        });
        while(thread1.isBusy() || thread2.isBusy()) { 
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
        }
        if (thread1.getFatigue() < thread2.getFatigue()) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

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
        thread.shutdown();
        thread.join(1000);
        if (!thread.isAlive()) {
            System.out.println("Success! (thread terminated)");
        } else {
          System.out.println("Fail... (thread is still running)");  
        }
    }

}
