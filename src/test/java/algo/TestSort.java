package algo;


import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by murphytalk on 12/10/2016.
 */
public class TestSort {
    private static final int size = 1_000_000;
    private static final Integer data [];
    private static final Integer sortedData [];

    private Integer toBeSorted [];

    static {
        Object [] random = (new Random()).ints(size).boxed().toArray();
        data = Arrays.copyOf( random, random.length, Integer[].class);
        sortedData = data.clone();
        Arrays.sort(sortedData);
    }

    @Before
    public void setup(){
        toBeSorted = data.clone();
    }

    @Test
    public void testMergeSort(){
        MergeSort mergeSort = new MergeSort(size);
        mergeSort.sort(toBeSorted);
        assertThat(sortedData,is(toBeSorted));
    }

    @Test
    public void testMergeSort2(){
        Integer d1 [] = sortedData.clone();
        assertThat(sortedData,is(d1));
    }
}
