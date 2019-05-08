package sensors

import java.io.File
import scala.io.Source
import javax.inject.{Inject, Named}
import akka.actor.{ActorRef, ActorSystem}
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import v1.post._
import play.api.libs.ws._
import play.api.mvc._
import play.api.test._
import play.api.libs.json._
import sensors._
import play.api.libs.functional.syntax._


case class State(objectID: Int, charge: Float, temperature: Float, place: Place)

case class Location(lat: Double, long: Double)

case class Place(name: String, location: Location)

case class Weather(objectID: Int, sunshine: Boolean, temperature: Float, humidity: Float, wind: Float)

case class FruitQuality(objectID: Int, mature: Boolean, sickness: Boolean)



class CSVReader @Inject()(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext, controller: MyController) {

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
    (JsPath \ "objectID").write[Int] and
      (JsPath \ "chargeperc").write[Float] and
      (JsPath \ "temperature").write[Float] and
      (JsPath \ "place").write[Place]
    ) (unlift(State.unapply))

  implicit val weatherWrites: Writes[Weather] = (
    (JsPath \ "objectID").write[Int] and
      (JsPath \ "sunshine").write[Boolean] and
      (JsPath \ "temperature").write[Float] and
      (JsPath \ "humidity").write[Float] and
      (JsPath \ "wind").write[Float]
    ) (unlift(Weather.unapply))

  implicit val qualityWrites: Writes[FruitQuality] = (
    (JsPath \ "objectID").write[Int] and
      (JsPath \ "mature").write[Boolean] and
      (JsPath \ "sickness").write[Boolean]
    ) (unlift(FruitQuality.unapply))



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
    val caseClass = content.toList.map(t => State(0, t(0).toFloat, t(1).toFloat, Place(t(4), Location(t(2).toDouble, t(3).toDouble))))
    Json.toJson(caseClass)
  }

  def getWeather(sensor: String): JsValue = {
    val file = checkFile(sensor)
    val content: Iterator[Array[String]] = Source.fromFile(sensor).getLines.map(_.split(",")).drop(1)
    val caseClass = content.toList.map(t => Weather(0, BoolFromString(t(0)), t(1).toFloat, t(2).toFloat, t(3).toFloat))
    Json.toJson(caseClass)
  }

  def getFruit(sensor: String): JsValue = {
    val file = checkFile(sensor)
    val content: Iterator[Array[String]] = Source.fromFile(sensor).getLines.map(_.split(",")).drop(1)
    val caseClass = content.toList.map(t => FruitQuality(0, BoolFromString(t(0)), BoolFromString(t(1))))
    Json.toJson(caseClass)
  }
}