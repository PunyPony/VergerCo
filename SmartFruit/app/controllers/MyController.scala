package controllers

import java.io.File
import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.mvc._
import play.api.libs.ws._
import confreader._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent._
import scala.io.Source
import play.api.libs.json._
import play.api.libs.functional.syntax._
import sensors._
import play.api.http.HttpEntity
import akka.util.ByteString



import services.Kafka


class MyController @Inject()(implicit ec: ExecutionContext, ws: WSClient,
                             val kafkaService : Kafka, val controllerComponents: ControllerComponents) extends BaseController {

  case class Alert(objectID: Int, alertType: String)
  implicit val AlertWrites: Writes[Alert] = (
    (JsPath \ "objectID").write[Int] and
      (JsPath \ "alertType").write[String]
    ) (unlift(Alert.unapply))


  def responseToResult(response: WSResponse): Result = {
    val headers = response.headers map { h => (h._1, h._2.head) }
    val entity = HttpEntity.Strict(
      ByteString(response.body.getBytes),
      response.headers.get("Content-Type").map(_.head)
    )
    Result(ResponseHeader(response.status, headers), entity)
  }

  def PushInfo(url: String, sensor: JsValue, topic : String) = {
    send(topic, sensor)
    val futureResponse: Future[WSResponse] = ws.url(url).post(sensor)
    val r: Future[Result] = futureResponse.flatMap(responseObj => Future {
      responseToResult(responseObj)
    })
    r
  }

  def MetaPushInfo(url: String, sensor: JsValue, topic: String) = Action.async {
    implicit request => {
      val r: Future[Result] = PushInfo(url, sensor, topic)
      r
    }
  }

  def pushWeather() = {
    val jsonSensor = CSVReader.getWeather()
    val url = confReader.getURL()
    checkWeatherAlert(jsonSensor)
    MetaPushInfo(url+"processWeather", jsonSensor, "weather")
  }

  def pushState() = {
    val jsonSensor = CSVReader.getState()
    val url = confReader.getURL()
    checkStateAlert(jsonSensor)
    MetaPushInfo(url+"processState", jsonSensor, "state")
  }

  def pushFruitQuality() = {
    val jsonSensor = CSVReader.getFruit()
    val url = confReader.getURL()
    checkFruitQualityAlert(jsonSensor)
    MetaPushInfo(url+"processQuality", jsonSensor, "quality")
  }

  def pushFruitAlert(alert: JsValue) = {
    val url = confReader.getURL()
    MetaPushInfo(url+"processAlert", alert, "alert")
  }

  def getSensor(sensor: JsValue): Action[AnyContent] = Action.async {
    implicit request => {
      val r: Future[Result] = Future.successful(Ok(sensor))
      r
    }
  }

  def checkStateAlert(state: JsValue): Unit = {
    val charge = (state.head \ "chargeperc").as[Float]
    val temperature = (state.head \ "temperature").as[Float]
    val url = confReader.getURL()
    val id = confReader.getObjID()
    if (charge <= 15)
      PushInfo(url+"processAlert", Json.toJson(new Alert(id, "Sys : Critical battery low")), "alert")
    if (temperature >= 70)
      PushInfo(url+"processAlert", Json.toJson(new Alert(id, "Sys : High critical temperature")), "alert")
  }

  def checkWeatherAlert(weather: JsValue): Unit = {
    val temperature = (weather.head \ "temperature").as[Float]
    val wind = (weather.head \ "wind").as[Float]
    val url = confReader.getURL()
    val id = confReader.getObjID()
    if (wind >= 40)
      PushInfo(url+"processAlert", Json.toJson(new Alert(id, "Warning : High wind speed")), "alert")
    if (temperature >= 28)
      PushInfo(url+"processAlert", Json.toJson(new Alert(id, "Warning : High temperature")), "alert")
  }

  def checkFruitQualityAlert(fruitQuality: JsValue): Unit = {
    val mature = (fruitQuality.head \ "mature").as[Boolean]
    val sickness = (fruitQuality.head \ "sickness").as[Boolean]
    val url = confReader.getURL()
    val id = confReader.getObjID()
    if (mature)
      PushInfo(url+"processAlert", Json.toJson(new Alert(id, "Your fruits are mature !")),"alert")
    if (sickness)
      PushInfo(url+"processAlert", Json.toJson(new Alert(id, "Warning : Your fruits seems to be sick.")),"alert")
  }

  def getState: Action[AnyContent] = {
    val jsonSensor = CSVReader.getState()
    getSensor(jsonSensor)
  }

  def getWeather: Action[AnyContent] = {
    val jsonSensor = CSVReader.getWeather()
    getSensor(jsonSensor)
  }

  def getFruitQuality: Action[AnyContent] = {
    val jsonSensor = CSVReader.getFruit()
    getSensor(jsonSensor)
  }

  def send(topic: String, json: JsValue) =  {
    topic match
    {
      case "alert" => {
        kafkaService.sendMessage(topic, Json.stringify(json)).map {
          case _ => Ok
        }
      }
      case _ => {
        kafkaService.sendMessage(topic, Json.stringify(json(0))).map {
          case _ => Ok
        }
      }
    }
//
//    println("message : " +message)
//    kafkaService.sendMessage(topic, message).map {
//      case _ => Ok
//    }
//    implicit request =>
//    kafkaService.sendMessage(topic, request.body).map {
//      case _ => Ok
//    }

  }
}

