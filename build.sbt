name := "slick"

version := "1.0"

scalaVersion := "2.12.4"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ywarn-dead-code",
  "-Xlint",
  "-Xfatal-warnings"
)

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "com.h2database"      % "h2"              % "1.4.185",
  "ch.qos.logback"      % "logback-classic" % "1.1.2"
)

//val initStr = """
//                                |import slick.driver.H2Driver.api._
//                                |import scala.concurrent._
//                                |import scala.concurrent.duration._
//                                |import scala.concurrent.ExecutionContext.Implicits.global
//                                |repl.prompt() = "scala> "
//                              """.trim.stripMargin
//
//initialCommands in console := s"""
//                                 |ammonite.repl.Repl.run(\"\"\"$initStr\"\"\")
//""".trim.stripMargin