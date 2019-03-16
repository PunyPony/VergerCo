package v1.post

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the PostResource controller.
  */
class PostRouter @Inject()(controller: MyController) extends SimpleRouter {
  val prefix = "/v1/posts"

  def link(id: PostId): String = {
    import com.netaporter.uri.dsl._
    val url = prefix / id.toString
    url.toString()
  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index
    case GET(p"/state") =>
      controller.getstate
    case GET(p"/weather") =>
      controller.getweather
//
//    case POST(p"/") =>
//      controller.process
//
//    case GET(p"/$id") =>
//      controller.show(id)
  }

}
