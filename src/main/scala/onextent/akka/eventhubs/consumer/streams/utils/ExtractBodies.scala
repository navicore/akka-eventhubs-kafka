package onextent.akka.eventhubs.consumer.streams.utils

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.microsoft.azure.reactiveeventhubs.EventHubsMessage
import onextent.akka.eventhubs.consumer.models.EhEnvelop
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, _}

object ExtractBodies {

  implicit val formats: DefaultFormats.type = DefaultFormats
  def apply(contains: String): Flow[EventHubsMessage, String, NotUsed] =
    Flow[EventHubsMessage]
      .map(e => parse(e.contentAsString).extract[EhEnvelop].contents.body)
      .filter(_.contains(contains))
}
