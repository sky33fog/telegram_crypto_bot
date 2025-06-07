package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.bot.CryptoBot;
import com.skillbox.cryptobot.model.Subscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Value("${app.interval.refresh-price}")
    Integer refreshPricePeriod;

    @Value("${app.interval.notification}")
    Integer notificationPeriod;

    private final CryptoCurrencyService cryptoCurrencyService;

    private final SubscriberService subscriberService;

    private final CryptoBot cryptoBot;

    private final Map<Long, Integer> timeoutMap = new TreeMap<>();


    @Scheduled(fixedRateString = "${app.interval.refresh-price}")
    private void refreshPrice() {
        Double price = cryptoCurrencyService.refreshPrice();
        List<Subscriber> subscribers = subscriberService.findAllByGreaterCurrentPrice(price);

        sendNotification(subscribers, price);
    }

    private void sendNotification(List<Subscriber> newSubscribers, Double price) {
        List<Long> sendList = new ArrayList<>(newSubscribers.stream().map(Subscriber::getTelegramId).toList());
        List<Long> timeoutList = timeoutMap.keySet().stream().toList();
        sendList.removeAll(timeoutList);
        sendList.forEach(id -> sendMessageToUser(id, "Пора покупать, стоимость биткоина " + price));
        newSubscribers.forEach(s -> {
                    if (!timeoutMap.containsKey(s.getTelegramId())) {
                        timeoutMap.put(s.getTelegramId(), 0);
                    }
                });
        timeoutMap.replaceAll((key, value) -> value + refreshPricePeriod);
        timeoutMap.entrySet().removeIf(entry -> entry.getValue() >= notificationPeriod);
    }

    private void sendMessageToUser(Long telegramId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(telegramId.toString());
        message.setText(text);
        try {
            cryptoBot.execute(message);
        } catch (TelegramApiException ex) {
            log.error("Ошибка возникла при отправке уведомления пользователю", ex);
        }
    }
}
