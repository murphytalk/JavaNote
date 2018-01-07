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


data class Config (val questionNumber:Int = 15, val operatorNum:Int = 4)

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
    }
}


