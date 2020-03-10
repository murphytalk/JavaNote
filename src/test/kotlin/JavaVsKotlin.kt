package murphytalk.JavaVsKotlin

import murphytalk.JavaVsKotlin.JavaVersion.NOOP
import murphytalk.test.StopWatch
import org.junit.Test

class KotlinVersion {
    @Test
    fun testLambda() {
        StopWatch.measureSimple("KotlinLambda", {
            val i = 0
            JavaVersion.CallHandler(100) { _: Int? ->
                val v = i + 100
                val nop = NOOP(v)
                nop.nop(v)
            }
        }, 10000000)

    }
}