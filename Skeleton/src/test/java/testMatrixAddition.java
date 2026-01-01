import java.util.Arrays;

import memory.SharedMatrix;
import memory.SharedVector;
import memory.VectorOrientation;

public class testMatrixAddition {

    public static void main(String[] args) {
        System.out.println("Checking Vector and Matrix functions!!!!!");
        negateTester();
        tranposeTester();
        addTester();
        dotTester();
        vecMultTester();
        //////////////////
        
        

    }

public static void loadColumnMajorTester() {
        System.out.println("Checking the loadColumnMajor Function in SharedMatrix");

        System.out.println("---Check 1 (standard)---");
        double[][] c1m1 = {{1.0, 1.0}, {1.0, 1.0}};
        SharedMatrix c1sm = new SharedMatrix(c1m1);
        double[][] c1m2 = {{9.0, 8.0}, {7.0, 6.0}};
        c1sm.loadColumnMajor(c1m2);
        double[][] c1res = c1sm.readRowMajor();
        if (Arrays.deepEquals(c1m2, c1res)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 2 (resizing matrix)---");
        double[][] c2m1 = {{1.0, 2.0}, {3.0, 4.0}};
        SharedMatrix c2sm = new SharedMatrix(c2m1);
        double[][] c2m2 = {{10.0, 20.0, 30.0}};
        c2sm.loadColumnMajor(c2m2);
        double[][] c2res = c2sm.readRowMajor();
        if (Arrays.deepEquals(c2m2, c2res)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 3 (loading empty matrix)---");
        double[][] c3m1 = {{5.0, 5.0}};
        SharedMatrix c3sm = new SharedMatrix(c3m1);
        double[][] c3m2 = {}; 
        c3sm.loadColumnMajor(c3m2);
        double[][] c3res = c3sm.readRowMajor();
        if (c3res.length == 0) { 
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 4 (verify orientation)---");
        double[][] c4m = {{1.1, 2.2}, {3.3, 4.4}};
        SharedMatrix c4sm = new SharedMatrix();
        c4sm.loadColumnMajor(c4m);
        if (c4sm.getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 5 (same matrix)---");
        double[][] c5m = {{1.0, 0.0}, {0.0, 1.0}};
        SharedMatrix c5sm = new SharedMatrix(c5m);
        c5sm.loadColumnMajor(c5m);
        double[][] c5res = c5sm.readRowMajor();
        if (Arrays.deepEquals(c5m, c5res)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
    }    

    public static void loadRowMajorTester() {
        System.out.println("Checking the loadRowMajor Function in SharedMatrix");

        System.out.println("---Check 1 (standard)---");
        double[][] c1m1 = {{1.0, 1.0}, {1.0, 1.0}};
        SharedMatrix c1sm = new SharedMatrix(c1m1);
        double[][] c1m2 = {{9.0, 8.0}, {7.0, 6.0}};
        c1sm.loadRowMajor(c1m2);
        double[][] c1res = c1sm.readRowMajor();
        if (Arrays.deepEquals(c1m2, c1res)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 2 (resizing matrix)---");
        double[][] c2m1 = {{1.0, 2.0}, {3.0, 4.0}};
        SharedMatrix c2sm = new SharedMatrix(c2m1);
        double[][] c2m2 = {{10.0}, {20.0}, {30.0}};
        c2sm.loadRowMajor(c2m2);
        double[][] c2res = c2sm.readRowMajor();
        if (Arrays.deepEquals(c2m2, c2res)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 3 (loading empty matrix)---");
        double[][] c3m1 = {{5.0, 5.0}};
        SharedMatrix c3sm = new SharedMatrix(c3m1);
        double[][] c3m2 = {}; 
        c3sm.loadRowMajor(c3m2);
        double[][] c3res = c3sm.readRowMajor();
        if (c3res.length == 0) { // check if empty
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 4 (verify orientation)---");
        double[][] c4m = {{1.1, 2.2}};
        SharedMatrix c4sm = new SharedMatrix();
        c4sm.loadRowMajor(c4m);
        if (c4sm.getOrientation() == VectorOrientation.ROW_MAJOR) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 5 (same matrix)---");
        double[][] c5m = {{1.0, 0.0}, {0.0, 1.0}};
        SharedMatrix c5sm = new SharedMatrix(c5m);
        c5sm.loadRowMajor(c5m);
        double[][] c5res = c5sm.readRowMajor();
        if (Arrays.deepEquals(c5m, c5res)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
    }    

    public static void readRowMajorTester () {
        System.out.println("Checking the readRowMajor Function in SharedMatrix");

        System.out.println("---Check 1 (row matrix)---");
        double[][] c1m = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        SharedMatrix c1sm = new SharedMatrix(c1m);
        double[][] c1res = c1sm.readRowMajor();
        if (Arrays.deepEquals(c1m, c1res)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 2 (column matrix)---");
        double[][] c2m = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        SharedMatrix c2sm = new SharedMatrix(c2m);
        for (int i = 0; i < c2sm.length(); i++) {
            c2sm.get(i).transpose();
        }
        double[][] c2res = c1sm.readRowMajor();
        double[][] c2a = {{1.0, 4.0, 7.0}, {2.0, 5.0, 8.0}, {3.0, 6.0, 9.0}};
        if (Arrays.equals(c2a, c2res)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 3 (empty matrix)---");
        double[][] c3m = {};
        SharedMatrix c3sm = new SharedMatrix(c3m);
        double[][] c3res = c3sm.readRowMajor();
        if (Arrays.equals(c3m, c3res)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
    }

    public static void getTester() {
        System.out.println("Checking the get function in SharedMatrix");

        System.out.println("---Check 1 (standard)---");
        double[][] c1m = {{1.0, 2.0}, {3.0, 4.0}};
        SharedMatrix c1sm = new SharedMatrix(c1m);
        SharedVector c1sv = c1sm.get(1);
        if (c1sv.get(0) == 3.0 && c1sv.get(1) == 4.0) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 2 (out of bounds, exception expected)---");
        try {
            c1sm.get(5);
            System.out.println("Fail... (didn't throw an exception)");
        } catch (Exception e) {
            System.out.println("Success! (threw an exception)");
        }
    }

    public static void getOrientationTester() {
        System.out.println("Checking the getOrientation function in SharedMatrix");

        System.out.println("---Check 1 (Row Major)---");
        double[][] c1m = {{1.0, 2.0}};
        SharedMatrix c1sm = new SharedMatrix(c1m);
        if (c1sm.getOrientation() == VectorOrientation.ROW_MAJOR) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 2 (empty matrix, exception expected)---");
        SharedMatrix c2sm = new SharedMatrix();
        try {
            c2sm.getOrientation();
            System.out.println("Fail... (didn't throw an exception)");
        } catch (Exception e) {
            System.out.println("Success! (threw an exception)");
        }
    }

    public static void lengthTester() {
        System.out.println("Checking the length function in SharedMatrix");

        System.out.println("---Check 1 (standard)---");
        double[][] c1m = {{1.0}, {2.0}, {3.0}};
        SharedMatrix c1sm = new SharedMatrix(c1m);
        if (c1sm.length() == 3) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 2 (empty)---");
        SharedMatrix c2sm = new SharedMatrix();
        if (c2sm.length() == 0) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
    }

    public static void vecMultTester() {
        System.out.println("Checking the vecMult function in SharedVector");

        System.out.println("---Check 1 (Row x Column Matrix)---");
        double[] c1v1 = {3.0, 6.0, 2.0};
        SharedVector c1sv1 = new SharedVector(c1v1, VectorOrientation.ROW_MAJOR); 
        double[][] c1m = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        SharedMatrix c1sm = new SharedMatrix(c1m);
        // manual transpose
        for (int i = 0; i < c1sm.length(); i++) {
            c1sm.get(i).transpose();
        }
        c1sv1.vecMatMul(c1sm);
        double[] c1a = {21.0, 54.0, 76.0};
        SharedVector c1sva = new SharedVector(c1a, VectorOrientation.ROW_MAJOR);
        String c1s1 = c1sva.toString();
        String c1sa = c1sva.toString();
        if (c1s1.equals(c1sa)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
        System.out.println("wanted: "+ c1sa);
        System.out.println("got: " + c1s1);

        System.out.println("---Check 2 (Row x Row Matrix)---");
        double[] c2v1 = {3.0, 6.0, 2.0};
        SharedVector c2sv1 = new SharedVector(c2v1, VectorOrientation.ROW_MAJOR); 
        double[][] c2m = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        SharedMatrix c2sm = new SharedMatrix(c2m);
        c2sv1.vecMatMul(c2sm);
        double[] c2a = {21.0, 54.0, 76.0};
        SharedVector c2sva = new SharedVector(c2a, VectorOrientation.ROW_MAJOR);
        String c2s1 = c2sva.toString();
        String c2sa = c2sva.toString();
        if (c2s1.equals(c2sa)) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
        System.out.println("wanted: "+ c2sa);
        System.out.println("got: " + c2s1);

        System.out.println("---Check 3 (invalid dimensions, exception expected)---");
        double[] c3v1 = {3.0, 6.0, 2.0};
        SharedVector c3sv1 = new SharedVector(c3v1, VectorOrientation.ROW_MAJOR); 
        double[][] c3m = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        SharedMatrix c3sm = new SharedMatrix(c3m);
        try {
            c3sv1.vecMatMul(c3sm);
            System.out.println("Failed... (didn't throw an exception)");
        } catch (Exception e) {
            System.out.println("Success! (threw an exception");
        }
    }

    public static void dotTester() {
        System.out.println("Checking the dot function in SharedVector");

        System.out.println("---Check 1 (standard)---");
        double[] c1v1 = {3.0, 8.0, 1.0};
        double[] c1v2 = {5.0, 2.0, 5.0};
        SharedVector c1sv1 = new SharedVector(c1v1, VectorOrientation.ROW_MAJOR); 
        SharedVector c1sv2 = new SharedVector(c1v2, VectorOrientation.ROW_MAJOR);
        double c1a = 3*5 + 8*2 + 1*5;
        double c1res = c1sv1.dot(c1sv2);
        if (c1res == c1a) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
        System.out.println("wanted: " + c1a);
        System.out.println("got: " + c1res);

        System.out.println("---Check 2 (different orientations)---");
        double[] c2v1 = {3.0, 8.0, 1.0};
        double[] c2v2 = {5.0, 2.0, 5.0};
        SharedVector c2sv1 = new SharedVector(c2v1, VectorOrientation.COLUMN_MAJOR); 
        SharedVector c2sv2 = new SharedVector(c2v2, VectorOrientation.ROW_MAJOR);
        double c2a = 3*5 + 8*2 + 1*5;
        double c2res = c2sv1.dot(c2sv2);
        if (c2res == c2a) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
        System.out.println("wanted: " + c2a);
        System.out.println("got: " + c2res);

        System.out.println("---Check 3 (empty vectors)---");
        double[] c3v1 = {};
        double[] c3v2 = {};
        SharedVector c3sv1 = new SharedVector(c3v1, VectorOrientation.COLUMN_MAJOR); 
        SharedVector c3sv2 = new SharedVector(c3v2, VectorOrientation.ROW_MAJOR);
        double c3a = 0;
        double c3res = c3sv1.dot(c3sv2);
        if (c3res == c3a) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
        System.out.println("wanted: " + c3a);
        System.out.println("got: " + c3res);

        System.out.println("---Check 4 (different lengths, error expected)---");
        double[] c4v1 = {4.0, 2.0, 0.0};
        double[] c4v2 = {6.0, 7.0, 4.0, 1.0};
        SharedVector c4sv1 = new SharedVector(c4v1, VectorOrientation.COLUMN_MAJOR); 
        SharedVector c4sv2 = new SharedVector(c4v2, VectorOrientation.ROW_MAJOR);
        try {
            c4sv1.dot(c4sv2);
            System.out.println("Failed... (didn't throw an error)");
        } catch (Exception e) {
            System.out.println("Success! (threw an exception)");
        }
    }

    public static void addTester() {
        System.out.println("Checking the add function in SharedVector");

        System.out.println("---Check 1 (standard, row vectors)---");
        double[] c1v1 = {1.0, 2.0};
        double[] c1v2 = {5.0, 6.0};
        double[] c1a = {6.0,8.0};
        SharedVector c1sv1 = new SharedVector(c1v1, VectorOrientation.ROW_MAJOR); 
        SharedVector c1sv2 = new SharedVector(c1v2, VectorOrientation.ROW_MAJOR);
        SharedVector c1sva = new SharedVector(c1a, VectorOrientation.ROW_MAJOR);
        c1sv1.add(c1sv2);
        if (c1sv1.toString().equals(c1sva.toString())) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 2 (different lengths, exception expected)---");
        double[] c2v1 = {1.0, 2.0};
        double[] c2v2 = {5.0, 6.0, 2.0};
        SharedVector c2sv1 = new SharedVector(c2v1, VectorOrientation.ROW_MAJOR); 
        SharedVector c2sv2 = new SharedVector(c2v2, VectorOrientation.ROW_MAJOR);
        try {
            c2sv1.add(c2sv2);
            System.out.println("Fail... (didn't throw an exception)");
        } catch (Exception e) {
            System.out.println("Success! (threw an exception)");
        }
        

        System.out.println("---Check 3 (empty vectors)---");
        double[] c3v1 = {};
        SharedVector c3sv1 = new SharedVector(c3v1, VectorOrientation.ROW_MAJOR); 
        SharedVector c3sv2 = new SharedVector(c3v1, VectorOrientation.ROW_MAJOR);
        c3sv1.add(c3sv2);
        if (c3sv1.toString().equals(c3sv2.toString())) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 4 (column vectors)---");
        double[] c4v1 = {3.0, 1.0};
        double[] c4v2 = {5.0, 2.0};
        double[] c4a = {8.0,3.0};
        SharedVector c4sv1 = new SharedVector(c4v1, VectorOrientation.COLUMN_MAJOR); 
        SharedVector c4sv2 = new SharedVector(c4v2, VectorOrientation.COLUMN_MAJOR);
        SharedVector c4sva = new SharedVector(c4a, VectorOrientation.COLUMN_MAJOR);
        c4sv1.add(c4sv2);
        if (c4sv1.toString().equals(c4sva.toString())) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 5 (different orientations)---");
        double[] c5v1 = {3.0, 1.0};
        double[] c5v2 = {5.0, 2.0};
        double[] c5a = {8.0,3.0};
        SharedVector c5sv1 = new SharedVector(c5v1, VectorOrientation.COLUMN_MAJOR); 
        SharedVector c5sv2 = new SharedVector(c5v2, VectorOrientation.ROW_MAJOR);
        SharedVector c5sva = new SharedVector(c5a, VectorOrientation.COLUMN_MAJOR);
        c5sv1.add(c5sv2);
        if (c5sv1.toString().equals(c5sva.toString())) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
    }

    public static void tranposeTester() {
        System.out.println("Checking the transpose function in SharedVector");

        System.out.println("---Check 1 (row -> column)---");
        double[] c1v1 = {1.0,2.0,3.0};
        SharedVector c1sv1 = new SharedVector(c1v1, VectorOrientation.ROW_MAJOR);
        c1sv1.transpose();
        if (c1sv1.getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 2 (column -> row)---");
        double[] c2v1 = {1.0,2.0,3.0};
        SharedVector c2sv1 = new SharedVector(c2v1, VectorOrientation.COLUMN_MAJOR);
        c2sv1.transpose();
        if (c2sv1.getOrientation() == VectorOrientation.ROW_MAJOR) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
    }

    public static void negateTester() {
        System.out.println("Checking the negate function in SharedVector");

        System.out.println("---Check 1 (standard)---");
        double[] c1v1 = {1.0,-2.0,3.0};
        SharedVector c1sv1 = new SharedVector(c1v1, VectorOrientation.ROW_MAJOR);
        double[] c1a = {-1.0,2.0,-3.0};
        SharedVector c1sva = new SharedVector(c1a, VectorOrientation.ROW_MAJOR);
        c1sv1.negate();
        if (c1sv1.toString().equals(c1sva.toString())) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }

        System.out.println("---Check 2 (empty vector)---");
        double[] c2v1 = {};
        SharedVector c2sv1 = new SharedVector(c2v1, VectorOrientation.COLUMN_MAJOR);
        double[] c2a = {};
        SharedVector c2sva = new SharedVector(c2a, VectorOrientation.ROW_MAJOR);
        c2sv1.negate();
        if (c2sv1.toString().equals(c2sva.toString())) {
            System.out.println("Success!");
        } else {
            System.out.println("Fail...");
        }
    }
}