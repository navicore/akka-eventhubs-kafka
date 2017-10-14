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
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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


  Consumer
    .committableSource(consumerSettings, Subscriptions.topics(topic))
    .mapAsync(10) {
      val ehPublish = EhPublish()
      def publish(key: String, value: String): Future[Done] = {
        ehPublish(key, value)
        Future.successful(Done) //todo: make async in ehClient call
      }

      (msg: ConsumerMessage.CommittableMessage[Array[Byte], String]) =>
        logger.warn("ejs processing")
        val key = Option(msg.record.key()).getOrElse(msg.hashCode().toString.toArray).toString //todo fix bs
        publish(key, msg.record.value()).map(_ => msg)
    }
    .mapAsync(10) {
      (msg: ConsumerMessage.CommittableMessage[Array[Byte], String]) =>
        logger.warn("ejs commit")
        msg.committableOffset.commitScaladsl()
    }
    .runWith(Sink.ignore)
}
