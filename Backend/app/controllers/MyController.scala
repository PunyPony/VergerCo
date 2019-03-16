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


class MyController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index: Action[AnyContent] = Action.async { implicit request =>
    val r: Future[Result] = Future.successful(Ok("hello world"))
    r
  }

  def sayHello = Action(parse.json) { request =>
    (request.body \ "name").asOpt[String].map { name =>
      Ok("Hello " + name)
    }.getOrElse {
      BadRequest("Missing parameter [name]")
    }
  }

}
