package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        // Added by us
        this.vectors = new SharedVector[0];
        // Adding end
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        // Added by us
        loadRowMajor(matrix);
        // Adding end
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        // Added by us
        int rows = matrix.length;
        SharedVector[] newVectors = new SharedVector[rows];
        for (int i = 0; i < rows; i++) {
            // Each vector stores a row
            newVectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
        this.vectors = newVectors;
        // Adding end
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        // Added by us
        int rows = matrix.length;
        int cols = matrix[0].length;
        SharedVector[] newVectors = new SharedVector[cols];
        
        for (int j = 0; j < cols; j++) {
            double[] columnData = new double[rows];
            for (int i = 0; i < rows; i++) {
                columnData[i] = matrix[i][j];
            }
            // Each vector stores a column
            newVectors[j] = new SharedVector(columnData, VectorOrientation.COLUMN_MAJOR);
        }
        this.vectors = newVectors;
        // Adding end
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        // Added by us
        if (vectors.length == 0) {
            return new double[0][0];
        }
        int vecNum = vectors.length;
        int vecLen = vectors[0].length();
        
        if (getOrientation() == VectorOrientation.ROW_MAJOR) {
            double[][] result = new double[vecNum][vecLen];
            for (int i = 0; i < vecNum; i++) {
                for (int j = 0; j < vecLen; j++) {
                    result[i][j] = vectors[i].get(j);
                }
            }
            return result;
        } else {
            // If stored as Column Major, we must pivot back to Row Major
            double[][] result = new double[vecLen][vecNum];
            for (int j = 0; j < vecNum; j++) {
                for (int i = 0; i < vecLen; i++) {
                    result[i][j] = vectors[j].get(i);
                }
            }
            return result;
        }
        // Adding end
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        // Added by us
        return vectors[index];
        // Adding end
    }

    public int length() {
        // TODO: return number of stored vectors
        // Added by us
        return vectors.length;
        // Adding end
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        // Added by us
        if (vectors.length == 0) {
            return null;
        }
        else { 
            return vectors[0].getOrientation();        
        }
        // Adding end
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        // Added by us

        // Adding end
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        // Added by us
        for (SharedVector v : vecs) 
            { v.readLock(); }
        // Adding end
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        // Added by us
        for (SharedVector v : vecs) {
            v.writeLock();
        }
        // Adding end
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        // Added by us
        for (SharedVector v : vecs) 
            { v.readUnlock(); }
        // Adding end
    }
}
