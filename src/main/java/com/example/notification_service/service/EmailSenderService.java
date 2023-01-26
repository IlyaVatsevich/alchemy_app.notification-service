package com.example.notification_service.service;

import com.example.notification_service.dto.Ingredient;
import com.example.notification_service.dto.IngredientNotification;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender mailSender;
    private final TaskExecutor taskExecutorCustom;
    private final Queue<SimpleMailMessage> ingredientNotificationQueue = new LinkedBlockingQueue<>();
    private final Environment environment;

    @KafkaListener(topics = "${kafka.topic.name}",groupId = "${kafka.topic.group_id}")
    public void sendNotificationAboutNewIngredient(IngredientNotification ingredientNotification) {
        SimpleMailMessage notificationAboutNewIngredient = createNotificationAboutNewIngredient(ingredientNotification);
        ingredientNotificationQueue.add(notificationAboutNewIngredient);
        taskExecutorCustom.execute(new MessagesSendRunnable(ingredientNotificationQueue));
    }

    private SimpleMailMessage createNotificationAboutNewIngredient(IngredientNotification ingredientNotification) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(ingredientNotification.getMail());
        simpleMailMessage.setSubject("Alchemy, new ingredient");
        Ingredient ingredient = ingredientNotification.getIngredient();
        simpleMailMessage.setText("In Alchemy appeared a new ingredient: " + ingredient);
        return simpleMailMessage;
    }

    @AllArgsConstructor
    private class MessagesSendRunnable implements Runnable {

        private final Queue<SimpleMailMessage> messages;

        @Override
        public void run() {
            while (!messages.isEmpty()) {
                SimpleMailMessage[] messagesToSend = getArrayOfSimpleMessages();
                mailSender.send(messagesToSend);
                log.info("Messages to send left: " + messages.size());
                if (environment.acceptsProfiles(activeProfiles -> activeProfiles.test("dev"))) {
                    sleepForOneMinute();
                }
            }
        }

        private SimpleMailMessage[] getArrayOfSimpleMessages() {
            int size = Math.min(messages.size(), 50);
            return Stream
                    .generate(messages::poll)
                    .limit(size)
                    .toArray(SimpleMailMessage[]::new);
        }

        private void sleepForOneMinute() {
            try {
                Thread.sleep(70000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
