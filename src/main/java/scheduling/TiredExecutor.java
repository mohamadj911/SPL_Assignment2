package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);
    // Collect exceptions thrown by worker tasks
    private final ConcurrentLinkedQueue<Throwable> taskExceptions = new ConcurrentLinkedQueue<>();

    public TiredExecutor(int numThreads) {
        // Validate thread count
        if (numThreads < 1) {
            throw new IllegalArgumentException("numThreads must be >= 1");
        }
        // Create workers with random fatigue factors between 0.5 and 1.5
        workers = new TiredThread[numThreads];
        // Initialize and start workers
        for (int i = 0; i < numThreads; i++) {
            double fatigueFactor = 0.5 + Math.random();
            // Create worker
            TiredThread worker = new TiredThread(i, fatigueFactor);
            // Store worker
            workers[i] = worker;
            // Add to idle heap and start
            idleMinHeap.add(worker);
            // Start worker thread
            worker.start();
        }
    }

    public void submit(Runnable task) {
        // TODO
        // Added by us
        // Submit a single task to the least fatigued idle worker
        if (task == null) {
            throw new NullPointerException("task");
        }

        // Take least fatigued idle worker
        try {
            while (true) {
                TiredThread worker = idleMinHeap.take();

                // Wrap task to reinsert worker into idle heap upon completion
                Runnable wrapped = () -> {
                    // Execute original task
                    try {
                        // Run the actual task
                        task.run();
                    } catch (Throwable t) {
                        // Capture any task exception for later reporting
                        taskExceptions.add(t);
                        throw t;
                    } finally {
                        // Reinsert worker into idle heap
                        idleMinHeap.add(worker);
                        // Decrement in-flight count and notify if zero
                        int left = inFlight.decrementAndGet();
                        if (left == 0) {
                            // Notify waiting threads
                            synchronized (this) {
                                // Notify all waiting threads
                                this.notifyAll();
                            }
                        }
                    }
                };

                // Try to assign the task; if worker cannot accept it, try next worker
                try {
                    worker.newTask(wrapped);
                    // Only increment in-flight after successful assignment
                    inFlight.incrementAndGet();
                    break; // assigned successfully
                } catch (IllegalStateException ise) {
                    // Worker could not accept the task right now; put it back if alive and try another
                    if (worker.isAlive()) {
                        idleMinHeap.add(worker);
                    }
                    // small busy-wait backoff to avoid tight loop
                    Thread.yield();
                    continue;
                }
            }
        // Handle interruption
        } catch (InterruptedException e) {
            // Restore interrupt status
            Thread.currentThread().interrupt();
        }
        // Adding end
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        // Added by us
        if (tasks == null) {
            throw new NullPointerException("tasks");
        }
        // Submit each task
        for (Runnable task : tasks) {
            submit(task);
        }
        // Wait until all tasks are done
        synchronized (this) {
            // Wait while there are in-flight tasks
            while (inFlight.get() > 0) {
                try {
                    // Wait for notification
                    this.wait();
                  // Handle interruption
                } catch (InterruptedException e) {
                    // Restore interrupt status
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        // If any task threw an exception, surface it to the caller
        if (!taskExceptions.isEmpty()) {
            Throwable first = taskExceptions.peek();
            throw new RuntimeException("One or more tasks failed during execution", first);
        }
        // Adding end
    }

    public void shutdown() throws InterruptedException {
        // TODO
        // Added by us
        // Shutdown all workers and wait for them to finish
        for (TiredThread worker : workers) {
            worker.shutdown();
        }
        // Wait for all workers to terminate
        for (TiredThread worker : workers) {
            worker.join();
        }
        // Adding end
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        // Added by us
        // Build report header
        String report = "";
        // Header lines
        report = report + "=== TiredExecutor Worker Report ===\n";
        report = report + String.format("%-8s | %-12s | %-12s | %-14s | %-6s\n",
                "Worker", "timeUsed(ns)", "timeIdle(ns)", "fatigue", "busy");
        report = report + "---------------------------------------------------------------\n";

        // Aggregate statistics
        long totalUsed = 0;
        long totalIdle = 0;
        double minFat = Double.POSITIVE_INFINITY;
        double maxFat = Double.NEGATIVE_INFINITY;

        // Per-worker stats
        for (TiredThread w : workers) {
            long used = w.getTimeUsed();
            long idle = w.getTimeIdle();
            double fat = w.getFatigue();
            // Update aggregates
            totalUsed = totalUsed + used;
            totalIdle = totalIdle + idle;
            minFat = Math.min(minFat, fat);
            maxFat = Math.max(maxFat, fat);
            // Append worker line
            report = report + String.format("%-8d | %-12d | %-12d | %-14.2f | %-6s\n",
                    w.getWorkerId(), used, idle, fat, w.isBusy());
        }
        // Append totals
        report = report + "---------------------------------------------------------------\n";
        report = report + String.format("Totals: used=%d ns, idle=%d ns\n", totalUsed, totalIdle);
        report = report + String.format("Fatigue range: min=%.2f , max=%.2f\n", minFat, maxFat);
        report = report + String.format("In-flight tasks right now: %d\n", inFlight.get());

        return report;
        // Adding end

    }
}