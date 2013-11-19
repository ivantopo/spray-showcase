package dodo.client

import spray.client.pipelining._
import akka.actor.ActorSystem
import dodo.core.Dodo.{Status, Timeline}
import spray.httpx.{SprayJsonSupport, RequestBuilding}
import scala.concurrent.Future
import akka.util.Timeout
import scala.concurrent.duration._
import dodo.api.JsonFormats
import spray.http._

object RestClient extends RequestBuilding with SprayJsonSupport with JsonFormats {
  implicit val system = ActorSystem("rest-client")
  implicit val ec = system.dispatcher
  implicit val timeout = Timeout(10 seconds)

  val basePipeline = sendReceive
  val timelinePipeline = sendReceive ~> unmarshal[Timeline]

  def getTimeline(user: String): Future[Timeline] = timelinePipeline {
    Get(s"http://localhost:9090/users/$user/timeline")
  }

  def postStatus(user: String, author: String, content: String): Future[HttpResponse] = basePipeline {
    Post(s"http://localhost:9090/users/$user/statuses", Status(author, content))
  }

}


