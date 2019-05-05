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

class CodeBlockTask @Inject() (actorSystem: ActorSystem)(implicit executionContext: ExecutionContext, controller: MyController) {

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 3.seconds) {
    // the block of code that will be executed
    println("Push Fruit Quality...")
    //controller.pushFruitQuality()
    //val request = new FakeRequest("fakeMethod", "fakeUrl", new FakeHeaders, "fakeBody")
//    val request : Request = new Request("lol")
//    val result = controller.pushFruitQuality.apply(request).run

  }
  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 300.seconds) {
    // the block of code that will be executed
    println("Push Weather...")
    controller.pushWeather()
  }
  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 300.seconds) {
    // the block of code that will be executed
    println("Push State...")
    controller.pushState()
  }
}