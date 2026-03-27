package matrix;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include("MatrixBenchmark")
                .warmupIterations(1)
                .measurementIterations(2)
                .forks(1)
                .addProfiler("gc")
                .build();

        new Runner(opt).run();
    }
}
