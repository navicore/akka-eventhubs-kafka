package onextent.akka.eventhubs.consumer.models

final case class Body(body: String)

final case class EhEnvelop(contents: Body)

