import sbt.Keys._

lazy val GatlingTest = config("gatling") extend Test

scalaVersion in ThisBuild := "2.12.7"

libraryDependencies += guice
libraryDependencies += "org.joda" % "joda-convert" % "2.1.2"
libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "5.2"

libraryDependencies += "com.netaporter" %% "scala-uri" % "0.4.16"
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.1"
libraryDependencies += "com.typesafe.play" %% "play-iteratees" % "2.6.1"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.0.1.1" % Test
libraryDependencies += "io.gatling" % "gatling-test-framework" % "3.0.1.1" % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.197"
libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.2"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.8" % Test
)

libraryDependencies += "org.apache.spark" % "spark-streaming-kafka-0-10_2.12" % "2.4.3"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.3"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.4.3"
libraryDependencies +=  "org.apache.spark" %% "spark-sql" % "2.4.3"
libraryDependencies +=  "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.4.3"
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.5.23"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "1.0.4"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.9"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.9"
libraryDependencies +=  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.9.9"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.9"

libraryDependencies += ws
libraryDependencies += ehcache
libraryDependencies += jdbc
libraryDependencies += guice
libraryDependencies += evolutions

// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala, GatlingPlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    name := """Backend""",
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation"
  )

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/index.html
lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
  settings(
    paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-rest-api")
  )
