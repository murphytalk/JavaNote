package murphytalk.arithmetic

import org.junit.Test

class Test{
    @Test
    fun collection(){
        // both of the followings generate byte codes using Iterator
        val l = listOf(1,2,3,4)
        // but this generates an additional assignment inside the loop
        //  $i$a$-forEach-Test$collection$1 = 0;
        l.forEach { /*println(it)*/ }
        //
        for(x in l){
            //println(x)
        }

        // both fo the followings generate byte codes using for loop and position indexing
        val l2 = arrayOf(4,3,2,1)
        l2.forEach { /*println(it)*/ }
        // more efficient
        for(x in l2){
            //println(x)
        }
    }
}