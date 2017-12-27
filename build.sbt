name := "slick"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "com.h2database"      % "h2"              % "1.4.185"
//  ,
//  "ch.qos.logback"      % "logback-classic" % "1.1.2"
)