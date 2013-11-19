package dodo.core

import akka.actor.{ActorRef, Props, Actor}

class DodoUser(val userName: String) extends Actor {
  import DodoUser._

  var timeline: List[Dodo.Status] = Nil
  var followers: List[ActorRef] = Nil
  var subscribers: List[ActorRef] = Nil

  def receive = {
    case status: Dodo.Status            => storeStatus(status)
    case getTimeline: Dodo.GetTimeline  => sender ! Dodo.Timeline(timeline)
    case ListensToYou                   => subscribe(sender)
    case FollowsYou                     => addFollower(sender)
  }

  def storeStatus(status: Dodo.Status): Unit = {
    if(status.author == userName)
      broadcastToFollowers(status)

    addToTimeline(status)
  }

  def addToTimeline(status: Dodo.Status): Unit = {
    timeline = status :: timeline
    broadcastToSubscribers(status)
  }

  def broadcastToSubscribers(status: Dodo.Status): Unit = subscribers foreach (_ ! status)

  def broadcastToFollowers(status: Dodo.Status): Unit = followers foreach (_ ! status)

  def subscribe(subscriber: ActorRef): Unit = {
    subscribers = subscriber :: subscribers
  }

  def addFollower(follower: ActorRef): Unit = {
    followers = follower :: followers
  }
}

object DodoUser {
  case object FollowsYou
  case object ListensToYou

  def props(userName: String): Props = Props[DodoUser](new DodoUser(userName))
}


