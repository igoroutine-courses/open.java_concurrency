package matrix;

import java.util.Random;

public class MatrixParallel {
    private static final int N = 3;

    public static void main(String[] args) {
        int threads = Runtime.getRuntime().availableProcessors();

        double[][] a = randomMatrix(N);
        double[][] b = randomMatrix(N);
        double[][] c = multiplyParallel(a, b, threads);

        System.out.println("Threads: " + threads);

        System.out.println("Matrix A:");
        printMatrix(a);

        System.out.println("\nMatrix B:");
        printMatrix(b);

        System.out.println("\nMatrix C = A x B:");
        printMatrix(c);
    }

    static double[][] multiplyParallel(final double[][] a, final double[][] b, final int threads) {
        final int n = a.length;
        final double[][] c = new double[n][n];

        final int actualThreads = Math.min(threads, n);
        final Thread[] workers = new Thread[actualThreads];

        final int rowsPerThread = n / actualThreads;
        final int extraRows = n % actualThreads;

        int startRow = 0;
        for (int t = 0; t < actualThreads; t++) {
            int blockSize = rowsPerThread + (t < extraRows ? 1 : 0);
            int from = startRow;
            int to = startRow + blockSize;
            startRow = to;

            workers[t] = new Thread(() -> {
                for (int i = from; i < to; i++) {
                    for (int j = 0; j < n; j++) {
                        double sum = 0.0;

                        for (int k = 0; k < n; k++) {
                            sum += a[i][k] * b[k][j];
                        }

                        c[i][j] = sum;
                    }
                }
            });
        }

        for (Thread worker : workers) {
            worker.start();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted", e);
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
