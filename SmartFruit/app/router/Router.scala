package controllers

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the PostResource controller.
  */
class Router @Inject()(controller: MyController) extends SimpleRouter {
  val prefix = ""

//  def link(id: PostId): String = {
//    import com.netaporter.uri.dsl._
//    val url = prefix / id.toString
//    url.toString()
//  }

  override def routes: Routes = {
    case GET(p"/state") =>
      controller.getState
    case GET(p"/weather") =>
      controller.getWeather
    case GET(p"/quality") =>
      controller.getFruitQuality
    case GET(p"/pushWeather") =>
      controller.pushWeather
    case GET(p"/pushState") =>
      controller.pushState
    case GET(p"/pushQuality") =>
      controller.pushFruitQuality
    case POST(p"/send/$topic") =>
      controller.send(topic)

  }

}
