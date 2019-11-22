package murphytalk.web
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import murphytalk.arithmetic.ArithmeticGenerator
import murphytalk.arithmetic.evalArithmetic

import murphytalk.arithmetic.ArithmeticConfig
data class MathConfig  (val questionNumber:Int = 15, val arithmeticConfig: ArithmeticConfig = ArithmeticConfig())
data class History (val yyyymmdd: Int,
                    val hhmmss: Int,
                    val totalSeconds:Int,
                    val totalAnsweredTimes:Int,
                    val accuracyRate:Float)

class QuestionMaster(private val config:MathConfig, private val dao:DAO) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(QuestionMaster.javaClass)
    }

    private var generator: ArithmeticGenerator = ArithmeticGenerator(config.arithmeticConfig)

    data class Question(val question: String, val correctAnswer: Int)

    fun generate(): Array<Question> {
        return Array(config.questionNumber) { _ -> val s = generator.generate(); Question(s, evalArithmetic(s)) }
    }

    fun saveHistory(history:History){
        dao.saveHistory(history,config)
    }
}

fun DAO.saveHistory(history:History,config:MathConfig){
    q.update("insert into math_history (yyyymmdd,hhmmss,total_seconds,total_answered_times,accuracy_rate,config) values (:yyyymmdd,:hhmmss,:total_seconds,:total_answered_times,:accuracy_rate,:config)")
            .namedParam("yyyymmdd",history.yyyymmdd)
            .namedParam("hhmmss",history.hhmmss)
            .namedParam("total_seconds",history.totalSeconds)
            .namedParam("total_answered_times",history.totalAnsweredTimes)
            .namedParam("accuracy_rate",history.accuracyRate)
            .namedParam("config", Gson().toJson(config))
            .run()
}

