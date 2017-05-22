package murphytalk.algo

import java.util.*

/**
 * Created by murphytalk on 5/22/2017.
 */
//Reverse Polish Notation
fun RPN(s: String): Int {
    val stack = Stack<Int>()
    for (c in s) {
        val v = c.toInt() - '0'.toInt()
        if (v in 0..9) {
            stack.push(v)
        } else {
            //pop 2 values
            val v2 = stack.pop()
            val v1 = stack.pop()
            when (c) {
                'x', '*' -> stack.push(v1 * v2)
                '/'      -> stack.push(v1 / v2)
                '-'      -> stack.push(v1 - v2)
                '+'      -> stack.push(v1 + v2)
                else     -> throw RuntimeException("Invalid operator $c")
            }
        }
    }
    return stack.pop().toInt()
}


class ShuntingYard {
}