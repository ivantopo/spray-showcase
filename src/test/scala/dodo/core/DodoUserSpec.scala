package dodo.core

import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.WordSpecLike

class DodoUserSpec extends TestKit(ActorSystem("dodo-user-spec")) with WordSpecLike with ImplicitSender {

  "a DodoUser" should {
    "append own statuses to timeline" in new UserFixture {
      val status1 = Dodo.Status("test", "status 1")
      val status2 = Dodo.Status("test", "status 2")

      user ! status1
      user ! status2
      user ! Dodo.GetTimeline("test")

      expectMsg(Dodo.Timeline(status2 :: status1 :: Nil))
    }

    "append following statuses to timeline" in new UserFixture {
      val status1 = Dodo.Status("scala", "status 1")
      val status2 = Dodo.Status("scala", "status 2")

      user ! status1
      user ! status2
      user ! Dodo.GetTimeline("test")

      expectMsg(Dodo.Timeline(status2 :: status1 :: Nil))
    }

    "forward new statuses to followers" in new UserFixture {
      user ! DodoUser.FollowsYou
      user ! Dodo.Status("test", "hello world")

      expectMsg(Dodo.Status("test", "hello world"))
    }

    "not forward statuses not created by him to followers" in new UserFixture {
      user ! DodoUser.FollowsYou
      user ! Dodo.Status("scala", "hello world")

      expectNoMsg()
    }
  }

  trait UserFixture {
    val user = system.actorOf(DodoUser.props("test"))
  }

}
