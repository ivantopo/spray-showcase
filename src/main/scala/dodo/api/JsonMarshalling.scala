package dodo.api

import spray.json.DefaultJsonProtocol
import dodo.core.Dodo._

trait JsonFormats extends DefaultJsonProtocol {

  implicit val statusJF = jsonFormat2(Status)
  implicit val followerJF = jsonFormat1(Follower)
  implicit val updateStatusJF =  jsonFormat2(UpdateStatus)
  implicit val addFollowerJF =  jsonFormat2(AddFollower)
  implicit val timelineJF = jsonFormat1(Timeline)

}
