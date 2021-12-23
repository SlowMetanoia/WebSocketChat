name := "ChatRoom"

version := "1.0"

lazy val `chatroom` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice)

val AkkaVersion = "2.6.8"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies ++= Seq(
  "org.postgresql"    %   "postgresql"          % "9.4-1201-jdbc41",
  "org.scalikejdbc" %% "scalikejdbc"         % "3.5.0",
  "org.scalikejdbc" %% "scalikejdbc-config"  % "3.5.0",
  "com.h2database"  %  "h2"                  % "1.4.200",
  "ch.qos.logback"  %  "logback-classic"     % "1.2.3"
)