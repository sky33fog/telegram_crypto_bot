package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.model.Subscriber;
import com.skillbox.cryptobot.service.SubscriberService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        Subscriber existedSubscriber;

        try {
            existedSubscriber = subscriberService.findByTelegramId(message.getFrom().getId());
        } catch (NoSuchElementException ex) {
            existedSubscriber = subscriberService
                    .save(new Subscriber(UUID.randomUUID(), message.getFrom().getId(), null));
        }

        if(existedSubscriber.getSubscribePrice() == null) {
            answer.setText("Активные подписки отсутствуют.");
        } else {
            answer.setText("Вы подписаны на стоимость биткоина " + TextUtil.toString(existedSubscriber.getSubscribePrice()) + " USD");
        }

        try {
            absSender.execute(answer);
        } catch (Exception e) {
            log.error("Ошибка возникла в /get_subscription", e);
        }
    }
}