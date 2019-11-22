package murphytalk.arithmetic

import java.util.*
import kotlin.math.floor
import kotlin.math.pow

/**
 * Created by murphytalk on 5/31/2017.
 */
data class DigitsPossibility(val percentage:Int, val digits:Int)
data class ArithmeticConfig (val operatorNum:Int = 4,
                             val noNegative: Boolean = true,
                             val allowMultiplication: Boolean = true,
                             val allowDivision: Boolean = false,
                             //default num digits for + -: 80% chance to generate a 3 digits number; 20% chance to generate a 4 digits number
                             //needs to be sorted by chance in descending order
                             val addSubNumberDigitsPossibilities: Array<DigitsPossibility> = arrayOf(DigitsPossibility(80,3), DigitsPossibility(20,4)),
                             //default num digits for * /: 80% chance to generate a 1 digit number; 20% chance to generate a 2 digits number
                             val multiDivNumberDigitsPossibilities: Array<DigitsPossibility> = arrayOf(DigitsPossibility(80,1), DigitsPossibility(20,2)))

class ArithmeticGenerator(private val config:ArithmeticConfig = ArithmeticConfig()){
    private val random = Random()

    private fun generateOperator(): String {
        //division can only be allowed when multiplication is allowed
        val possibleOperators =  if(config.allowMultiplication) {if(config.allowDivision) arrayOf('+','-','*','/') else arrayOf('+','-','*')} else arrayOf('+','-')
        return possibleOperators[random.nextInt(possibleOperators.size)].toString()
    }

    private fun generateNumber(operatorIsAddSub:Boolean): Int {
        var p = random.nextInt(101) // an integer within [0, 100]
        var v = 0
        val possibilities = if(operatorIsAddSub) config.addSubNumberDigitsPossibilities else config.multiDivNumberDigitsPossibilities
        for ((possibility, digits) in possibilities){
            if(p<=possibility){
                do {
                    v = floor(random.nextFloat() * 10.0.pow(digits.toDouble())).toInt()
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
        if(config.noNegative) callback = { value:Int -> if(!negative && value<0) negative = true}

        while(true) {
            sb.append(generateNumber(true))
            while (numOfOperator < config.operatorNum) {
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
