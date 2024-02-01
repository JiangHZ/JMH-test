package org.jianghz;

import com.google.common.collect.Sets;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@Threads(4)
public class BenchmarkTest {

    public List<Integer> list1 = new ArrayList<>(10000);
    public List<Integer> list2 = new ArrayList<>(10000);

    @Setup
    public void initParams(){
        //分别给list1和list2生成大量随机数
        for (int i = 0; i < 10000; i++) {
            list1.add((int) (Math.random() * 10000000));
            list2.add((int) (Math.random() * 10000000));
        }
    }
    @Benchmark
    public void set1() {
        new ArrayList<>(Sets.intersection(new HashSet<>(list1), new HashSet<>(list2)));
    }

    @Benchmark
    public void set2() {
        HashSet<Integer> set1 = new HashSet<>(list1);
        HashSet<Integer> set2 = new HashSet<>(list2);
        set2.removeIf(integer -> !set1.contains(integer));
        new ArrayList<>(set2);
    }

    @Benchmark
    public void set3() {
        HashSet<Integer> set2 = new HashSet<>(list2);
        list1.parallelStream()
                .filter(set2::contains)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws Exception {
        Options opts = new OptionsBuilder()
                .include(BenchmarkTest.class.getSimpleName())
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opts).run();
    }
}
