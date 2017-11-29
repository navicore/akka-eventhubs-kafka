package onextent.akka.eventhubs.kafka

import com.typesafe.scalalogging.LazyLogging

object Main extends App with LazyLogging with Conf {

  if (ehMode == "PRODUCE") {
    logger.info("INIT kafka TO eventhubs")
    ReadKafkaWriteEventHubs()
  } else {
    logger.info("INIT kafka FROM eventhubs")
    ReadEventhubsWriteKafka()
  }
}
