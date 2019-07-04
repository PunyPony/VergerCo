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
import org.apache.spark.streaming.Time
import org.apache.kafka.common.TopicPartition


object Consummer {
  def main(args: Array[String]) {
    println("Salut")
    stream.foreachRDD { rdd =>
      // Get the offset ranges in the RDD
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      for (o <- offsetRanges) {
        println(s"${o.topic} ${o.partition} offsets: ${o.fromOffset} to ${o.untilOffset}")
      }
    }

    streamingContext.start

      // the above code is printing out topic details every 5 seconds
      // until you stop it.
    //ssc.stop(stopSparkContext = false)

  }

  val ss = SparkSession.builder().master("local[*]").appName("KafkaSparkConsummer").getOrCreate()
  val sc = ss.sparkContext
  val streamingContext = new StreamingContext(sc, Seconds(2))

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "localhost:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "use_a_separate_group_id_for_each_stream",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  val topics = Array("state")
//  val stream = KafkaUtils.createDirectStream[String, String](
//    streamingContext,
//    PreferConsistent,
//    Subscribe[String, String](topics, kafkaParams)
//
  val preferredHosts = LocationStrategies.PreferConsistent
  val offsets = Map(new TopicPartition("state", 0) -> 2L)
  val stream = KafkaUtils.createDirectStream[String, String](
    streamingContext,
    preferredHosts,
    ConsumerStrategies.Subscribe[String, String](topics, kafkaParams, offsets))

  stream.map(record => (record.key, record.value))
}


