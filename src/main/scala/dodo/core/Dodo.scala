package dodo.core

import akka.actor.{ActorRef, Props, Actor}
import dodo.core.Dodo._

class Dodo extends Actor {
  var firehoseListeners: List[ActorRef] = Nil

  def receive = {
    case update: UpdateStatus     => dispatchUpdate(update)
    case follow: AddFollower      => dispatchFollow(follow)
    case SubscribeToFirehose      => subscribeToFirehose(sender)
    case getTimeline: GetTimeline => fetchTimeline(getTimeline)
  }

  def locateUser(user: String): ActorRef = {
    context.child(user).getOrElse {
      context.actorOf(User.props(user), user)
    }
  }

  def dispatchUpdate(update: UpdateStatus): Unit = {
    locateUser(update.user) ! update.status
    firehoseListeners foreach (_ ! update.status)
  }

  def dispatchFollow(follow: AddFollower): Unit = {
    val user = locateUser(follow.user)
    val follower = locateUser(follow.follower)
    user.tell(User.FollowsYou, follower)
  }

  def subscribeToFirehose(subscriber: ActorRef): Unit = {
    firehoseListeners = subscriber :: firehoseListeners
  }

  def fetchTimeline(getTimeline: GetTimeline): Unit = {
    locateUser(getTimeline.user).forward(getTimeline)
  }
}

object Dodo {
  case class Status(author: String, content: String)
  case class Follower(user: String)
  case class UpdateStatus(user: String, status: Status)
  case class AddFollower(user: String, follower: String)
  case class GetTimeline(user: String)
  case class Timeline(statuses: List[Status])

  case object SubscribeToFirehose

  def props: Props = Props[Dodo]
}
