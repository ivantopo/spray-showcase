package dodo.api

import spray.routing.{HttpServiceActor, Directives, Route}
import akka.actor.{ActorRefFactory, Props, ActorSystem, ActorRef}
import dodo.core.Dodo
import akka.io.IO
import spray.can.Http
import dodo.core.Dodo._
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import spray.httpx.SprayJsonSupport
import scala.concurrent.ExecutionContext
import spray.http._
import spray.http.HttpResponse
import spray.http.ChunkedResponseStart
import spray.http.HttpResponse
import dodo.core.Dodo.GetTimeline
import spray.http.ChunkedResponseStart
import dodo.core.Dodo.Timeline

trait Routes extends Directives with SprayJsonSupport with JsonFormats {

  implicit val timeout = Timeout(10 seconds)

  def route(dodoService: ActorRef, actorFactory: ActorRefFactory)(implicit ec: ExecutionContext): Route = {
    pathPrefix("users" / Segment) { user =>
      get {
        path("timeline") {
          complete {
            (dodoService ? GetTimeline(user)).mapTo[Timeline]
          }
        }
      } ~
      post {
        path("statuses") {
          entity(as[Status]) { status =>
            complete {
              dodoService ! UpdateStatus(user, status)
              StatusCodes.Accepted
            }
          }
        } ~
        path("followers") {
          entity(as[Follower]) { follower =>
            complete {
              dodoService ! AddFollower(user, follower.user)
              StatusCodes.Accepted
            }
          }
        }
      }
    } ~
    get {
      path("firehose") { ctx =>
        val streamer = actorFactory.actorOf(Streamer.props(ctx.responder))
        dodoService.tell(SubscribeToFirehose, streamer)
      }
    }
  }
}