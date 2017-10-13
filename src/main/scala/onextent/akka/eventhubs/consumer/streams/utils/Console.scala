package onextent.akka.eventhubs.consumer.streams.utils

import akka.Done
import akka.stream.scaladsl.Sink
import com.microsoft.azure.reactiveeventhubs.EventHubsMessage
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

object Console extends LazyLogging {

  def apply(): Sink[EventHubsMessage, Future[Done]] =
    Sink.foreach[EventHubsMessage] { m =>
      logger.debug(
        s"enqueued-timeh ${m.received}, offset: ${m.offset}, payload: ${m.contentAsString}")
    }
}
