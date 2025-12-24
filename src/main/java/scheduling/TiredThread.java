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
       // Added by us
       // Check if worker is alive
       if (!alive.get()) {
            throw new IllegalStateException("Worker not alive");
        }
        
        // Atomically reserve the worker
        if (!busy.compareAndSet(false, true)) {
            throw new IllegalStateException("Worker already busy.");
        }

        // Place task in the handoff slot
        if (!handoff.offer(task)) {
            // Revert reservation if queue is full
            busy.set(false); 
            throw new IllegalStateException("Handoff queue rejected task.");
        }
         // Adding end
    }

    /**
     * Request this worker to stop after finishing current task.
     * Inserts a poison pill so the worker wakes up and exits.
     */
    public void shutdown() {
       // TODO
       // Added by us
       // Signal the worker to stop
       alive.set(false);
        // Ensure the thread isn't stuck waiting for a task
        handoff.offer(POISON_PILL);
        // Interrupt in case it's blocked on take()
        this.interrupt();
        // Adding end
    }

    @Override
    public void run() {
       // TODO
       // Added by us
       // Main worker loop
       try {
            // Continue while alive
            while (alive.get()) {
                // Wait for a task
                Runnable task;
                try {
                    // Block until a task is offered
                    task = handoff.take();
                    // If interrupted, check alive status
                } catch (InterruptedException e) {
                    // if not, skip to next iteration
                    if (!alive.get()) {
                        break;
                    }
                    continue;
                }

                // REPLACEMENT LOGIC: Use if-else instead of break
                if (task == POISON_PILL) {
                    // Signal the loop to stop after this iteration
                    alive.set(false); 
                } else {
                // Track Idle Time
                long now = System.nanoTime();
                // Update total idle time
                timeIdle.addAndGet(now - idleStartTime.get());

                // // Execute Task
                // busy.set(true);
                // Measure execution time
                long start = System.nanoTime();
                try {
                    // Run the assigned task
                    task.run();
                } catch (Throwable t) {
                    // Log but don't kill the worker
                    t.printStackTrace(); 
                } finally {
                    // Track Work Time
                    long elapsed = System.nanoTime() - start;
                    // Update total time used
                    timeUsed.addAndGet(elapsed);
                    // Mark worker as idle
                    busy.set(false);
                    // Reset idle start time
                    idleStartTime.set(System.nanoTime()); 
                }
              }
            }
        } finally {
            // Ensure worker is marked not busy on exit
            alive.set(false);
            // Mark as not busy
            busy.set(false);
        }
         // Adding end
    }

    @Override
    public int compareTo(TiredThread o) {
        // TODO
        // Added by us
        // Calculate and compare fatigue (Fatigue Factor * Time Used)
        double myFatigue = this.getFatigue();
        double otherFatigue = o.getFatigue();

        int cmp = Double.compare(myFatigue, otherFatigue);
        
        // If fatigue is exactly equal, cheack the ID
        if (cmp != 0) {
            return cmp;
        }
    // If fatigue is equal, compare by ID to ensure consistent ordering
    return Integer.compare(this.id, o.id);

    }
}