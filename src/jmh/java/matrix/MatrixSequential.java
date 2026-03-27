package matrix;

import java.util.Random;

public class MatrixSequential {
    private static final int N = 3;

    public static void main(final String[] args) {
        final double[][] a = randomMatrix(N);
        final double[][] b = randomMatrix(N);
        final double[][] c = multiplySequential(a, b);

        System.out.println("Matrix A:");
        printMatrix(a);

        System.out.println("\nMatrix B:");
        printMatrix(b);

        System.out.println("\nMatrix C = A x B:");
        printMatrix(c);
    }

    static double[][] multiplySequential(double[][] a, double[][] b) {
        final int n = a.length;
        final double[][] c = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0.0;

                for (int k = 0; k < n; k++) {
                    sum += a[i][k] * b[k][j];
                }

                c[i][j] = sum;
            }
        }

        return c;
    }

    static double[][] randomMatrix(final int n) {
        final Random random = new Random(42);

        final double[][] matrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = random.nextInt(10);
            }
        }

        return matrix;
    }

    static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double value : row) {
                System.out.printf("%8.2f", value);
            }
            System.out.println();
        }
    }
}
