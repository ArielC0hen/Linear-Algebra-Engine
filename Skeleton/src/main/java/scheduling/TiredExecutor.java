package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        workers = new TiredThread[numThreads];
        for (int i = 0; i < numThreads; i++) {

            workers[i] = new TiredThread(i, 0);
            workers[i].start();
            idleMinHeap.add(workers[i]);
        }
    }

    public void submit(Runnable task) {
        // TODO
        try {
            //System.out.println("Banana");
            TiredThread first = idleMinHeap.take();
            //System.out.println("apple");
            inFlight.incrementAndGet();
            Runnable wrappedTask = () -> { // so we can track when the task finishes
                try {
                    task.run();
                } finally {
                    inFlight.decrementAndGet();
                    idleMinHeap.add(first);
                }
            };
            //System.out.println("kiwi");
            first.newTask(wrappedTask);
        } catch (InterruptedException e) {
            //System.out.println("interupted submitting");
            Thread.currentThread().interrupt();
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        //System.out.println("called submit all");
        for (Runnable task : tasks) {
            //System.out.println("running submit");
            submit(task);
            //System.out.println("finished running submit");
        }
        while (inFlight.get() > 0) {  // haven't finished executing all yet 
            //System.out.println("all tasks haven't finished");
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        //System.out.println("finished submit all");
    }

    public void shutdown() throws InterruptedException {
        // TODO
        for (TiredThread worker : workers) {
            worker.shutdown();
        }
        for (TiredThread worker : workers) { // forces the main thread to wait until every thread finishes shutting down
            worker.join();
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        String output = "\n";
        for (int i = 0; i < workers.length; i++) {
            output+= "----Worker " + workers[i].getWorkerId() + "---- \n" + 
            "fatigue = " + workers[i].getFatigue() + "\n" +
            "time used = " + workers[i].getTimeUsed()+ "\n" + 
            "time idle = " + workers[i].getTimeIdle() + "\n" +
            "is busy? = " + workers[i].isBusy() + "\n";
        }
        return output;
    }
}
