package matrix;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 2, time = 1)
@Fork(1)
@State(Scope.Thread)
public class MatrixBenchmark {

    @Param({"1000"})
    public int n;

    @Param({"1", "10"})
    public int threads;

    private double[][] a;
    private double[][] b;

    @Setup(Level.Trial)
    public void setup() {
        a = randomMatrix(n);
        b = randomMatrix(n);
    }

    @Benchmark
    public void sequential(Blackhole bh) {
        bh.consume(multiplySequential(a, b));
    }

    @Benchmark
    public void parallelRows(Blackhole bh) {
        bh.consume(multiplyParallelRows(a, b, threads));
    }

    @Benchmark
    public void parallelCells(Blackhole bh) {
        bh.consume(multiplyParallelPerCell(a, b, threads));
    }

    static double[][] multiplySequential(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];

        new Thread()

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

    static double[][] multiplyParallelRows(double[][] a, double[][] b, int threads) {
        int n = a.length;
        double[][] c = new double[n][n];

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            final int row = i;
            futures.add(pool.submit(() -> {
                for (int j = 0; j < n; j++) {
                    double sum = 0.0;
                    for (int k = 0; k < n; k++) {
                        sum += a[row][k] * b[k][j];
                    }
                    c[row][j] = sum;
                }
            }));
        }

        waitAll(futures);
        pool.shutdown();
        return c;
    }

    static double[][] multiplyParallelPerCell(double[][] a, double[][] b, int threads) {
        int n = a.length;
        double[][] c = new double[n][n];

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                final int row = i;
                final int col = j;
                futures.add(pool.submit(() -> {
                    double sum = 0.0;
                    for (int k = 0; k < n; k++) {
                        sum += a[row][k] * b[k][col];
                    }
                    c[row][col] = sum;
                }));
            }
        }

        waitAll(futures);
        pool.shutdown();
        return c;
    }

    static void waitAll(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
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
}
