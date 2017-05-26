package murphytalk.algo;

import murphytalk.concurrent.ForkJoinMergeSort;
import murphytalk.test.StopWatch;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Compare the sorting (standard Tim sort and a home-made merge sort) performance in single thread and parallel mode.
 *
 * Run each test separately, otherwise tests run later will benefit from a warmed up JVM.
 *
 * Observations:
 * Averagely the parallel sorting are faster, but its standard deviation is doubled compared with single thread versions.
 * In other words : the performance of parallel sorting tends to be not as stable as single threaded sorting.
 */
public class TestSort {
    private static final int size = 1_000_000;
    private static final int repeat = 5;
    private static final Integer data [];
    private static final Integer sortedData [];

    private Integer toBeSorted [];

    static {
        //make sure this only runs once, so the runtime of each individual test are comparable.
        Object [] random = (new Random()).ints(size).boxed().toArray();
        data = Arrays.copyOf( random, random.length, Integer[].class);
        sortedData = data.clone();
        Arrays.sort(sortedData);
    }

    /* The vanilla array sort uses TimSort.
       For this 1 million random int test case, quite often this sort is the slowest
     */
    @Test
    public void testArraySort(){
        StopWatch.measure("testArraySort", ()-> {
            toBeSorted = data.clone();
            Arrays.sort(toBeSorted);
        },repeat);
        assertThat(sortedData,is(toBeSorted));
    }

    /* ForkJoin + TimSort
     */
    @Test
    public void testArrayParallelSort(){
         StopWatch.measure("testArrayParallelSort", ()-> {
            toBeSorted = data.clone();
            Arrays.parallelSort(toBeSorted);
        },repeat);
        assertThat(sortedData,is(toBeSorted));
    }

    /* Merge sort in single thread
     */
    @Test
    public void testMergeSort(){
        StopWatch.measure("testMergeSort", ()-> {
            final MergeSort mergeSort = new MergeSort(size);
            toBeSorted = data.clone();
            mergeSort.sort(toBeSorted);
        },repeat);
        assertThat(sortedData,is(toBeSorted));
    }

    /* Merge sort in single thread, Kotlin version
     */
    @Test
    public void testMergeSortKotlin(){
        StopWatch.measure("testMergeSortKotlin", ()-> {
            final Sort mergeSort = new murphytalk.algo.kotlin.MergeSort(size);
            toBeSorted = data.clone();
            mergeSort.sort(toBeSorted);
        },repeat);
        assertThat(sortedData,is(toBeSorted));
    }

    /* This leverages fork/join framework to parallelize the sorting.
       30% faster than testMergeSort() on my 8-core (logical) i7 6700K
    */
    @Test
    public void testMergeSortForkJoin(){
        StopWatch.measure("testMergeSortForkJoin", ()-> {
            toBeSorted = data.clone();
            final ForkJoinMergeSort sorter = new ForkJoinMergeSort(MergeSort::merge, toBeSorted,0,size);
            ForkJoinPool.commonPool().invoke(sorter);
        },repeat);
        assertThat(sortedData,is(toBeSorted));
    }

    @Test
    public void testMergeSortForkJoinKotlin(){
        StopWatch.measure("testMergeSortForkJoinKotlin", ()-> {
            toBeSorted = data.clone();
            final ForkJoinMergeSort sorter = new ForkJoinMergeSort(murphytalk.algo.kotlin.MergeSort.Companion::merge, toBeSorted,0,size);
            ForkJoinPool.commonPool().invoke(sorter);
        },repeat);
        assertThat(sortedData,is(toBeSorted));
    }
}
