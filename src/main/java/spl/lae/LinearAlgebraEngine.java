package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        this.executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        return null;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
    ComputationNodeType type = node.getNodeType();
    List<ComputationNode> operands = node.getChildren();

    // 1. Initial Load of M1
    leftMatrix.loadRowMajor(operands.get(0).getMatrix());
    List<Runnable> tasks;

    // 2. Decide operation using if-else instead of switch
    if (type == ComputationNodeType.ADD) {
        rightMatrix.loadRowMajor(operands.get(1).getMatrix());
        tasks = createAddTasks();
    } 
    else if (type == ComputationNodeType.MULTIPLY) {
        rightMatrix.loadColumnMajor(operands.get(1).getMatrix());
        tasks = createMultiplyTasks();
    } 
    else if (type == ComputationNodeType.NEGATE) {
        tasks = createNegateTasks();
    } 
    else if (type == ComputationNodeType.TRANSPOSE) {
        tasks = createTransposeTasks();
    } 
    else {
        throw new IllegalArgumentException("Unknown operation type");
    }

    // 3. Execution
    executor.submitAll(tasks);
    node.resolve(leftMatrix.readRowMajor()); 
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
        return null;
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
