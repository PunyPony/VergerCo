package v1.post

import java.io.File
import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.mvc._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent._
import scala.io.Source
import play.api.libs.json._
import play.api.libs.functional.syntax._
import sensors._
import play.api.http.HttpEntity
import akka.util.ByteString


class MyController @Inject()(implicit ec: ExecutionContext, ws: WSClient, val controllerComponents: ControllerComponents) extends BaseController {

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

  def PushInfo(url: String, sensor: JsValue) = {
    val futureResponse: Future[WSResponse] = ws.url(url).post(sensor)
    val r: Future[Result] = futureResponse.flatMap(responseObj => Future {
      responseToResult(responseObj)
    })
    r
  }

  def MetaPushInfo(url: String, sensor: JsValue) = Action.async {
    implicit request => {
      val r: Future[Result] = PushInfo(url, sensor)
      r
    }
  }

  def pushWeather() = {
    val jsonSensor = CSVReader.getWeather("csvjson/weather.csv")
    checkWeatherAlert(jsonSensor)
    MetaPushInfo("http://localhost:9000/v1/posts/processWeather", jsonSensor)
  }

  def pushState() = {
    val jsonSensor = CSVReader.getState("csvjson/state.csv")
    checkStateAlert(jsonSensor)
    MetaPushInfo("http://localhost:9000/v1/posts/processState", jsonSensor)
  }

  def pushFruitQuality() = {
    val jsonSensor = CSVReader.getFruit("csvjson/quality.csv")
    checkFruitQualityAlert(jsonSensor)
    MetaPushInfo("http://localhost:9000/v1/posts/processQuality", jsonSensor)
  }

  def pushFruitAlert(alert: JsValue) = {
    MetaPushInfo("http://localhost:9000/v1/posts/processAlert", alert)
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
    if (charge <= 15)
      PushInfo("http://localhost:9000/v1/posts/processAlert", Json.toJson(new Alert(0, "Sys : Critical battery low")))
    if (temperature >= 70)
      PushInfo("http://localhost:9000/v1/posts/processAlert", Json.toJson(new Alert(0, "Sys : High critical temperature")))
  }

  def checkWeatherAlert(weather: JsValue): Unit = {
    val temperature = (weather.head \ "temperature").as[Float]
    val wind = (weather.head \ "wind").as[Float]
    if (wind >= 40)
      PushInfo("http://localhost:9000/v1/posts/processAlert", Json.toJson(new Alert(0, "Warning : High wind speed")))
    if (temperature >= 28)
      PushInfo("http://localhost:9000/v1/posts/processAlert", Json.toJson(new Alert(0, "Warning : High temperature")))
  }

  def checkFruitQualityAlert(fruitQuality: JsValue): Unit = {
    val mature = (fruitQuality.head \ "mature").as[Boolean]
    val sickness = (fruitQuality.head \ "sickness").as[Boolean]
    if (mature)
      PushInfo("http://localhost:9000/v1/posts/processAlert", Json.toJson(new Alert(0, "Your fruits are mature !")))
    if (sickness)
      PushInfo("http://localhost:9000/v1/posts/processAlert", Json.toJson(new Alert(0, "Warning : Your fruits seems to be sick.")))
  }

  def getState: Action[AnyContent] = {
    val jsonSensor = CSVReader.getState("csvjson/state.csv")
    getSensor(jsonSensor)
  }

  def getWeather: Action[AnyContent] = {
    val jsonSensor = CSVReader.getWeather("csvjson/weather.csv")
    getSensor(jsonSensor)
  }

  def getFruitQuality: Action[AnyContent] = {
    val jsonSensor = CSVReader.getFruit("csvjson/quality.csv")
    getSensor(jsonSensor)
  }
}

