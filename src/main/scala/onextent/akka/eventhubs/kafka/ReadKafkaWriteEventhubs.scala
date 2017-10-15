package onextent.akka.eventhubs.kafka

import akka.Done
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerMessage, ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.eventhubs.kafka.streams.eventhubs.EhPublish
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{
  ByteArrayDeserializer,
  StringDeserializer
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ReadKafkaWriteEventhubs extends LazyLogging {

  def apply(): Future[Done] = {

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

    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      .mapAsync(10) {
        val ehPublish = EhPublish()
        (msg: ConsumerMessage.CommittableMessage[Array[Byte], String]) =>
          val key = new String(
            Option(msg.record.key()).getOrElse(msg.hashCode().toString.getBytes("UTF8")))
          ehPublish(key, msg.record.value()).map(_ => msg)
      }
      .mapAsync(10) {
        (msg: ConsumerMessage.CommittableMessage[Array[Byte], String]) =>
          msg.committableOffset.commitScaladsl()
      }
      .runWith(Sink.ignore)
  }
}
