package scheduling;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TiredThread extends Thread implements Comparable<TiredThread> {

    private static final Runnable POISON_PILL = () -> {}; // Special task to signal shutdown

    private final int id; // Worker index assigned by the executor
    private final double fatigueFactor; // Multiplier for fatigue calculation

    private final AtomicBoolean alive = new AtomicBoolean(true); // Indicates if the worker should keep running

    // Single-slot handoff queue; executor will put tasks here
    private final BlockingQueue<Runnable> handoff = new ArrayBlockingQueue<>(1);

    private final AtomicBoolean busy = new AtomicBoolean(false); // Indicates if the worker is currently executing a task

    private final AtomicLong timeUsed = new AtomicLong(0); // Total time spent executing tasks
    private final AtomicLong timeIdle = new AtomicLong(0); // Total time spent idle
    private final AtomicLong idleStartTime = new AtomicLong(0); // Timestamp when the worker became idle

    public TiredThread(int id, double fatigueFactor) {
        this.id = id;
        this.fatigueFactor = fatigueFactor;
        this.idleStartTime.set(System.nanoTime());
        setName(String.format("FF=%.2f", fatigueFactor));
    }

    public int getWorkerId() {
        return id;
    }

    public double getFatigue() {
        return fatigueFactor * timeUsed.get();
    }

    public boolean isBusy() {
        return busy.get();
    }

    public long getTimeUsed() {
        return timeUsed.get();
    }

    public long getTimeIdle() {
        return timeIdle.get();
    }

    /**
     * Assign a task to this worker.
     * This method is non-blocking: if the worker is not ready to accept a task,
     * it throws IllegalStateException.
     */
    public void newTask(Runnable task) {
        // TODO
        try {
            handoff.put(task); // blocks until there’s space
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while submitting task");
        }
    }

    /**
     * Request this worker to stop after finishing current task.
     * Inserts a poison pill so the worker wakes up and exits.
     */
    public void shutdown() {
       // TODO
        alive.set(false);
        try {
            handoff.put(POISON_PILL);
        } catch (InterruptedException e) { // at some point while waiting to add poison pill we're interupted by something else
            Thread.currentThread().interrupt(); // with alive = false it will stop run()
        }
    }

    @Override
    public void run() {
       // TODO
        while (alive.get()) {
            try {
                long idleStart = System.nanoTime();
                Runnable task = handoff.take();
                long idleEnd = System.nanoTime();
                timeIdle.getAndAdd(idleEnd - idleStart);
                if (task == POISON_PILL) {
                    break;
                }
                busy.set(true);
                long usedStart = System.nanoTime();
                task.run();
                long usedEnd = System.nanoTime();
                System.out.println("updated time used");
                timeUsed.getAndAdd(usedEnd - usedStart);                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                busy.set(false);;
            }
        }
    }

    @Override
    public int compareTo(TiredThread o) {
        // TODO
        //the priority queue in TiredExecutor orders by fatigue
        return Double.compare(getFatigue(), o.getFatigue());
    }
}