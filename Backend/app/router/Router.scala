package controllers

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the PostResource controller.
  */
class Router @Inject()(controller: MyController) extends SimpleRouter {
  val prefix = "v1/posts"

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index
    case GET(p"/weather") =>
      controller.getWeather
    case GET(p"/state") =>
      controller.getState
    case GET(p"/quality") =>
      controller.getFruitQuality
    case POST(p"/processWeather") =>
      controller.processWeather
    case POST(p"/processState") =>
      controller.processState
    case POST(p"/processQuality") =>
      controller.processFruitQuality
    case POST(p"/processAlert") =>
      controller.processAlert
  }

}
