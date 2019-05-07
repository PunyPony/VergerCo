package tasks


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

class CodeBlockTask @Inject() (actorSystem: ActorSystem)(implicit executionContext: ExecutionContext, controller: MyController) {

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

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 3600.seconds) {
    // the block of code that will be executed
//    println("Push Fruit Quality...")
//    val jsonSensor = Json.toJson(CSVReader.getWeather("csvjson/weather.csv"))
//    val response = controller.PushInfo("http://localhost:9000/v1/posts/processState", jsonSensor)

  }
  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 300.seconds) {
    // the block of code that will be executed
    println("Push Weather...")
    val jsonSensor = Json.toJson(CSVReader.getWeather("csvjson/weather.csv"))
    val response = controller.PushInfo("http://localhost:9000/v1/posts/processWeather", jsonSensor)
  }
  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 300.seconds) {
    // the block of code that will be executed
    println("Push State...")
    controller.pushState()
  }
}