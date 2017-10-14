package onextent.akka.eventhubs.kafka

import akka.kafka.{ConsumerMessage, ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

object Main extends App with LazyLogging {

  val conf: Config = ConfigFactory.load()
  val bootstrap: String = conf.getString("kafka.bootstrap")
  val consumerGroup: String = conf.getString("kafka.consumerGroup")
  val topic: String = conf.getString("kafka.topic")

  val consumerSettings = ConsumerSettings(actorSystem,
                                          new ByteArrayDeserializer,
                                          new StringDeserializer)
    .withBootstrapServers(bootstrap)
    .withGroupId(consumerGroup)
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  Consumer.committableSource(consumerSettings, Subscriptions.topics(topic))
    .mapAsync(1) { (msg: ConsumerMessage.CommittableMessage[Array[Byte], String]) =>
      logger.warn("ejs got key: " + msg.record.key())
      logger.warn("ejs got value: " + msg.record.value())
      msg.committableOffset.commitScaladsl()
    }
    .runWith(Sink.ignore)
}
