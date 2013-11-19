package dodo.core

import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.WordSpecLike

class UserSpec extends TestKit(ActorSystem("dodo-user-spec")) with WordSpecLike with ImplicitSender {

  "a DodoUser" should {
    "append own statuses to timeline" in new UserFixture {

      user ! testUserStatus1
      user ! testUserStatus2
      user ! Dodo.GetTimeline("test")

      expectMsg(Dodo.Timeline(testUserStatus2 :: testUserStatus1 :: Nil))
    }

    "append following statuses to timeline" in new UserFixture {

      user ! otherUserStatus1
      user ! otherUserStatus2
      user ! Dodo.GetTimeline("testuser")

      expectMsg(Dodo.Timeline(otherUserStatus2 :: otherUserStatus1 :: Nil))
    }

    "forward own statuses to followers" in new UserFixture {
      user ! User.FollowsYou
      user ! testUserStatus1

      expectMsg(testUserStatus1)
    }

    "not forward statuses not created by itself to followers" in new UserFixture {
      user ! User.FollowsYou
      user ! otherUserStatus1

      expectNoMsg()
    }
  }

  trait UserFixture {
    val user = system.actorOf(User.props("testuser"))

    val testUserStatus1 = Dodo.Status("testuser", "hello world 1")
    val testUserStatus2 = Dodo.Status("testuser", "hello world 2")
    val otherUserStatus1 = Dodo.Status("otheruser", "hello world 1")
    val otherUserStatus2 = Dodo.Status("otheruser", "hello world 2")
  }

}
