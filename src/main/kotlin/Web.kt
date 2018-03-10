package murphytalk.web
import com.hubspot.jinjava.JinjavaConfig
import com.hubspot.jinjava.loader.ClasspathResourceLocator
import org.slf4j.LoggerFactory
import spark.Spark.*
import spark.ModelAndView
import spark.template.jinjava.JinjavaEngine
import com.google.gson.Gson
import murphytalk.utils.mergeReduce
import org.codejargon.fluentjdbc.api.FluentJdbc
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder
import org.codejargon.fluentjdbc.api.query.Query
import org.slf4j.Logger
import spark.Response
import java.io.File
import java.sql.DriverManager
import java.time.LocalDateTime
import javax.sql.DataSource


data class Config (val dbUrl:String = "jdbc:sqlite::memory:",val mathConfig:MathConfig = MathConfig())


class DAO(config:Config){
    val q : Query = FluentJdbcBuilder().build().queryOn(DriverManager.getConnection(config.dbUrl))
}


fun main(args:Array<String>) {
    val logger:Logger = LoggerFactory.getLogger("Web.main")

    fun <T> toJson(gson: Gson, response: Response, obj:T):String{
        response.status(200)
        response.type("application/json")
        return gson.toJson(obj)
    }

    val gson = Gson()
    val configFile = if( args.isNotEmpty()) args[0] else ""
    val f = File(if(configFile.isEmpty())  "~/.java-note.json"; else configFile)

    val config:Config = if(f.isFile && f.canRead()){
                            logger.info("Reading config from {}",f.absolutePath)
                            gson.fromJson(f.readText(), Config::class.java)
                        }else{
                            logger.info("Using default config")
                            Config()
                        }
    val dao = DAO(config)
    val questionMaster = QuestionMaster(config.mathConfig,dao)

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
                    History(req.queryParams("yyyymmdd").toInt(),
                            req.queryParams("hhmmss").toInt(),
                            req.queryParams("totalSeconds").toInt(),
                            req.queryParams("totalAnsweredTimes").toInt(),
                            req.queryParams("accuracyRate").toFloat()))
            "ok"
        }
    }
}


