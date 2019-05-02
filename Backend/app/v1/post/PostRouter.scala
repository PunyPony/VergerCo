package v1.post

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the PostResource controller.
  */
class PostRouter @Inject()(controller: MyController) extends SimpleRouter {
  val prefix = "v1/posts"

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index
    case GET(p"/weather") =>
      controller.getWeather
    case GET(p"/state") =>
      controller.getState
    case GET(p"/qualityFruit") =>
      controller.getQualityFruit
    case GET(p"/processJson") =>
      controller.processJson
    case GET(p"/processWeather") =>
      controller.processWeather
    case GET(p"/processState") =>
      controller.processState
    case GET(p"/processQualityFruit") =>
      controller.processQualityFruit
  }

}
