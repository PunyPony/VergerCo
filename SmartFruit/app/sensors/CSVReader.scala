package sensors

import java.io.File
import scala.io.Source
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class State(charge: Int, temperature: Int, place: Place)

case class Location(lat: Double, long: Double)

case class Place(name: String, location: Location)

case class Weather(sunshine: Boolean, temperature: Int, humidity: Int, wind: Int)

case class Quality(mature: Boolean, sickness: Boolean)

class CSVReader {
}

object CSVReader {

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

  def BoolFromString(value: String) = {
    value.toInt == 1
  }

  def checkFile(sensor: String): File = {
    val file = new File(sensor)
    if (!file.exists || file.isDirectory)
      throw new IllegalArgumentException("File doesn't exist.")
    return file
  }

  def getState(sensor: String): JsValue = {
    val file = checkFile(sensor)
    val content: Iterator[Array[String]] = Source.fromFile(sensor).getLines.map(_.split(",")).drop(1)
    val caseClass = content.toList.map(t => State(t(0).toInt, t(1).toInt, Place(t(4), Location(t(2).toDouble, t(3).toDouble))))
    Json.toJson(caseClass)
  }

  def getWeather(sensor: String): JsValue = {
    val file = checkFile(sensor)
    val content: Iterator[Array[String]] = Source.fromFile(sensor).getLines.map(_.split(",")).drop(1)
    val caseClass = content.toList.map(t => Weather(BoolFromString(t(0)), t(1).toInt, t(2).toInt, t(3).toInt))
    Json.toJson(caseClass)
  }

  def getFruit(sensor: String): JsValue = {
    val file = checkFile(sensor)
    val content: Iterator[Array[String]] = Source.fromFile(sensor).getLines.map(_.split(",")).drop(1)
    val caseClass = content.toList.map(t => Quality(BoolFromString(t(0)), BoolFromString(t(1))))
    Json.toJson(caseClass)
  }
}