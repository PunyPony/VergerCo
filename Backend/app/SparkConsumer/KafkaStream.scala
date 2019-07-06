//package kafkastream
//
//import javax.inject.{Inject, Named}
//import akka.actor.{ActorRef, ActorSystem}
//import scala.concurrent.{Await, Future}
//import scala.concurrent.ExecutionContext
//import scala.concurrent.duration._
//import controllers._
//import play.api.libs.ws._
//import play.api.mvc._
//import play.api.test._
//import play.api.libs.json._
//import play.api.libs.functional.syntax._
//import consumer._
//
//
//class KafkaStream @Inject() (actorSystem: ActorSystem)(implicit executionContext: ExecutionContext, controller: MyController) {
//
//  actorSystem.scheduler.scheduleOnce(delay=0.seconds) {
//    //Consumer.SaveStreamKafka
//  }
//}
