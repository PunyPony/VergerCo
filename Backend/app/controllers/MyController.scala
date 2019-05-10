package controllers

import java.io.{PrintWriter, File}
import java.util.{Date, Calendar}
import java.text.SimpleDateFormat
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent._
import scala.util.{Try,Success,Failure}
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
import scala.language.postfixOps
import play.api.test._
//import play.api.test.Helpers._

class MyController @Inject()(implicit ec: ExecutionContext,
                             ws: WSClient,
                             val controllerComponents: ControllerComponents,
                             weatherService: WeatherRepository,
                             stateService: StateRepository,
                             fruitQualityService: FruitQualityRepository,
                             alertService: AlertRepository) extends BaseController {

  //  val weatherForm = Form(
  //    mapping(
  //      "id" -> ignored(None: Option[Long]),
  //      "sunshine" -> optional(boolean),
  //      "temperature" -> optional(float),
  //      "humidity" -> optional(float),
  //      "wind" -> optional(float),
  //      "timeStamp" -> optional(date("dd-MM-yyyy- HH:mm:ss a")), //HH:mm:ss a
  //    )(Weather.apply)(Weather.unapply)


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

  def getInfa(url: String) = {
    val requestObj: WSRequest = ws.url(url)
    val futureResponse: Future[WSResponse] = requestObj.get()
    futureResponse
  }

  def getWeather() = {
    getInfo("http://localhost:9001/v1/posts/weather")
  }

  def getState() = {
    getInfo("http://localhost:9001/v1/posts/state")
  }

  def getFruitQuality() = {
    getInfo("http://localhost:9001/v1/posts/qualityFruit")
  }

  def getBoard() = {
    val dir = new File("conf")
    //val files = dir.list.map{x => Json.parse(Source.fromFile("csvjson/"+x).getLines.mkString)}
    val objects = (Json.parse(Source.fromFile("conf/objects.conf").getLines.mkString) \ "objects")
                       .asOpt[List[JsValue]].get
                       .map{x => ((x \ "name").asOpt[String].get, (x \ "ip").asOpt[String].get)}.toList
    Action{Ok(views.html.board(objects))}
  }

  def getObjInfo(ip : String) = {
    val weather = Await.result(getInfa(ip + "/weather"), 1 seconds).json
    val state = Await.result(getInfa(ip + "/state"), 1 seconds).json
    val quality = Await.result(getInfa(ip + "/quality"), 1 seconds).json

    val result = Redirect(ip + "/v1/posts/state");
  //  print(Helpers.contentAsString(Result result))
    Action{Ok(views.html.info(ip, state))}
  }

  def processState = Action { request =>
    request.body.asJson.map { json =>
      val objectId = (json.head \ "objectID").asOpt[Int]
      val chargeperc = (json.head \ "chargeperc").asOpt[Float]
      val temperature = (json.head \ "temperature").asOpt[Float]
      val placename = (json.head \ "place" \ "name").asOpt[String]
      val lat = (json.head \ "place" \ "location" \ "lat").asOpt[Float]
      val long = (json.head \ "place" \ "location" \ "long").asOpt[Float]
      println("Recieve State")
      val state = new State(None, objectId, chargeperc, temperature, placename, lat, long, None)
      stateService.insert(state)
      Ok("State saved with sucess")
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }


  def processWeather = Action { request =>
    request.body.asJson.map { json =>
      val objectId = (json.head \ "objectID").asOpt[Int]
      val sunshine = (json.head \ "sunshine").asOpt[Boolean]
      val temperature = (json.head \ "temperature").asOpt[Float]
      val humidity = (json.head \ "humidity").asOpt[Float]
      val wind = (json.head \ "wind").asOpt[Float]
      println("Recieve Weather")
      val weather = new Weather(None, objectId, sunshine, temperature, humidity, wind, None)
      weatherService.insert(weather)
      Ok("Weather saved with sucess")
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }

  def processFruitQuality = Action { request =>
    request.body.asJson.map { json =>
      val objectId = (json.head \ "objectID").asOpt[Int]
      val mature = (json.head \ "mature").asOpt[Boolean]
      val sickness = (json.head \ "sickness").asOpt[Boolean]
      println("Recieve Quality")
      val fruitQuality = new FruitQuality(None, objectId, mature, sickness, None)
      fruitQualityService.insert(fruitQuality)
      Ok("FruitQuality saved with sucess")
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }

  def processAlert = Action { request =>
    request.body.asJson.map { json =>
      val objectId = (json \ "objectID").asOpt[Int]
      val alertType = (json \ "alertType").asOpt[String]
      println("Recieve Alert")
      val alert = new Alert(None, objectId, alertType, None)
      alertService.insert(alert)
      Ok("Alert saved with sucess")
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }

}
