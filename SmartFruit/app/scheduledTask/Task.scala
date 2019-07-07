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

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 3600.seconds) {
    println("Push Fruit Quality...")
    controller.pushFruitQuality
  }
  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 300.seconds) {
    println("Push Weather...")
    controller.pushWeather

  }
  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 300.seconds) {
    println("Push State...")
    controller.pushState
  }
}
