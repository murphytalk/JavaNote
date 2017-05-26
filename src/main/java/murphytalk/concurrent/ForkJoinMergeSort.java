package murphytalk.concurrent;

import murphytalk.algo.MergeSort;
import murphytalk.algo.Sort;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

/**
 *  Parallel version of merge sort
 */
public class ForkJoinMergeSort extends RecursiveAction {
    protected final Comparable[] data;
    protected final Comparable[] work;
    protected final int begin, end;
    private final Sort.Merge mergeFunc;

    public ForkJoinMergeSort(Sort.Merge mergeFunc, Comparable[] data, int begin, int end) {
        this.data = data;
        this.begin = begin;
        this.end = end;
        this.work = new Comparable[end-begin];
        this.mergeFunc = mergeFunc;
    }

    public int size(){
        return end - begin;
    }

    protected void merge(int mid){
        mergeFunc.merge(data,begin,end,mid,work);
    }

    protected void compute() {
        final int size = size();
        if(size<MergeSort.SMALL_ENOUGH){
            Arrays.sort(data,begin,end);
        }
        else{
            final int mid = begin + (size >>> 1 );
            ForkJoinMergeSort left  = new ForkJoinMergeSort(mergeFunc, data, begin, mid);
            ForkJoinMergeSort right = new ForkJoinMergeSort(mergeFunc, data, mid,end);
            invokeAll(left,right);
            merge(mid);
        }
    }
}
