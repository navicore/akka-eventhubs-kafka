package onextent.akka.eventhubs.kafka

import akka.kafka.{ConsumerSettings, ProducerSettings}
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

trait Conf {

  val conf: Config = ConfigFactory.load()

  val op: String = conf.getString("main.op")

  val bootstrap: String = conf.getString("kafka.bootstrap")
  val consumerGroup: String = conf.getString("kafka.consumerGroup")
  val topic: String = conf.getString("kafka.topic")
  val parallelism: Int = conf.getInt("kafka.parallelism")
  val consumerSettings: ConsumerSettings[Array[Byte], String] = ConsumerSettings(actorSystem,
    new ByteArrayDeserializer,
    new StringDeserializer)
    .withBootstrapServers(bootstrap)
    .withGroupId(consumerGroup)
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val connStr: String = conf.getString("eventhubs.connStr")

  val producerSettings: ProducerSettings[Array[Byte], String] =
    ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(bootstrap)

}
