package murphytalk.algo;

import java.util.Arrays;

public class MergeSort extends AbstractSort{
    public final static int SMALL_ENOUGH = 16;
    final Comparable[] workBuffer;

    /**
     * Initialize a MergeSort
     * @param size size of the elements to sort
     */
    public MergeSort(int size){
        workBuffer = new Comparable[size];
    }

    @Override
    public void sort(Comparable[] objects, int begin, int end) {
        final int size = end-begin;
        if(size<SMALL_ENOUGH){
            Arrays.sort(objects,begin,end);
        }
        else{
            final int mid = begin + (end - begin) / 2;
            sort(objects,begin,mid);
            sort(objects,mid ,end);
            merge(objects,begin,end,mid,workBuffer);
        }
    }

    /**
     * Merge the two halves of the array : [begin,mid) and [mid,length) .
     * Prerequisite : both of the halves themselves are already sorted.
     *
     * @param objects array whose two halves between begin and end and separated by mid are already sorted
     * @param begin index of the first element of the left half
     * @param end index after the last element of the right half
     * @param mid index of the last element of the left half
     * @param work a work buffer
     */
    public static void merge(Comparable[] objects,int begin,int end,int mid,Comparable[] work){
        int left = begin;
        int leftEnd = mid;
        int right = mid;
        int rightEnd = end;
        int pos = 0;
        /*
         Note the left half and the right half themselves are already sorted,
         We only compare the head of each part and pick the smaller one as winner to be copied to the work buffer,
         loop until one of the halves is empty.
        */
         while(left!=leftEnd && right!=rightEnd){
            if(objects[left].compareTo(objects[right])<=0){
                //[left] is smaller than or equals to [right], pick and copy [left] as the winner
                work[pos++] = objects[left++];
            }
            else{
                work[pos++] = objects[right++];
            }
         }
         /*
          Now at least one half is empty, all the leftover in the other half are larger than any of those
          originally in the now emptied half. So we just append the (sorted) leftover to work buffer.
         */
         int first = end, last = end;
         if(left==leftEnd){
             first = right;
             last = rightEnd;
         }
         else if(right==rightEnd){
             first = left;
             last = leftEnd;
         }
         if(first!=end && last!=end){
             final int sizeToCopy = last-first;
             System.arraycopy(objects,first,work,pos,sizeToCopy);
             pos+=sizeToCopy;
         }
         //finally copy all sorted elements back to the original array
         System.arraycopy(work,0,objects,begin,pos);
    }
}
