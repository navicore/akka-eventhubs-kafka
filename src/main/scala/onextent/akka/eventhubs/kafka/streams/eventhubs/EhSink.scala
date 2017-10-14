package onextent.akka.eventhubs.kafka.streams.eventhubs

import akka.Done
import akka.actor._
import akka.stream.scaladsl.Sink
import com.microsoft.azure.eventhubs.{EventData, EventHubClient}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.json4s._

import scala.concurrent.Future

object EhSink extends LazyLogging {

  val conf: Config = ConfigFactory.load()
  val connStr: String = conf.getString("eventhubs.connStr")
  val ehClient: EventHubClient = EventHubClient.createFromConnectionStringSync(connStr)

  def apply(
      implicit context: ActorContext): Sink[(String, String), Future[Done]] = {

    implicit val formats: DefaultFormats.type = DefaultFormats

    Sink.foreach[(String, String)] {
      case (key, value) =>
        val payloadBytes = value.getBytes("UTF-8")
        ehClient.sendSync(new EventData(payloadBytes) , key)
    }
  }
}