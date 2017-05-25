package murphytalk.algo.kotlin

import murphytalk.algo.Sort
import java.util.Arrays

class MergeSort
/**
 * Initialize a MergeSort
 * @param size size of the elements to sort
 */
(size: Int) : Sort {
    internal val workBuffer: Array<Comparable<*>?> = arrayOfNulls(size)

    override fun sort(objects: Array<Comparable<*>>, begin: Int, end: Int) {
        val size = end - begin
        if (size < SMALL_ENOUGH) {
            Arrays.sort(objects, begin, end)
        } else {
            val mid = begin + (end - begin).ushr(1)
            sort(objects, begin, mid)
            sort(objects, mid, end)
            merge(objects, begin, end, mid, workBuffer)
        }
    }

    companion object {
        val SMALL_ENOUGH = 16

        /**
         * Merge the two halves of the array : [begin,mid) and [mid,length) .
         * Prerequisite : both of the halves themselves are already sorted.

         * @param objects array whose two halves between begin and end and separated by mid are already sorted
         * @param begin index of the first element of the left half
         * @param end index after the last element of the right half
         * @param mid index of the last element of the left half
         * @param work a work buffer
         */
        fun merge(objects: Array<Comparable<*>>, begin: Int, end: Int, mid: Int, work: Array<Comparable<*>?>) {
            var left = begin
            val leftEnd = mid
            var right = mid
            val rightEnd = end
            var pos = 0
            /*
         Note the left half and the right half themselves are already sorted,
         We only compare the head of each part and pick the smaller one as winner to be copied to the work buffer,
         loop until one of the halves is empty.
        */
            while (left != leftEnd && right != rightEnd) {
                if (objects[left].compareTo(objects[right]) <= 0) {
                    //[left] is smaller than or equals to [right], pick and copy [left] as the winner
                    work[pos++] = objects[left++]
                } else {
                    work[pos++] = objects[right++]
                }
            }
            /*
          Now at least one half is empty, all the leftover in the other half are larger than any of those
          originally in the now emptied half. So we just append the (sorted) leftover to work buffer.
         */
            var first = end
            var last = end
            if (left == leftEnd) {
                first = right
                last = rightEnd
            } else if (right == rightEnd) {
                first = left
                last = leftEnd
            }
            if (first != end && last != end) {
                val sizeToCopy = last - first
                System.arraycopy(objects, first, work, pos, sizeToCopy)
                pos += sizeToCopy
            }
            //finally copy all sorted elements back to the original array
            System.arraycopy(work, 0, objects, begin, pos)
        }
    }
}
