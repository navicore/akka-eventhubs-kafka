package onextent.akka.eventhubs.kafka.models

final case class Body(body: String)

final case class EhEnvelop(contents: Body)

