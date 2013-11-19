package dodo.core

import akka.actor.{ActorRef, Props, Actor}

class User(val userName: String) extends Actor {
  import User._

  var timeline: List[Dodo.Status] = Nil
  var followers: List[ActorRef] = Nil

  def receive = {
    case status: Dodo.Status            => storeStatus(status)
    case getTimeline: Dodo.GetTimeline  => sender ! Dodo.Timeline(timeline)
    case FollowsYou                     => addFollower(sender)
  }

  def storeStatus(status: Dodo.Status): Unit = {
    if(status.author == userName)
      broadcastToFollowers(status)

    addToTimeline(status)
  }

  def addToTimeline(status: Dodo.Status): Unit = {
    timeline = status :: timeline
  }

  def broadcastToFollowers(status: Dodo.Status): Unit = followers foreach (_ ! status)

  def addFollower(follower: ActorRef): Unit = {
    followers = follower :: followers
  }
}

object User {
  case object FollowsYou

  def props(userName: String): Props = Props[User](new User(userName))
}


