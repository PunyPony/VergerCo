package services

import javax.inject._

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

import akka.kafka._
import akka.kafka.scaladsl._
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization._


@Singleton
class Kafka @Inject()(
                       system: ActorSystem,
                       config: KafkaConfiguration,
                     )(implicit materializer: Materializer) {

  def sendMessage(topic: String, message: String) = {
    val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(config.producerServer)

    Source.single(message)
      .map { elem =>
        new ProducerRecord[Array[Byte], String](topic, elem)
      }
      .runWith(Producer.plainSink(producerSettings))
  }

}