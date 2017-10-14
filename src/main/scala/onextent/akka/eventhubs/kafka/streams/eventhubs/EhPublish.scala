package onextent.akka.eventhubs.kafka.streams.eventhubs

import com.microsoft.azure.eventhubs.{EventData, EventHubClient}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

object EhPublish extends LazyLogging {

  val conf: Config = ConfigFactory.load()
  val connStr: String = conf.getString("eventhubs.connStr")

  def apply(): (String, String) => Unit = {

    val ehClient: EventHubClient =
      EventHubClient.createFromConnectionStringSync(connStr)

    def publish(key: String, value: String): Unit = {

      logger.warn("ejs got key: " + key)
      logger.warn("ejs got value: " + value)

      val payloadBytes = value.getBytes("UTF-8")
      ehClient.sendSync(new EventData(payloadBytes), key)
    }
    publish
  }
}
