package onextent.akka.eventhubs.kafka

import akka.kafka.{ConsumerSettings, ProducerSettings}
import com.microsoft.azure.reactiveeventhubs.ResumeOnError._
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

trait Conf {

  val conf: Config = ConfigFactory.load()

  val ehMode: String = conf.getString("eventhubs.ehMode")

  val bootstrap: String = conf.getString("kafka.bootstrap")
  val consumerGroup: String = conf.getString("kafka.consumerGroup")
  val topic: String = conf.getString("kafka.topic")
  val parallelism: Int = conf.getInt("kafka.parallelism")
  val consumerSettingsPlain: ConsumerSettings[Array[Byte], String] = ConsumerSettings(actorSystem,
    new ByteArrayDeserializer,
    new StringDeserializer)
    .withBootstrapServers(bootstrap)
    .withGroupId(consumerGroup)
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val trustStorePath = conf.getString("trustStorePath")
  val keyStorePath = conf.getString("keyStorePath")
  val trustStorePassword =  conf.getString("trustStorePassword")
  val keyStorePassword =  conf.getString("keyStorePassword")
  val keyPassword = conf.getString("keyPassword")
  val securityProtocol = conf.getString("securityProtocol")
  val saslMechanism = conf.getString("saslMechanism")

  lazy val consumerSettingsWithTLS: ConsumerSettings[Array[Byte], String] =
    consumerSettingsPlain
    .withProperty("ssl.truststore.location", trustStorePath)
    .withProperty("ssl.truststore.password", trustStorePassword)
    .withProperty("ssl.keystore.location", keyStorePath)
    .withProperty("ssl.keystore.password", keyStorePassword)
    .withProperty("ssl.key.password", keyPassword)
    .withProperty("security.protocol", securityProtocol)
    .withProperty("sasl.mechanism", saslMechanism)

  val consumerSettings:ConsumerSettings[Array[Byte], String]  = {
    if (conf.getBoolean("kafka.useTLS")) consumerSettingsWithTLS
    else consumerSettingsPlain
  }

  val connStr: String = conf.getString("eventhubs.connStr")

  val producerSettingsPlain: ProducerSettings[Array[Byte], String] =
    ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(bootstrap)

  lazy  val producerSettingsWithTLS: ProducerSettings[Array[Byte], String] =
    producerSettingsPlain
      .withProperty("ssl.truststore.location", trustStorePath)
      .withProperty("ssl.truststore.password", trustStorePassword)
      .withProperty("ssl.keystore.location", keyStorePath)
      .withProperty("ssl.keystore.password", keyStorePassword)
      .withProperty("ssl.key.password", keyPassword)
      .withProperty("security.protocol", securityProtocol)
      .withProperty("sasl.mechanism", saslMechanism)

  val producerSettings: ProducerSettings[Array[Byte], String] = {
     if (conf.getBoolean("kafka.useTLS")) producerSettingsWithTLS
     else producerSettingsPlain
  }
}
