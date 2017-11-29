package onextent.akka.eventhubs.kafka

import akka.kafka.scaladsl.Producer
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.microsoft.azure.reactiveeventhubs.SourceOptions
import com.microsoft.azure.reactiveeventhubs.config.{
  Configuration,
  IConfiguration
}
import com.microsoft.azure.reactiveeventhubs.scaladsl.EventHub
import org.apache.kafka.clients.producer.ProducerRecord

object ReadEventhubsWriteKafka extends Conf {

  def apply(): Unit = {

    val config: IConfiguration = Configuration(conf.getConfig("eventhubs-1"))

    EventHub(config)
      .source(SourceOptions().fromSavedOffsets().saveOffsets())
      .map { msg =>
        {  // use eh partition number as partition key
          (msg.runtimeInfo.partitionInfo.partitionNumber.toString
             .getBytes("UTF8"),
           msg.contentAsString)
        }
      }
      .map { elem: (Array[Byte], String) =>
        new ProducerRecord(topic, elem._1, elem._2)
      }
      .runWith(Producer.plainSink(producerSettings))

  }

}
