package dodo.api

import akka.actor.{Props, ActorRef, Actor}
import spray.http.StatusCodes._
import spray.http.{MessageChunk, HttpResponse, ChunkedResponseStart}
import dodo.core.Dodo.Status
import spray.json._

class Streamer(client: ActorRef) extends Actor with JsonFormats {
  client ! ChunkedResponseStart(HttpResponse(OK))

  def receive = {
    case status: Status => client ! MessageChunk(status.toJson.prettyPrint)
  }
}

object Streamer {
  def props(client: ActorRef): Props = Props[Streamer](new Streamer(client))
}
