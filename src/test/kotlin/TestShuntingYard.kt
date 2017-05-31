package murphytalk.arithmetic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

/**
 * Created by murphytalk on 5/22/2017.
 */
class TestShuntingYard {
    @Test fun testRPN1() {
        assertThat(RPN("5 1 2+4*+3-"), `is`(14))
    }

    @Test fun testRPN2() {
        assertThat(RPN("25 20-50-"), `is`(-45))
    }

    @Test fun testRPN3() {
        assertThat(RPN("4 5^"), `is`(1024))
    }

    @Test(expected = RuntimeException::class) fun testRPN_InvalidOperator() {
        RPN("512D34")
    }

    @Test fun testRPN_callback1() {
        var negative = false
        RPN("5 1 2+4*+3-", {value -> if(!negative && value<0) negative = true })
        assertThat(negative,`is`(false))
    }

     @Test fun testRPN_callback2() {
        var negative = false
        RPN("25 20-50-", {value -> if(!negative && value<0) negative = true })
        assertThat(negative,`is`(true))
    }

    @Test fun shuntingYard() {
        assertThat(shuntingYard("3+4*2/(1-5)^2^3"), `is`("3 4 2*1 5-2 3^^/+"))
    }

    @Test fun eval1(){
        assertThat(evalArithmetic("5+((1+2)*4)-3"),`is`(14))
    }

    @Test fun eval2(){
        assertThat(evalArithmetic("25+50"),`is`(75))
    }

    @Test fun eval3(){
        assertThat(evalArithmetic("4^5"),`is`(1024))
    }

    @Test fun eval4(){
        assertThat(evalArithmetic("1+4*5"),`is`(21))
    }

    @Test fun eval5(){
        assertThat(evalArithmetic("307+743*279-958"), `is`(206646))
    }
}