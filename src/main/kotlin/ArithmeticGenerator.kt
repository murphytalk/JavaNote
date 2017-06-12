package murphytalk.arithmetic

import java.util.*

/**
 * Created by murphytalk on 5/31/2017.
 */
class ArithmeticGenerator constructor(val operatorNum:Int = 3,
                                      val noNegative: Boolean = true,
                                      val allowMultiplication: Boolean = true,
                                      val allowDivision: Boolean = false,
                                      //default num digits for + -: 80% chance to generate a 3 digits number; 20% chance to generate a 4 digits number
                                      //needs to be sorted by chance in descending order
                                      val addSubNumberDigitsPossibilities: Array<Pair<Int,Int>> = arrayOf(Pair(80,3), Pair(20,4)),
                                      //default num digits for * /: 80% chance to generate a 1 digit number; 20% chance to generate a 2 digits number
                                      val multiDivNumberDigitsPossibilities: Array<Pair<Int,Int>> = arrayOf(Pair(80,1), Pair(20,2))){

    private val random = Random()

    private fun generateOperator(): String {
        //division can only be allowed when multiplication is allowed
        val possibleOperators =  if(allowMultiplication) {if(allowDivision) arrayOf('+','-','*','/') else arrayOf('+','-','*')} else arrayOf('+','-')
        return possibleOperators[random.nextInt(possibleOperators.size)].toString()
    }

    private fun generateNumber(operatorIsAddSub:Boolean): Int {
        var p = random.nextInt(101) // an integer within [0, 100]
        var v = 0
        val possibilities = if(operatorIsAddSub) addSubNumberDigitsPossibilities else multiDivNumberDigitsPossibilities
        for ((possibility, digits) in possibilities){
            if(p<=possibility){
                do {
                    v = Math.floor(random.nextFloat() * Math.pow(10.0, digits.toDouble())).toInt()
                } while(v == 0)
                break
            }
            else{
                p = 100 - p
            }
        }
        return v
    }

    fun generate(): String{
        fun isAddSub(operator:String):Boolean{
            return when(operator[0]) {
                '+','-' -> true
                else -> false
            }
        }
        val sb = StringBuilder()
        var numOfOperator = 0

        var negative = false
        var callback: ((Int) -> Unit)?  = null
        if(noNegative) callback = { value:Int -> if(!negative && value<0) negative = true}

        while(true) {
            sb.append(generateNumber(true))
            while (numOfOperator < operatorNum) {
                val op = generateOperator()
                sb.append(op).append(generateNumber(isAddSub(op)))
                ++numOfOperator
            }
            if (callback != null) {
                if (evalArithmetic(sb.toString(), callback) < 0 || negative) {
                    //negative value detected, but no negative is demanded, generate again
                    sb.delete(0,sb.length)
                    numOfOperator = 0
                    negative = false
                    continue
                }
            }
            break
        }

        return sb.toString()
    }
}
