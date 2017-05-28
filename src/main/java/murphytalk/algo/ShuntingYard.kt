package murphytalk.algo

import java.util.*

/**
 * Created by murphytalk on 5/22/2017.
 */
//Reverse Polish Notation
fun RPN(s: String): Int {
    val stack = Stack<Int>()
    val num = StringBuilder()
    for (c in s) {
        if (c == ' ') {
            stack.push(num.toString().toInt())
            num.delete(0, num.length)
        } else {
            if(c in '0'..'9'){
                num.append(c)
            } else {
                //the 2nd value could still be in buffer num
                val v2 = if (num.length > 0) num.toString().toInt() else stack.pop()
                num.delete(0, num.length)
                //pop the 1st value
                val v1 = stack.pop()
                stack.push(
                        when (c) {
                            '*' -> v1 * v2
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
// todo: support function
fun shuntingYard(s: String): String {
    /**
     * Compare operator order/precedence
     * Return value :
     *     >0 => o1 > o2
     *    ==0 => o1 = o2
     *     <0 =? o1 < o2
     */
    fun operatorOrder(o1: Char, o2: Char): Int {
        fun conv(o: Char): Int {
            return if (o == '+' || o == '-') 0
                   else if (o == '*' || o == '/') 1
                   else 2 //^
        }
        return conv(o1) - conv(o2)
    }

    //0: left, 1: right, 2: non
    fun associativity(o: Char): Int{
        return when(o){
            '+','-','*','/' -> 0
            '^' -> 1
            else -> 2
        }
    }

    val stack = Stack<String>()
    val output = StringBuilder()
    for (c in s) {
        if (c in '0'..'9') {
            output.append(c)
        } else {
            when (c) {
                '*', '/', '+', '-', '^' -> {
                    if (stack.size >= 1) {
                        val o2 = stack.peek()[0]
                        val order = operatorOrder(c /*o1*/, o2)
                        val ass = associativity(o2)
                        if( ((ass == 0 /*left-associative*/ ) && (order <= 0)) ||
                            ((ass == 1 /*right-associative*/) && (order <0))) {
                                stack.pop()
                                output.append(o2)
                        }
                    }
                    if(output.last() in '0'..'9') {
                        output.append(" ")/*number separator*/
                    }
                    stack.push(c.toString())
                }
                '(' -> stack.push(c.toString())
                ')' -> {
                    var foundLeftParenthesis = false
                    do {
                        if (stack.size == 0)
                            throw RuntimeException("Mismatched parenthesis")
                        else {
                            val c2 = stack.pop()
                            if (c2[0] == '(')
                                foundLeftParenthesis = true
                            else
                                output.append(c2)
                        }
                    } while (!foundLeftParenthesis)
                }
                else -> throw RuntimeException("Invalid operator $c")  //todo: support function
            }
        }
    }
    while (stack.size > 0) {
        val c2 = stack.pop()
        if (c2[0] == '(' || c2[0] == ')')
            throw RuntimeException("Mismatched parenthesis")
        else
            output.append(c2)
    }
    return output.toString()
}

fun evalArithmetic(s: String): Int {
    return RPN(shuntingYard(s))
}
