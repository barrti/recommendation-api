name := "recapi"

version := "1.0"

lazy val `recapi` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  ehcache,
  ws,
  specs2 % Test,
  guice,
  "org.postgresql" % "postgresql" % "42.1.4",
  "com.typesafe.play" %% "play-slick" % "3.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.1"
)
unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

      