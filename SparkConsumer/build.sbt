scalaVersion in ThisBuild := "2.11.0"

libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "1.0.4"

libraryDependencies += "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.0.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.2.0"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.2.0"
libraryDependencies +=  "org.apache.spark" %% "spark-sql" % "2.2.0"
libraryDependencies +=  "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.2.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.4.0"