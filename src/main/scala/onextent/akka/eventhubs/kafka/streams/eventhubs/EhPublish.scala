package onextent.akka.eventhubs.kafka.streams.eventhubs

import akka.kafka.ConsumerMessage._
import com.microsoft.azure.eventhubs.{EventData, EventHubClient}
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.eventhubs.kafka.Conf

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Setup a reusable eventhubs client connection and return a
  * function that can be invvoked on each message read from
  * Kafka.
  */
object EhPublish extends LazyLogging with Conf {

  def apply(): CommittableMessage[Array[Byte], String] => Future[
    CommittableMessage[Array[Byte], String]] = {

    // reuse thie conn for all the invokes of the func returned below
    val ehClient: EventHubClient =
      EventHubClient.createFromConnectionStringSync(connStr)

    // returns a Future to enable back-pressure
    (msg: CommittableMessage[Array[Byte], String]) =>
      val value = msg.record.value()
      val key = new String(
        Option(msg.record.key())
          .getOrElse(value.hashCode().toString.getBytes("UTF8")))
      logger.debug(s"key: $key value: $value")
      val payloadBytes = value.getBytes("UTF-8")
      ehClient.send(new EventData(payloadBytes), key).toScala.map(_ => msg)
  }

}
