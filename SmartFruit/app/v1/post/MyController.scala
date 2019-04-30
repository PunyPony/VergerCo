package v1.post

import java.io.File

import javax.inject.Inject
import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent._
import scala.io.Source
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import sensors._

//class HomeController @Inject()(cc:ControllerComponents) extends AbstractController(cc)  {
//
//}

class MyController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  //  case class State(charge : Int, temperature: Int, place : Place)
//  case class Location(lat: Double, long: Double)
//  case class Place(name: String, location: Location)
  implicit val locationWrites: Writes[Location] = (
    (JsPath \ "lat").write[Double] and
      (JsPath \ "long").write[Double]
    )(unlift(Location.unapply))
  implicit val placeWrites: Writes[Place] = (
    (JsPath \ "name").write[String] and
      (JsPath \ "location").write[Location]
    )(unlift(Place.unapply))

  implicit val StateWrites: Writes[State] = (
    (JsPath \ "chargeperc").write[Int] and
      (JsPath \ "temperature").write[Int] and
      (JsPath \ "place").write[Place]
    )(unlift(State.unapply))

//  case class Weather(sunshine : Int, temperature : Int , humidity : Int, wind : Int)
  implicit val weatherWrites: Writes[Weather] = (
    (JsPath \ "sunshine").write[Boolean] and
      (JsPath \ "temperature").write[Int] and
      (JsPath \ "humidity").write[Int] and
      (JsPath \ "wind").write[Int]
    )(unlift(Weather.unapply))

  implicit val qualityWrites: Writes[Quality] = (
    (JsPath \ "mature").write[Boolean] and
      (JsPath \ "sickness").write[Boolean]
    )(unlift(Quality.unapply))

  def pushInfo(url : String, sensor : String) = Action.async {
    implicit request => {
      val url = "http://localhost:9000/v1/posts/log"
      val csvreader = new CSVReader()
      val AsJson =  Json.toJson(csvreader.getWeather(sensor))
      val post = new HttpPost(url)
      val nameValuePairs = new ArrayList[NameValuePair]()
      nameValuePairs.add(new BasicNameValuePair("JSON", AsJson))
      post.setEntity(new UrlEncodedFormEntity(nameValuePairs))
    }
  }

  def index: Action[AnyContent] = Action.async { implicit request =>
    val r: Future[Result] = Future.successful(Ok("hello world"))
    r
  }

  def getstate : Action[AnyContent] = Action.async {
    implicit request => {
      val csvreader = new CSVReader()
      val r: Future[Result] = Future.successful(Ok(Json.toJson(csvreader.getState("csvjson/state.csv"))))
      r
    }
  }

  def getweather : Action[AnyContent] = Action.async {
    implicit request => {
      val csvreader = new CSVReader()
      val r: Future[Result] = Future.successful(Ok(Json.toJson(csvreader.getWeather("csvjson/weather.csv"))))
      r
    }
  }

  def getfruitQuality : Action[AnyContent] = Action.async {
    implicit request => {
      val csvreader = new CSVReader()
      val r: Future[Result] = Future.successful(Ok(Json.toJson(csvreader.getFruit("csvjson/quality.csv"))))
      r
    }
  }
}
