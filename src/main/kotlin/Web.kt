package murphytalk.web
import com.hubspot.jinjava.JinjavaConfig
import com.hubspot.jinjava.loader.ClasspathResourceLocator
import org.slf4j.LoggerFactory
import spark.Spark.*
import spark.ModelAndView
import spark.template.jinjava.JinjavaEngine
import com.google.gson.Gson
import murphytalk.arithmetic.ArithmeticGenerator
import murphytalk.arithmetic.evalArithmetic
import org.slf4j.Logger
import java.io.File


data class Config  (val questionNumber:Int = 15, val operatorNum:Int = 4, val historyFile:String = "")
data class History (val datetime:String,
                    val questionNum: Int,
                    val totalSeconds:Int,
                    val totalAnswerTimes:Int,
                    val accuracyRate:Float)

class QuestionMaster(configFile:String){
    companion object {
        val logger:Logger = LoggerFactory.getLogger(QuestionMaster.javaClass)
    }
    private val config : Config
    private var generator:ArithmeticGenerator
    init {
        val f = File(if(configFile.isEmpty())  "~/.java-note.json"; else configFile)
        config = if(f.isFile && f.canRead()){
            logger.info("Reading config from {}",f.absolutePath)
            Gson().fromJson(f.readText(), Config::class.java)
        }
        else{
            logger.info("Using default config")
            Config()
        }
        generator = ArithmeticGenerator(config.operatorNum)
    }
    data class Question (val question:String, val correctAnswer:Int)
    fun generate() : Array<Question>{
        return Array(config.questionNumber, { _ -> val s = generator.generate(); Question(s, evalArithmetic(s)) })
    }

    fun saveHistory(history:History){
        val sep = ","
        val newline = System.getProperty("line.separator")
        val f = File(config.historyFile)
        if(f.isFile && f.canWrite()) {
            f.appendText(StringBuilder()
                    .append(history.datetime).append(sep)
                    .append(history.questionNum).append(sep)
                    .append(history.totalSeconds).append(sep)
                    .append(history.totalAnswerTimes).append(sep)
                    .append(history.accuracyRate).append(newline)
                    .toString())
        }
    }
}



fun main(args:Array<String>) {
    val gson = Gson()
    val questionMaster = QuestionMaster(if(args !=null && args.isNotEmpty()) args[0] else "")

    staticFiles.location("/static")

    val templateEngine = JinjavaEngine(JinjavaConfig(), ClasspathResourceLocator())
    val commonAttributes = hashMapOf(
            "title" to "Math Homework",
            "year" to 2018
    )

    //math
    path("/math") {
        get("", { _, _ -> ModelAndView(commonAttributes, "templates/math.jinja2") }, templateEngine)
        get("/questions", { _, response ->
            response.status(200)
            response.type("application/json")
            gson.toJson(questionMaster.generate())
        })
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


