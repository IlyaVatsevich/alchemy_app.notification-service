package com.example.notification_service.service;

import com.example.notification_service.config.KafkaTestContainerConfiguration;
import com.example.notification_service.dto.IngredientNotification;
import com.example.notification_service.extension.GreenMailCustomExtension;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

import static com.example.notification_service.generator.IngredientNotificationGenerator.buildIngredientNotification;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(properties = "spring.profiles.active=test")
@Import(KafkaTestContainerConfiguration.KafkaConfiguration.class)
class EmailSenderServiceTest implements KafkaTestContainerConfiguration {

    @Autowired
    private KafkaTemplate<String, IngredientNotification> kafkaTemplate;

    @Autowired
    private NewTopic newTopic;

    @RegisterExtension
    static GreenMailExtension greenMailExtension =  new GreenMailCustomExtension()
            .withConfiguration();

    @Test
    void testSendMessageShouldSendMessage() {
        IngredientNotification ingredientNotification = buildIngredientNotification();
        ListenableFuture<SendResult<String, IngredientNotification>> listenableFuture =
                kafkaTemplate.send(newTopic.name(), ingredientNotification);
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(()-> {
                    Assertions.assertTrue(listenableFuture.isDone());
                    MimeMessage[] receivedMessages = greenMailExtension.getReceivedMessages();
                    Assertions.assertEquals(1,receivedMessages.length);
                });
    }

}
