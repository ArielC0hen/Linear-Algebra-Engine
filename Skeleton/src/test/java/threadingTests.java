import java.util.concurrent.atomic.AtomicBoolean;

import scheduling.TiredThread;

public class threadingTests {
    
    public static void main(String[] args) {
        
    }

    public void testNewTaskExecution() throws InterruptedException {
        TiredThread thread = new TiredThread(1, 1.0);
        thread.start();
        AtomicBoolean taskRan = new AtomicBoolean(false);
        thread.newTask(() -> {
            taskRan.set(true);
        });
        Thread.sleep(100); 

        assertTrue(taskRan.get(), "The task submitted via newTask should have executed.");
        
        thread.shutdown();
        thread.join();
    }

    public void testCompareTo() throws InterruptedException {
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
        while(thread1.isBusy() || thread2.isBusy()) { 
            Thread.sleep(20);
        }
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

    public void testFatigueCalculation() throws InterruptedException {
        System.out.println("Testing fatigue calculation in TiredThread");
        TiredThread thread = new TiredThread(1, 1.0);
        thread.start();
        double initFatigue = thread.getFatigue();        
        thread.newTask(() -> {
            try { 
                Thread.sleep(50); 
            } catch (InterruptedException e) {}
        });        
        while(thread.isBusy()) { 
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

    public void testShutdown() throws InterruptedException {
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
