package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    /////////////////////
    // try {} finally {} so if the code results in error the object wont stay locked forever
    /////////////////////

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        if (vector == null) {
            throw new IllegalArgumentException("given vector can't be null");
        }
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        readLock();
        double output;
        try {
            output = vector[index];
        } finally {
            readUnlock();
        }
        return output;
    }

    public int length() {// vecmult can make vector "change lengths"
        // TODO: return vector length
        readLock();
        try {
            return vector.length;
        } finally {
            readUnlock();
        }
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        readLock();
        try {
            return orientation;
        } finally {
            readUnlock();
        }
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        writeLock();
        try {
            if (orientation == VectorOrientation.ROW_MAJOR) {
                System.out.println("set to column major&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                orientation = VectorOrientation.COLUMN_MAJOR;
            } else {
                orientation = VectorOrientation.ROW_MAJOR;
            }
        } finally {
            writeUnlock();
        }
    }

    //might cause deadlock where 2 threads get the writing keys first then both wait forever to get the reading key
    // solution: make sure given the 2 vectors every thread tries to get the locks in the same order, so one of them is forced to wait
    public void add(SharedVector other) {
        // TODO: add two vectors
        if (this == other) { // same vector
            this.writeLock();
            try {
                for (int i = 0; i < vector.length; i++) {
                    vector[i] += vector[i];
                }
            }  finally {
                this.writeUnlock();
            }
        } else { // different vectors
            /// for deadlock
            SharedVector first = this;
            if (System.identityHashCode(this) < System.identityHashCode(other)) {
                first = other;
            }
            if (first == this) {
                this.writeLock();
                other.readLock();
            } else {
                other.readLock();
                this.writeLock();
            }
            ///
            try {
                for (int i = 0; i < vector.length; i++) {
                    vector[i] += other.vector[i];
                }
            } finally {
                if (first == this) {
                    other.readUnlock();
                    this.writeUnlock();
                } else {
                    this.writeUnlock();
                    other.readUnlock();
                }
            }
        }
    }

    public void negate() {
        // TODO: negate vector
        System.out.println("negating and what not");
        writeLock();
        try { 
            for (int i = 0; i < vector.length; i++) {
                vector[i] *= -1;
            }
        } finally {
            writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        if (this.length() != other.length()) {
            throw new IllegalArgumentException("The vectors arent of the same length!");
        }
        ///for deadlock
        SharedVector first = this;
        SharedVector second = other;
        if (System.identityHashCode(this) < System.identityHashCode(other)) {
            first = other;
            second = this;
        }
        ///
        first.readLock();
        second.readLock();
        double sum = 0;
        try {
            for (int i = 0; i < vector.length; i++) {
                sum += vector[i] * other.vector[i];
            }
        } finally {
            second.readUnlock();
            first.readUnlock();
        }
        return sum;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        /// boring checks
        if (orientation != VectorOrientation.ROW_MAJOR) {
            throw new IllegalArgumentException("Vector must be row major");
        }
        if (matrix.getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            if (matrix.length() > 0 && matrix.get(0).length() != length()) {
                System.out.println(toString());
                System.out.println(matrix.toString());
                throw new IllegalArgumentException("Can't multiply");
            }
            double[] result = new double[matrix.length()]; // 1 x m
            for (int i = 0; i < matrix.length(); i++) {
                result[i] = dot(matrix.get(i));
            }
            writeLock();
            try {
                this.vector = result;
            } finally {
                writeUnlock();
            }  
        } else { // row major 
            int numOfCols = matrix.get(0).length();
            if (matrix.length() > 0 && matrix.length() != length()) {
                System.out.println("vector length: " + length());
                System.out.println(toString());
                System.out.println("num of row: " + matrix.length());
                System.out.println("matrix orientation: " + matrix.getOrientation());
                System.out.println(matrix.toString());
                throw new IllegalArgumentException("Can't multiply");
            }
            double[] result = new double[numOfCols]; // 1 x m
            // forced to create columns manually :)
            for (int i = 0; i < numOfCols; i++) {
                double[] col = new double[matrix.length()];
                for (int j = 0; j < matrix.length(); j++) {
                    col[j] = matrix.get(j).get(i);
                    System.out.println("col[" + j + "] = " + col[j]);
                }
                SharedVector colVector = new SharedVector(col, VectorOrientation.COLUMN_MAJOR);
                System.out.println("dot with col " + i + " = " + dot(colVector));
                result[i] = dot(colVector);
            }
            writeLock();
            try {
                this.vector = result;
            } finally {
                writeUnlock();
            }
        }
    }


    //// DEKETE LATER
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < vector.length; i++) {
            sb.append(vector[i]);
            if (i < vector.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
