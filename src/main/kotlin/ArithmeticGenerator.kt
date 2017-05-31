package murphytalk.arithmetic

import java.util.*

/**
 * Created by murphytalk on 5/31/2017.
 */
class ArithmeticGenerator private constructor(val operatorNum:Int, val noNageative: Boolean,
                                              val allowMultiplication: Boolean, val allowDivision: Boolean,
                                              val numberDigitsPossibilities: Array<Pair<Int,Int>>){

    private val random = Random()

    private fun generateOperator(): String {
        //division can only be allowed when multiplication is allowed
        val possibleOperators =  if(allowMultiplication) {if(allowDivision) arrayOf('+','-','*','/') else arrayOf('+','-','*')} else arrayOf('+','-')
        return possibleOperators[random.nextInt(possibleOperators.size)].toString()
    }

    private fun generateNumber(): Int {
        var p = random.nextInt(101) // an integer within [0, 100]
        var v = 0
        for ((possibility, digits) in numberDigitsPossibilities){
            if(p<=possibility){
                do {
                    v = Math.floor(random.nextFloat() * Math.pow(10.0, digits.toDouble())).toInt()
                } while(v ==0 ) //at least 2 digits
                break
            }
            else{
                p = 100 - p
            }
        }
        return v
    }

    fun generate(): String{
        val sb = StringBuilder()
        var numOfOperator = 0

        sb.append(generateNumber())
        while(numOfOperator<operatorNum){
            sb.append(generateOperator()).append(generateNumber())
            ++numOfOperator
        }

        return sb.toString()
    }

    class Builder{
        private var operatorNum:Int = 3
        private var noNageative: Boolean = true
        private var allowMultiplication: Boolean = true
        private var allowDivision: Boolean = false
        //default : 80% chance to generate a 3 digits number; 20% chance to generate a 4 digits number
        //needs to be sorted by chance in descending order
        private var numberDigitsPossibilities: Array<Pair<Int,Int>> = arrayOf(Pair(80,3), Pair(20,4))

        fun setOperatorNum(n:Int?): Builder{
            if(n!=null) operatorNum = n
            return this
        }

        fun setNoNegative(b:Boolean?): Builder{
            if(b!=null) noNageative = b
            return this
        }

        fun setAllowMultiplication(b:Boolean?): Builder{
            if(b!=null) allowMultiplication = b
            return this
        }

        fun setAllowDivision(b:Boolean?): Builder{ //division can only be allowed when multiplication is allowed
            if(allowMultiplication) {
                if (b != null) allowDivision = b
            }
            return this
        }

        fun setNumberDigitsPossibilities(p: Array<Pair<Int,Int>>?):Builder{
            if(p!=null) numberDigitsPossibilities = p
            return this
        }

        fun build(): ArithmeticGenerator{
            return ArithmeticGenerator(operatorNum, noNageative, allowMultiplication, allowDivision, numberDigitsPossibilities)
        }
    }
}
