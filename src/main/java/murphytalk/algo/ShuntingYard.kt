package murphytalk.algo

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by murphytalk on 5/22/2017.
 */
//Reverse Polish Notation
fun RPN(s: String): Int {
    val stack = Stack<Int>()
    val num = StringBuilder()
    for (c in s) {
        if (c == ' ') {
            stack.push(Integer.parseInt(num.toString()))
            num.delete(0, num.length)
        } else {
            val v = c.toInt() - '0'.toInt()
            if (v in 0..9) {
                num.append(v)
            } else {
                //the 1st value could still be in buffer num
                val v2 = if (num.length > 0) num.toString().toInt() else stack.pop()
                num.delete(0, num.length)
                //pop the 2nd value
                val v1 = stack.pop()
                stack.push(
                        when (c) {
                            'x', '*' -> v1 * v2
                            '/' -> v1 / v2
                            '-' -> v1 - v2
                            '+' -> v1 + v2
                            '^' -> Math.pow(v1.toDouble(), v2.toDouble()).toInt()
                            else -> throw RuntimeException("Invalid operator $c")
                        }
                )
            }
        }
    }
    return stack.pop().toInt()
}

// https://en.wikipedia.org/wiki/Shunting-yard_algorithm
fun shuntingYard(s: String): String {
    val stack = Stack<String>()
    val output = StringBuilder()
    for (c in s) {
        if ((c.toInt() - '0'.toInt()) in 0..9) {
            output.append(c)
        } else {
            /*
            when(c){
                'x','*','/','+','-' ->

            }
            */
        }
    }
    return output.toString()
}

fun evalArithmetic(s: String): Int {
    return RPN(shuntingYard(s))
}
