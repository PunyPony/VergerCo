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

import sensors.CSVReader
import sensors.Weather
import sensors.State
import sensors.Location
import sensors.Place

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
    (JsPath \ "sunshine").write[Int] and
      (JsPath \ "temperature").write[Int] and
      (JsPath \ "humidity").write[Int] and
      (JsPath \ "wind").write[Int]
    )(unlift(Weather.unapply))

//  def fileisgood(file : File) = {
//    if (!file.exists || file.isDirectory)
//      throw new IllegalArgumentException("File doesn't exist.")
//  }
//
//  def getState(sensor: String) : List[State] =
//  {
//    val file = new File(sensor)
//    val content: Iterator[Array[String]] = Source.fromFile(sensor).getLines.map(_.split(",")).drop(1)
//    content.toList.map( t => State(t(0).toInt, t(1).toInt, Place(t(4),Location(t(2).toDouble, t(3).toDouble))))
//  }
//
//  def getWeather(sensor: String) : List[Weather] =
//  {
//    val file = new File(sensor)
//    val content: Iterator[Array[String]] = Source.fromFile(sensor).getLines.map(_.split(",")).drop(1)
//    content.toList.map( t => Weather(t(0).toInt, t(1).toInt, t(2).toInt, t(3).toInt))
//  }


//  def index: Action[AnyContent] = Action { implicit request =>
//    val r: Result = Ok("hello world")
//    r
//  }

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


}
