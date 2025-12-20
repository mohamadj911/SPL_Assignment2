package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        // Added by us
        executor = new TiredExecutor(numThreads);
        // Adding end
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        // Added by us
        if (computationRoot == null){
            return null;
        }

        // 2. Restructure the tree for left-associativity FIRST [cite: 88, 124]
        computationRoot.associativeNesting();

        // If the root is already a matrix node, return it directly
        if (computationRoot.getNodeType() == ComputationNodeType.MATRIX) {
            return computationRoot;
        }
        ComputationNode resolvable = null;

        // Loop to find and resolve resolvable nodes
        while ((resolvable = computationRoot.findResolvable()) != null) {
            loadAndCompute(resolvable);
    }
        return computationRoot;
        // Adding end
    }   

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        // Added by us

        // Get operands
        List<ComputationNode> operands = node.getChildren();
        
        // Load first operand into M1
        leftMatrix.loadRowMajor(operands.get(0).getMatrix());
        // Prepare task list
        List<Runnable> tasks = new ArrayList<>();
        
        // Choose tasks based on operator
        switch (node.getNodeType()) {
            // Load second operand into M2 & create tasks
            case ADD -> {
                // Load second operand into M2
                rightMatrix.loadRowMajor(operands.get(1).getMatrix());
                // Create addition tasks
                tasks = createAddTasks();
            }
            case MULTIPLY -> {
                // M2 is loaded as Column Major for efficient dot products
                rightMatrix.loadColumnMajor(operands.get(1).getMatrix());
                // Create multiplication tasks
                tasks = createMultiplyTasks();
            }
            case NEGATE -> tasks = createNegateTasks();
            case TRANSPOSE -> tasks = createTransposeTasks();
        }

        // Submit batch and block until finished
        executor.submitAll(tasks);

        // Read result back from M1 into the node
        node.resolve(leftMatrix.readRowMajor());
    // Adding ends
    }


    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        // Added by us
    
        List<Runnable> tasks = new ArrayList<>();
        // For each row, create a task to add corresponding rows
        // from leftMatrix and rightMatrix
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int rowIndex = i;
            tasks.add(() -> {
                // Get the corresponding rows
                SharedVector v1 = leftMatrix.get(rowIndex);
                SharedVector v2 = rightMatrix.get(rowIndex);
                // Perform addition with proper locking:
                // We are modifying v1
                v1.writeLock(); 
                // We are only reading v2
                v2.readLock();  
                try {
                    v1.add(v2);
                } finally {
                    v1.writeUnlock();
                    v2.readUnlock();
                }
            });
        }
        return tasks;
        // Adding end
        }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        // Added by us
        List<Runnable> tasks = new ArrayList<>();
        // For each row in leftMatrix, create a task to multiply it
        // with the entire rightMatrix
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int rowIndex = i;
            // Each task multiplies one row of leftMatrix with rightMatrix
            tasks.add(() -> {
                // Acquire write lock on the row being modified
                SharedVector rowV = leftMatrix.get(rowIndex);
                // Lock the row for writing
                rowV.writeLock();
                try {
                    // This updates the row in-place by dotting it with M2's columns
                    rowV.vecMatMul(rightMatrix);
                } finally {
                    // Release write lock
                    rowV.writeUnlock();
                }
        });
    }

    return tasks;
    // Adding end
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        // Added by us
        List<Runnable> tasks = new ArrayList<>();
        int rows = leftMatrix.length();
        // For each row, create a task to negate it
        for (int i = 0; i < rows; i++) {
            final int rowIndex = i;
            // Each task negates one row
            tasks.add(() -> {
                // Get the row to negate
                SharedVector row = leftMatrix.get(rowIndex);
                // Acquire write lock before negating
                row.writeLock();
                try {
                    // Negate the vector
                    row.negate();
                } finally {
                    // Release write lock
                    row.writeUnlock();
                }
            });
        }
        return tasks;
        // Adding end
    }


    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        // Added by us
        List<Runnable> tasks = new ArrayList<>();
            // For each row, create a task to transpose it
            for (int i = 0; i < leftMatrix.length(); i++) {
                final int rowIndex = i;
                // Each task transposes one row
                tasks.add(() -> {
                    SharedVector v = leftMatrix.get(rowIndex);
                    // Acquire write lock before transposing
                    v.writeLock();
                    try {
                        // Transpose the vector
                        v.transpose();
                    } finally {
                        // Release write lock
                        v.writeUnlock();
                    }
                });
            }
            return tasks;
        // Adding end
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        // Added by us
        return executor.getWorkerReport();  
        // Adding end
      }
}