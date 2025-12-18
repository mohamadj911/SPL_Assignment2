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
        executor=new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        if (computationRoot == null){
            return null;
        }
       if(leftMatrix==null && rightMatrix==null)
       {
        return computationRoot;
       }
       if (computationRoot.getRight() != null) {
        run(computationRoot.getRight());
    }
        if (computationRoot.getLeft() != null) {
            run(computationRoot.getLeft());
        }
        loadAndCompute(computationRoot);
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        return null;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        return null;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
         List<Runnable> tasks = new ArrayList<>();

    int rows = leftMatrix.length();
    for (int i = 0; i < rows; i++) {
        final int rowIndex = i;
        tasks.add(() -> {
            SharedVector row = leftMatrix.get(rowIndex);
            row.negate(); 
        });
    }

    return tasks;
}


    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        return null;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return null;
    }
}