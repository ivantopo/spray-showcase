package dodo.core

import akka.actor.{ActorRef, Props, Actor}
import dodo.core.Dodo._

class Dodo extends Actor {
  var firehoseListeners: List[ActorRef] = Nil

  def receive = {
    case update: UpdateStatus             => dispatchUpdate(update)
    case follow: AddFollower              => dispatchFollow(follow)
    case subsUser: SubscribeToUserStream  => dispatchSubscribeToUser(subsUser)
    case SubscribeToFirehose              => subscribeToFirehose(sender)
  }

  def locateUser(user: String): ActorRef = {
    context.child(user).getOrElse {
      context.actorOf(DodoUser.props(user), user)
    }
  }

  def dispatchUpdate(update: UpdateStatus): Unit = {
    locateUser(update.user) ! update.status
    firehoseListeners foreach (_ ! update.status)
  }

  def dispatchFollow(follow: AddFollower): Unit = {
    val user = locateUser(follow.user)
    val follower = locateUser(follow.follower)
    user.tell(DodoUser.FollowsYou, follower)
  }

  def dispatchSubscribeToUser(subscribe: SubscribeToUserStream): Unit = locateUser(subscribe.user) ! subscribe

  def subscribeToFirehose(subscriber: ActorRef): Unit = {
    firehoseListeners = subscriber :: firehoseListeners
  }
}


object Dodo {
  case class Status(author: String, content: String)

  case class UpdateStatus(user: String, status: Status)
  case class AddFollower(user: String, follower: String)
  case class GetTimeline(user: String)
  case class Timeline(statuses: List[Status])

  case object SubscribeToFirehose
  case class SubscribeToUserStream(user: String)

  def props: Props = Props[Dodo]
}
