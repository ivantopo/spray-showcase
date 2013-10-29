name := "spray-showcase"

version := "1.0"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "io.spray" % "spray-can" % "1.2-RC1",
  "io.spray" % "spray-client" % "1.2-RC1",
  "io.spray" % "spray-http" % "1.2-RC1",
  "io.spray" % "spray-httpx" % "1.2-RC1",
  "io.spray" % "spray-routing" % "1.2-RC1",
  "io.spray" % "spray-testkit" % "1.2-RC1" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.3" % "test",
  "org.scalatest" %% "scalatest" % "2.0.RC2" % "test"
)