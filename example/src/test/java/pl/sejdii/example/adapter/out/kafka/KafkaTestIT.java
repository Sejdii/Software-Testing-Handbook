package pl.sejdii.example.adapter.out.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@EmbeddedKafka(kraft = true)
abstract class KafkaTestIT {

  protected final ObjectMapper objectMapper = createObjectMapper();

  private final KafkaConfiguration kafkaConfiguration = new KafkaConfiguration();

  protected KafkaTemplate<String, String> template;

  @BeforeEach
  void prepareKafkaTemplate(EmbeddedKafkaBroker embeddedKafkaBroker) {
    embeddedKafkaBroker.addTopics(kafkaConfiguration.roomReservedTopic());

    Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
    ProducerFactory<String, String> producerFactory =
        new DefaultKafkaProducerFactory<>(producerProps);
    template = new KafkaTemplate<>(producerFactory);
  }

  protected String getMessageFromTopic(EmbeddedKafkaBroker embeddedKafkaBroker, String topic) {
    Map<String, Object> consumerProps =
        KafkaTestUtils.consumerProps("testT", "false", embeddedKafkaBroker);
    DefaultKafkaConsumerFactory<String, String> cf =
        new DefaultKafkaConsumerFactory<>(consumerProps);
    Consumer<String, String> consumer = cf.createConsumer();
    embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, topic);

    return KafkaTestUtils.getSingleRecord(consumer, topic).value();
  }

  private static ObjectMapper createObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new Jdk8Module());
    return objectMapper;
  }
}
