package tasks

import javax.inject.{Inject, Named}
import akka.actor.{ActorRef, ActorSystem}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class CodeBlockTask @Inject() (actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) {
  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 1000.seconds) {
  // the block of code that will be executed
  print("Executing something...")
  }
}