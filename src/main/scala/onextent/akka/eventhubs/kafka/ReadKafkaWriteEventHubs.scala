package onextent.akka.eventhubs.kafka

import akka.Done
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerMessage, Subscriptions}
import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.eventhubs.kafka.streams.eventhubs.EhPublish

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ReadKafkaWriteEventHubs extends LazyLogging with Conf {

  def apply(): Future[Done] = {
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      .mapAsync(parallelism) {
        val ehPublish = EhPublish()
        (msg: ConsumerMessage.CommittableMessage[Array[Byte], String]) =>
          val key = new String(Option(msg.record.key())
            .getOrElse(msg.hashCode().toString.getBytes("UTF8")))
          ehPublish(key, msg.record.value()).map(_ => msg)
      }
      .mapAsync(parallelism) {
        (msg: ConsumerMessage.CommittableMessage[Array[Byte], String]) =>
          msg.committableOffset.commitScaladsl()
      }
      .runWith(Sink.ignore)
  }
}
