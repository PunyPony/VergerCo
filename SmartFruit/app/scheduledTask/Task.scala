package tasks


import javax.inject.{Inject, Named}
import akka.actor.{ActorRef, ActorSystem}
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import controllers._
import play.api.libs.ws._
import play.api.mvc._
import play.api.test._
import play.api.libs.json._
import sensors._
import confreader._
import play.api.libs.functional.syntax._

class CodeBlockTask @Inject() (actorSystem: ActorSystem)(implicit executionContext: ExecutionContext, controller: MyController) {

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 5.seconds) {
    println("Push Fruit Quality...")
    val jsonSensor = CSVReader.getFruit()
    val url = confReader.getURL()
    val response = controller.PushInfo(url+"processQuality", jsonSensor, "quality")
  }
  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 2.seconds) {
    println("Push Weather...")
    val jsonSensor = CSVReader.getWeather()
    val url = confReader.getURL()
    val response = controller.PushInfo(url+"processWeather", jsonSensor, "weather")

  }
  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 2.seconds) {
    println("Push State...")
    val jsonSensor = CSVReader.getState()
    val url = confReader.getURL()
    val response = controller.PushInfo(url+"processState", jsonSensor, "state")
  }
}
