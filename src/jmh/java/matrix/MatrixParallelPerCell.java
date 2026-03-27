package matrix;

import java.util.Random;

public class MatrixParallelPerCell {
    private static final int N = 3;

    public static void main(String[] args) {
        double[][] a = randomMatrix(N);
        double[][] b = randomMatrix(N);
        double[][] c = multiplyParallelPerCell(a, b);

        System.out.println("Result:");
        printMatrix(c);
    }

    static double[][] multiplyParallelPerCell(final double[][] a, final double[][] b) {
        final int n = a.length;
        final double[][] c = new double[n][n];

        final Thread[][] workers = new Thread[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                final int row = i;
                final int col = j;

                workers[i][j] = new Thread(() -> {
                    double sum = 0.0;
                    for (int k = 0; k < n; k++) {
                        sum += a[row][k] * b[k][col];
                    }

                    c[row][col] = sum;
                });
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                workers[i][j].start();
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                try {
                    workers[i][j].join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread was interrupted", e);
                }
            }
        }

        return c;
    }

    static double[][] randomMatrix(int n) {
        Random random = new Random(42);

        double[][] matrix = new double[n][n];
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
