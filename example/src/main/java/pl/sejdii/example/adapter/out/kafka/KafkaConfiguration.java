package pl.sejdii.example.adapter.out.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
class KafkaConfiguration {

  @Bean
  NewTopic roomReservedTopic() {
    return TopicBuilder.name("roomReserved").partitions(1).replicas(1).build();
  }
}
