package com.skillbox.cryptobot.repository;

import com.skillbox.cryptobot.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {

    Optional<Subscriber> findByTelegramId(Long telegramID);

    @Query("SELECT s FROM com.skillbox.cryptobot.model.Subscriber s WHERE s.subscribePrice >:price")
    public List<Subscriber> findAllByGreaterCurrentPrice(Double price);


}
