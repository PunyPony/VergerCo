package v1.post

import java.io.File

import javax.inject.Inject
import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent._
import scala.io.Source
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import sensors._
import play.api.libs.json._
import play.api.http.HttpEntity
import akka.util.ByteString


class MyController  @Inject()(implicit ec: ExecutionContext, ws: WSClient, val controllerComponents: ControllerComponents) extends BaseController  {

  implicit val locationWrites: Writes[Location] = (
    (JsPath \ "lat").write[Double] and
      (JsPath \ "long").write[Double]
    ) (unlift(Location.unapply))
  implicit val placeWrites: Writes[Place] = (
    (JsPath \ "name").write[String] and
      (JsPath \ "location").write[Location]
    ) (unlift(Place.unapply))

  implicit val StateWrites: Writes[State] = (
    (JsPath \ "chargeperc").write[Int] and
      (JsPath \ "temperature").write[Int] and
      (JsPath \ "place").write[Place]
    ) (unlift(State.unapply))
  implicit val weatherWrites: Writes[Weather] = (
    (JsPath \ "sunshine").write[Boolean] and
      (JsPath \ "temperature").write[Int] and
      (JsPath \ "humidity").write[Int] and
      (JsPath \ "wind").write[Int]
    ) (unlift(Weather.unapply))

  implicit val qualityWrites: Writes[Quality] = (
    (JsPath \ "mature").write[Boolean] and
      (JsPath \ "sickness").write[Boolean]
    ) (unlift(Quality.unapply))

  def responseToResult(response: WSResponse): Result = {
    val headers = response.headers map { h => (h._1, h._2.head) }
    val entity = HttpEntity.Strict(
      ByteString(response.body.getBytes),
      response.headers.get("Content-Type").map(_.head)
    )
    Result(ResponseHeader(response.status, headers), entity)
  }

  def pushInfo(url: String, sensor: JsValue) = Action.async {
    implicit request => {
      val futureResponse: Future[WSResponse] = ws.url(url).post(sensor)
      val r: Future[Result] = futureResponse.flatMap(responseObj => Future {
        responseToResult(responseObj)
      })
      r
    }
  }

  def pushWeather() =
  {
    val jsonSensor = Json.toJson(CSVReader.getWeather("csvjson/weather.csv"))
    pushInfo("http://localhost:9000/v1/posts/processWeather", jsonSensor)
  }

  def pushState() = {
    val jsonSensor = Json.toJson(CSVReader.getWeather("csvjson/state.csv"))
    pushInfo("http://localhost:9000/v1/posts/processState", jsonSensor)
  }

  def pushQualityFruit() = {
    val jsonSensor = Json.toJson(CSVReader.getWeather("csvjson/quality.csv"))
    pushInfo("http://localhost:9000/v1/posts/processQuality", jsonSensor)
  }

  def getSensor(sensor: JsValue): Action[AnyContent] = Action.async {
    implicit request => {
      val r: Future[Result] = Future.successful(Ok(sensor))
      r
    }
  }

  def getState: Action[AnyContent] = {
    val jsonSensor = Json.toJson(CSVReader.getState("csvjson/state.csv"))
      getSensor (jsonSensor)
  }

  def getWeather: Action[AnyContent] = {
    val jsonSensor = Json.toJson(CSVReader.getWeather("csvjson/weather.csv"))
      getSensor (jsonSensor)
  }

  def getFruitQuality: Action[AnyContent] = {
    val jsonSensor = Json.toJson(CSVReader.getFruit("csvjson/quality.csv"))
      getSensor (jsonSensor)
  }

}
