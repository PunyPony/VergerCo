package services

import javax.inject._
import play.api.Configuration

@Singleton
class KafkaConfiguration @Inject()(
                                    configuration: Configuration
                                  ) {

  private val defaultServer = "localhost:9092"

  lazy val producerServer : String ="localhost:9092" //consumerConfig.get[String]("server")
  lazy val consumerServer : String = producerServer// consumerConfig.get[String]("server")

  lazy val consumerClientId = "play-kafka-eventbus" // consumerConfig.get[String]("clientId") //.flatMap(_.get[String]("clientId")))

  private val producerConfig : Configuration = configuration.get[Configuration]("kafka.producer")
  private val consumerConfig : Configuration = configuration.get[Configuration]("kafka.consumer")

}