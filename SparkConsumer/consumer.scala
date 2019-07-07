package consumer

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext
import org.apache.spark._
import org.apache.spark.sql._
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
//import org.apache.spark.streaming.dstream._
import org.apache.spark.sql.catalyst.util.DateTimeUtils
//import scala.sys.process.processInternal.InputStream
import play.api.libs.json._


//import play.api.libs.json._

//import kafkastream._
import org.apache.spark.streaming.Time
//import org.apache.kafka.common.TopicPartition


object Consumer {
  def writedDf(topic: String, data: String) = {
    import ss.implicits._

    println(Console.YELLOW + topic + ": " + data + Console.WHITE)

    //    val df = ss.read.json(Seq(data).toDS)
    //df.write.parquet("/tmp/data/"+topic+".parquet")

    val json = Json.parse(data)
    println(json.toString)
    topic match {
      case "state" => {
        val objectId = (json \ "objectID").asOpt[Int].get
        val chargeperc = (json \ "chargeperc").asOpt[Double].get
        val temperature = (json \ "temperature").asOpt[Double].get
        val placename = (json \ "place" \ "name").asOpt[String].get
        val lat = (json \ "place" \ "location" \ "lat").asOpt[Double].get
        val long = (json \ "place" \ "location" \ "long").asOpt[Double].get

        val someData = Seq(Row(objectId, chargeperc, temperature, placename, lat, long))

        val someSchema = List(
          StructField("id", IntegerType, true),
          StructField("perc", DoubleType, true),
          StructField("temperature", DoubleType, true),
          StructField("place", StringType, true),
          StructField("lat", DoubleType, true),
          StructField("long", DoubleType, true)
        )

        val df = ss.createDataFrame(
          ss.sparkContext.parallelize(someData),
          StructType(someSchema))

        df.write.mode("append").parquet("/tmp/data/" + topic + ".parquet")

      }
      case "weather" => {
        val objectId = (json \ "objectID").asOpt[Int].get
        val sunshine = (json \ "sunshine").asOpt[Boolean].get
        val temperature = (json \ "temperature").asOpt[Double].get
        val humidity = (json \ "humidity").asOpt[Double].get
        val wind = (json \ "wind").asOpt[Double].get

        val someData = Seq(Row(objectId, sunshine, temperature, humidity, wind))
        val someSchema = List(
          StructField("id", IntegerType, true),
          StructField("sunshine", BooleanType, true),
          StructField("temperature", DoubleType, true),
          StructField("humidity", StringType, true),
          StructField("wind", DoubleType, true)
        )

        val df = ss.createDataFrame(
          ss.sparkContext.parallelize(someData),
          StructType(someSchema))

        df.write.mode("append").parquet("/tmp/data/" + topic + ".parquet")
      }
      case "quality" => {
        val objectId = (json \ "objectID").asOpt[Int].get
        val mature = (json \ "mature").asOpt[Boolean].get
        val sickness = (json \ "sickness").asOpt[Boolean].get

        val someData = Seq(Row(objectId, mature, sickness))
        val someSchema = List(
          StructField("id", IntegerType, true),
          StructField("mature", BooleanType, true),
          StructField("sickness", BooleanType, true)
        )
        val df = ss.createDataFrame(
          ss.sparkContext.parallelize(someData),
          StructType(someSchema))
        df.write.mode("append").parquet("/tmp/data/" + topic + ".parquet")

      }
      case "alert" => {

        val objectId = (json \ "objectID").asOpt[Int].get
        val alertType = (json \ "alertType").asOpt[String].get

        val someData = Seq(Row(objectId, alertType))
        val someSchema = List(
          StructField("id", IntegerType, true),
          StructField("alertType", StringType, true)
        )
        val df = ss.createDataFrame(
          ss.sparkContext.parallelize(someData),
          StructType(someSchema))

        df.write.mode("append").parquet("/tmp/data/" + topic + ".parquet")
      }
    }

  }

  def main(args: Array[String]) = {
    import ss.implicits._
    stream.foreachRDD {
      rdd =>
        if (rdd.count > 0)
          rdd.foreach { record => writedDf(record.topic(), record.value()) }
    }


        //
        //    stream.foreachRDD { rdd =>
        //      // Get the offset ranges in the RDD
        //      rdd.foreach { record =>
        //        val value : String = record.value()
        //        println(value)
        //      }}

        //      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        //      for (o <- offsetRanges) {
        //        println(s"${o.topic} ${o.partition} offsets: ${o.fromOffset} to ${o.untilOffset}")
        //
        //      }
        //    }

        streamingContext.start

        while (true) {}

        // the above code is printing out topic details every 5 seconds
        // until you stop it.
        streamingContext.stop(stopSparkContext = false)
        sc.stop

    }


    val ss = SparkSession.builder().master("local[*]").appName("KafkaSparkConsummer").getOrCreate()
    val sc = ss.sparkContext
    val streamingContext = new StreamingContext(sc, Seconds(10))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("state", "weather", "quality", "alert")
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams))

    stream.map(record => (record.key, record.value))
}


