package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        // Added by us 
        this.vector = vector;
        this.orientation = orientation;
        // Adding end
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        // Added by us 
        readLock();
        try {
            return vector[index];
        } finally {
            readUnlock();
        }
        // Adding end
    }

    public int length() {
        // TODO: return vector length
        // Added by us 
        return vector.length;
        // Adding end
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        // Added by us 
        return this.orientation;
        // Adding end
    }

    public void writeLock() {
        // TODO: acquire write lock
        // Added by us 
        lock.writeLock().lock();
        // Adding end
    }

    public void writeUnlock() {
        // TODO: release write lock
        // Added by us 
        lock.writeLock().unlock();
        // Adding end
    }

    public void readLock() {
        // TODO: acquire read lock
        // Added by us 
        lock.readLock().lock();
        // Adding end
    }

    public void readUnlock() {
        // TODO: release read lock
        // Added by us  
        lock.readLock().unlock();
        // Adding end
    }

    public void transpose() {
        // TODO: transpose vector
        // Added by us 
        if (this.orientation == VectorOrientation.ROW_MAJOR) { 
            // Switch to column-major
            this.orientation = VectorOrientation.COLUMN_MAJOR;
        } else {
            // Switch to row-major
            this.orientation = VectorOrientation.ROW_MAJOR;
        }
        // Adding end
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        // Added by us 
        for (int i = 0; i < this.vector.length; i++) {
            // Element-wise addition
            this.vector[i] = this.vector[i] + other.get(i);
        }
        // Adding end
    }

    public void negate() {
        // TODO: negate vector
        // Added by us 
        for (int i = 0; i < this.vector.length; i++) {
            // Element-wise negation
            this.vector[i] = -this.vector[i];
        }
        // Adding end
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        // Added by us 
        // Defensive check for mismatched lengths
        if (this.length() != other.length()) {
            throw new IllegalArgumentException("Vector length mismatch: " + this.length() + " vs " + other.length());
        }
        double sum = 0.0;
        for (int i = 0; i < this.vector.length; i++) {
            // Element-wise multiplication and accumulation
            sum += this.vector[i] * other.get(i);
        }
        // Adding end
        return sum;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        // Added by us 
        // Defensive checks to avoid ArrayIndexOutOfBounds and provide clearer errors
        if (matrix.length() == 0) {
            this.vector = new double[0];
            this.orientation = VectorOrientation.ROW_MAJOR;
            return;
        }
        if (matrix.get(0).length() != this.length()) {
            throw new IllegalArgumentException("Vector length " + this.length() + " does not match matrix column length " + matrix.get(0).length());
        }
        double[] result = new double[matrix.length()];
        for (int i = 0; i < matrix.length(); i++) {
            SharedVector column = matrix.get(i);
            if (column.length() != this.length()) {
                throw new IllegalArgumentException("Matrix column " + i + " length " + column.length() + " does not match vector length " + this.length());
            }
            // Dot product of this vector with each column of the matrix
            result[i] = this.dot(column);
        }
        this.vector = result;
        this.orientation = VectorOrientation.ROW_MAJOR;
        // Adding end
        }
    }

