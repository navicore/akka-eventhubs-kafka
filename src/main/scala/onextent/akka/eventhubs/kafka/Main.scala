package onextent.akka.eventhubs.kafka

import com.typesafe.scalalogging.LazyLogging

object Main extends App with LazyLogging with Conf {

  if (op == "READ_KAFKA") {
    logger.info("INIT KAFKA to eh")
    ReadKafkaWriteEventHubs()
  } else {
    logger.info("INIT EVENTHUB to kafka")
    ReadEventhubsWriteKafka()
  }
}
