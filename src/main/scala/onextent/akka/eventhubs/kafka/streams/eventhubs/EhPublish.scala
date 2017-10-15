package onextent.akka.eventhubs.kafka.streams.eventhubs

import com.microsoft.azure.eventhubs.{EventData, EventHubClient}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

import scala.compat.java8.FutureConverters._
import scala.concurrent.Future

object EhPublish extends LazyLogging {

  val conf: Config = ConfigFactory.load()
  val connStr: String = conf.getString("eventhubs.connStr")

  def apply(): (String, String) => Future[Void] = {

    val ehClient: EventHubClient =
      EventHubClient.createFromConnectionStringSync(connStr)

    (key: String, value: String) =>
      logger.debug(s"key: $key value: $value")
      val payloadBytes = value.getBytes("UTF-8")
      ehClient.send(new EventData(payloadBytes), key).toScala
  }
}
