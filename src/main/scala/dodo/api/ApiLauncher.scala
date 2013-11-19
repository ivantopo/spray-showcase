package dodo.api

import akka.actor.{Props, ActorSystem}
import dodo.core.Dodo
import akka.io.IO
import spray.can.Http
import spray.routing._

object ApiLauncher extends App with Routes {
  val system = ActorSystem("dodo")
  val dodoManager = system.actorOf(Dodo.props, "manager")

  val httpService = system.actorOf(ApiHttpService.props(route(dodoManager, system)(system.dispatcher)))
  IO(Http)(system) ! Http.Bind(listener = httpService, interface = "localhost", port = 9090)
}

class ApiHttpService(route: Route) extends HttpServiceActor {
  def receive = runRoute(route)
}

object ApiHttpService {
  def props(route: Route): Props = Props[ApiHttpService](new ApiHttpService(route))
}
