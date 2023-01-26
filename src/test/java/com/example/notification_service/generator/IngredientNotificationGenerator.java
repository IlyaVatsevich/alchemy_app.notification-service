package com.example.notification_service.generator;

import com.example.notification_service.dto.Ingredient;
import com.example.notification_service.dto.IngredientNotification;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IngredientNotificationGenerator {

    public static IngredientNotification buildIngredientNotification() {
        return IngredientNotification.builder()
                .withIngredient(buildIngredient())
                .withMail("user_1@yopmail.com")
                .build();
    }

    private static Ingredient buildIngredient() {
        return Ingredient.builder()
                .withName("Lava")
                .withPrice(100L)
                .withLossProbability(((short) 50))
                .build();
    }


}
