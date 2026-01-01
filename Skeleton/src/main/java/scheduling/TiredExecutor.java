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
            double fatigueFactor = 0.5 + Math.random();
            workers[i] = new TiredThread(i, fatigueFactor);
            workers[i].start();
            idleMinHeap.add(workers[i]);
        }
    }

    public void submit(Runnable task) {
        // TODO
        try {
            TiredThread first = idleMinHeap.take();
            inFlight.incrementAndGet();
            Runnable wrappedTask = () -> { // so we can track when the task finishes
                try {
                    task.run();
                } finally {
                    inFlight.decrementAndGet();
                    idleMinHeap.add(first);
                    //synchronized (inFlight) {
                    //    inFlight.notifyAll();
                    //}
                }
            };
            first.newTask(wrappedTask);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for (Runnable task : tasks) {
            submit(task);
        }
        //synchronized (inFlight) {
            while (inFlight.get() > 0) {  // haven't finished executing all yet 
                try {
                    //inFlight.wait();;
                    sle
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        //}
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
        double totalFatigue = 0;
        for (int i = 0; i < workers.length; i++) {
            output+= "----Worker " + workers[i].getWorkerId() + "---- \n" + 
            "fatigue = " + workers[i].getFatigue() + "\n" +
            "time used = " + workers[i].getTimeUsed()+ "\n" + 
            "time idle = " + workers[i].getTimeIdle() + "\n" +
            "is busy? = " + workers[i].isBusy() + "\n";
            totalFatigue += workers[i].getFatigue();
        }
        double averageFatigue = totalFatigue / workers.length;
        double fairness = 0;
        double delta = 0;
        for (int i = 0; i < workers.length; i++) {
            delta = workers[i].getFatigue() - averageFatigue;
            fairness += Math.pow(delta, 2);
        }
        output += "--Fairness: " + fairness;
        return output;
    }
}
