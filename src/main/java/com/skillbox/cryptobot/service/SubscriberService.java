package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.model.Subscriber;
import com.skillbox.cryptobot.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public Subscriber save(Subscriber subscriber) {
        Optional<Subscriber> existedSubscriber = subscriberRepository.findByTelegramId(subscriber.getTelegramId());
        return existedSubscriber.orElseGet(() -> subscriberRepository.save(subscriber));
    }

    public Subscriber findByTelegramId(Long id) {
        return subscriberRepository.findByTelegramId(id).orElseThrow();
    }

    public Subscriber subscribe(Long telegramId, Float price) {
        Optional<Subscriber> optionalSubscriber =  subscriberRepository.findByTelegramId(telegramId);

        if(optionalSubscriber.isEmpty()) {
            return subscriberRepository.save(new Subscriber(UUID.randomUUID(), telegramId, price));
        } else {
            Subscriber subscriber = optionalSubscriber.get();
            subscriber.setSubscribePrice(price);
            return subscriberRepository.save(subscriber);
        }
    }

    public void unsubscribe(Long telegramId) {
        Optional<Subscriber> optionalSubscriber = subscriberRepository.findByTelegramId(telegramId);
        Subscriber subscriber = optionalSubscriber.orElseThrow();
        subscriber.setSubscribePrice(null);
        subscriberRepository.save(subscriber);
    }

    public List<Subscriber> findAllByGreaterCurrentPrice(Double price) {
        return subscriberRepository.findAllByGreaterCurrentPrice(price);
    }
}
