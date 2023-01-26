package com.example.notification_service.config;

import com.example.notification_service.dto.IngredientNotification;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@Testcontainers
public interface KafkaTestContainerConfiguration {

    @Container
    KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @TestConfiguration
    class KafkaConfiguration {

        @Value("${kafka.topic.name}")
        private String kafkaTopic;

        @Value("${kafka.topic.partitions}")
        private Integer kafkaCountPartitions;

        @Value("${kafka.topic.replicas}")
        private Integer kafkaCountReplicas;

        @Bean
        public NewTopic kafkaTopic() {
            return TopicBuilder
                    .name(kafkaTopic)
                    .partitions(kafkaCountPartitions)
                    .replicas(kafkaCountReplicas)
                    .build();
        }

        @Bean
        public KafkaTemplate<String, IngredientNotification> kafkaTemplate(ProducerFactory<String,IngredientNotification> producerFactory) {
            return new KafkaTemplate<>(producerFactory);
        }

        @Bean
        public ProducerFactory<String, IngredientNotification> producerFactory(KafkaProperties kafkaProperties) {
            Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
            producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            return new DefaultKafkaProducerFactory<>(producerProperties);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, IngredientNotification> kafkaListenerContainerFactory(
                ConsumerFactory<String, IngredientNotification> consumerFactory) {
            ConcurrentKafkaListenerContainerFactory<String, IngredientNotification> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory);
            return factory;
        }

        @Bean
        public ConsumerFactory<String,IngredientNotification> consumerFactory(KafkaProperties kafkaProperties) {
            Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
            consumerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            return new DefaultKafkaConsumerFactory<>(consumerProperties);
        }

    }

}
