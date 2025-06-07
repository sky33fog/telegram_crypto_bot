package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.model.Subscriber;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.service.SubscriberService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscribeCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    private final CryptoCurrencyService service;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        float price;

        if(message.getText().length() <= 10) {
            try {
                answer.setText("Ошибочный формат команды. Пример правильного формата команды для оформления подписки: /subscribe 98000");
                absSender.execute(answer);
            }
            catch (Exception e) {
                log.error("Ошибка возникла в /subscribe методе", e);
            }
        } else {
            price = Float.parseFloat(message.getText().substring(11));

            Subscriber existedSubscriber = subscriberService.subscribe(message.getFrom().getId(), price);

            try {
                answer.setText("Текущая цена биткоина " + TextUtil.toString(service.getBitcoinPrice()) + " USD");
                absSender.execute(answer);
            } catch (Exception e) {
                log.error("Ошибка возникла в /subscribe методе", e);
            }

            try {
                answer.setText("Новая подписка создана на стоимость " + TextUtil.toString(existedSubscriber.getSubscribePrice()) + " USD");
                absSender.execute(answer);
            } catch (Exception e) {
                log.error("Ошибка возникла в /subscribe методе", e);
            }
        }
    }
}