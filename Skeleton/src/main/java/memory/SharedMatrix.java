package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            vectors[i] = new SharedVector (matrix[i],VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        SharedVector[] newVectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            newVectors[i] = new SharedVector (matrix[i],VectorOrientation.ROW_MAJOR);
        }
        this.vectors = newVectors;
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        if (matrix.length != 0) { // not empty
            SharedVector[] newVectors = new SharedVector[matrix[0].length];
            for (int i = 0; i < matrix[0].length; i++) {
                double[] col = new double[matrix.length];
                for (int j = 0; j < matrix.length; j++) {
                    col[j] = matrix[j][i];
                }
                newVectors[i] = new SharedVector (col,VectorOrientation.COLUMN_MAJOR);
            }
            this.vectors = newVectors;
        }
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        if (vectors.length == 0) { // empty
            return new double[0][0];
        }
        VectorOrientation orientation = vectors[0].getOrientation();
        double[][] result;
        if (orientation == VectorOrientation.ROW_MAJOR) {
            int rows = vectors.length;
            int cols = vectors[0].length();
            result = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result[i][j] = vectors[i].get(j);
                }
            }
        } else {
            int rows = vectors[0].length();
            int cols = vectors.length;
            result = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result[i][j] = vectors[j].get(i);
                }
            }
        }
        return result;
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        return vectors[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        return vectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for (int i = 0; i < vecs.length; i++) {
            vecs[i].readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for (int i = vecs.length - 1; i >= 0; i--) {
            vecs[i].readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for (int i = 0; i < vecs.length; i++) {
            vecs[i].writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for (int i = vecs.length - 1; i >= 0; i--) {
            vecs[i].writeUnlock();
        }
    }

    ///// DELETE LATER
    public String toString() {
        String result;
        double [][] matrix = readRowMajor();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            sb.append("[");
            for (int j = 0; j < matrix[0].length; j++) {
                sb.append(matrix[i][j]);
                if (j < matrix[0].length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            if (i < matrix.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
