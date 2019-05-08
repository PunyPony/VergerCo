package v1.post

import java.io.File
import java.util.{Date, Calendar}
import java.text.SimpleDateFormat
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent._
import scala.io.Source
import javax.inject.Inject
import scala.concurrent.duration._
import play.api.http.HttpEntity
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import java.sql.Timestamp
import java.io.File
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.iteratee.Enumerator
import play.api.libs.ws._
import models._

class MyController @Inject()(implicit ec: ExecutionContext,
                             ws: WSClient,
                             val controllerComponents: ControllerComponents,
                             weatherService: WeatherRepository) extends BaseController {

//  val weatherForm = Form(
//    mapping(
//      "id" -> ignored(None: Option[Long]),
//      "sunshine" -> optional(boolean),
//      "temperature" -> optional(float),
//      "humidity" -> optional(float),
//      "wind" -> optional(float),
//      "timeStamp" -> optional(date("dd-MM-yyyy- HH:mm:ss a")), //HH:mm:ss a
//    )(Weather.apply)(Weather.unapply)

//
//  case class Weather(id: Option[Long] = None,
//                     sunshine: Option[Boolean],
//                     temperature: Option[Float],
//                     humidity: Option[Float],
//                     wind: Option[Float],
//                     timeStamp: Option[Date])

  def responseToResult(response: WSResponse): Result = {

    val headers = response.headers map { h => (h._1, h._2.head) }
    val entity = HttpEntity.Strict(
      ByteString(response.body.getBytes),
      response.headers.get("Content-Type").map(_.head)
    )
    Result(ResponseHeader(response.status, headers), entity)
  }

  def index: Action[AnyContent] = Action.async { implicit request =>
    val r: Future[Result] = Future.successful(Ok("hello world"))
    r
  }

  def getInfo(url: String) = Action.async {
    implicit request => {

      val requestObj: WSRequest = ws.url(url)
      val futureResponse: Future[WSResponse] = requestObj.get()
      val r: Future[Result] = futureResponse.flatMap(responseObj => Future {
        responseToResult(responseObj)
      })
      r
    }
  }

  def getWeather() = {
    getInfo("http://localhost:9001/v1/posts/weather")
  }

  def getState() = {
    getInfo("http://localhost:9001/v1/posts/state")
  }

  def getFruitQuality() = {
    getInfo("http://localhost:9001/v1/posts/quality")
  }

  def getCurrentdateTimeStamp: Timestamp ={
    val today:java.util.Date = Calendar.getInstance.getTime
    val timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val now:String = timeFormat.format(today)
    val re = java.sql.Timestamp.valueOf(now)
    re
  }


  def processState = Action { request =>
    request.body.asJson.map { jsonObj =>
        val json = jsonObj(0)
        val chargeperc = (json \ "chargeperc").asOpt[Int].get
        val temperature = (json \ "temperature").asOpt[Int].get
        val placename = (json \ "place" \"name").asOpt[String].get
        val lat = (json \ "place" \"location" \ "lat").asOpt[Float].get
        val long = (json \"place" \ "location" \ "long").asOpt[Float].get

      println("Recieve State")
//        println(chargeperc)
//        println(temperature)
//        println(lat)
//        println(long)
//        println(placename)
        Ok("Saved with sucess")
      }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }



  def processWeather = Action { request =>
    request.body.asJson.map { jsonObj =>
      val json = jsonObj(0)
      val sunshine = (json \ "sunshine" ).asOpt[Boolean]//.get
      val temperature = (json \ "temperature").asOpt[Float]//.get
      val humidity = (json \ "humidity").asOpt[Float]//.get
      val wind = (json \ "wind" ).asOpt[Float]//.get
      println("Recieve Weather")

      val weather = new Weather(None, sunshine, temperature, humidity, wind, /*Some(getCurrentdateTimeStamp)*/None)
      weatherService.insert(weather)

//      println(sunshine)
//      println(temperature)
//      println(humidity)
//      println(wind)
      Ok("Saved with sucess")
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }

  def processFruitQuality = Action { request =>
    request.body.asJson.map { jsonObj =>
      val json = jsonObj(0)
      val mature = (json \ "mature").asOpt[Boolean].get
      val sickness = (json \ "sickness").asOpt[Boolean].get
      println("Recieve Quality")
      //      println(mature)
//      println(sickness)
      Ok("Saved with sucess")
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }

}
