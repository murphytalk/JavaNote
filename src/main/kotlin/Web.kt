package murphytalk.web
import com.hubspot.jinjava.JinjavaConfig
import com.hubspot.jinjava.loader.ClasspathResourceLocator
import org.slf4j.LoggerFactory
import spark.Spark.*
import spark.ModelAndView
import spark.template.jinjava.JinjavaEngine
import com.google.gson.Gson
import murphytalk.arithmetic.ArithmeticConfig
import murphytalk.arithmetic.ArithmeticGenerator
import murphytalk.arithmetic.evalArithmetic
import murphytalk.utils.mergeReduce
import org.slf4j.Logger
import spark.Response
import java.io.File
import java.time.LocalDateTime


data class MathConfig  (val questionNumber:Int = 15, val arithmeticConfig: ArithmeticConfig = ArithmeticConfig() , val historyDb:String = "jdbc:sqlite::memory:")
data class Config (val mathConfig:MathConfig = MathConfig())

data class History (val datetime:String,
                    val questionNum: Int,
                    val totalSeconds:Int,
                    val totalAnswerTimes:Int,
                    val accuracyRate:Float)

class QuestionMaster(val config:MathConfig){
    companion object {
        val logger:Logger = LoggerFactory.getLogger(QuestionMaster.javaClass)
    }
    private var generator:ArithmeticGenerator = ArithmeticGenerator(config.arithmeticConfig)

    data class Question (val question:String, val correctAnswer:Int)
    fun generate() : Array<Question>{
        return Array(config.questionNumber, { _ -> val s = generator.generate(); Question(s, evalArithmetic(s)) })
    }

    fun saveHistory(history:History){
    }
}

fun main(args:Array<String>) {
    val logger:Logger = LoggerFactory.getLogger("Web.main")

    fun <T> toJson(gson: Gson, response: Response, obj:T):String{
        response.status(200)
        response.type("application/json")
        return gson.toJson(obj)
    }

    val gson = Gson()
    val configFile = if(args !=null && args.isNotEmpty()) args[0] else ""
    val f = File(if(configFile.isEmpty())  "~/.java-note.json"; else configFile)

    val config:Config = if(f.isFile && f.canRead()){
                            logger.info("Reading config from {}",f.absolutePath)
                            gson.fromJson(f.readText(), Config::class.java)
                        }else{
                            logger.info("Using default config")
                            Config()
                        }
    val questionMaster = QuestionMaster(config.mathConfig)

    staticFiles.location("/static")

    val templateEngine = JinjavaEngine(JinjavaConfig(), ClasspathResourceLocator())
    val commonAttributes = hashMapOf(
            "year" to LocalDateTime.now().year
    )

    //general
    get("/config") { _, resp->
        toJson(gson, resp, config)
    }

    //math
    path("/math") {
        get("", { _, _ -> ModelAndView(
                commonAttributes.mergeReduce(hashMapOf("title" to "Math Homework")),
                "templates/math.jinja2") }, templateEngine)
        get("/questions") { _, response ->
            toJson(gson, response, questionMaster.generate())
        }
        post("/submit") { req, _ ->
            questionMaster.saveHistory(
                    History(req.params("datetime"),
                            req.params("questionNum").toInt(),
                            req.params("totalSeconds").toInt(),
                            req.params("totalAnswerTimes").toInt(),
                            req.params("accuracyRate").toFloat()))
            "ok"
        }
    }
}


