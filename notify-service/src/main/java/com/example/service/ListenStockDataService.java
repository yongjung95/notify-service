package com.example.service;

import com.example.dto.SendNotifyStocksTopic;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ListenStockDataService {

    private final String koreaTopicName = "korea-management-topic";
    private final String koreaTopicGroupId = "korea-management-topic-group";

    private final String americaTopicName = "america-management-topic";
    private final String americaTopicGroupId = "america-management-topic-group";

    @KafkaListener(topics = koreaTopicName, groupId = koreaTopicGroupId)
    public void listenKoreaStockData(SendNotifyStocksTopic sendNotifyStocksTopic) {
        String title;

        if (sendNotifyStocksTopic.isOpening()) {
            title = sendNotifyStocksTopic.nickname() + "님의 관심 종목 개장 정보 🌝";
        } else {
            title = sendNotifyStocksTopic.nickname() + "님의 관심 종목 폐장 정보 🌞";
        }

        String body = sendNotifyStocksTopic.name() + "의 현재 가격 : " +
                (int) sendNotifyStocksTopic.price().doubleValue() +
                "원 입니다!";

        Message message = Message.builder()
                .setToken(sendNotifyStocksTopic.fcmToken())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("type", "stocks")
                .build();

        // 메시지 전송
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            log.error("알림 전송 에러 발생 {}", e.getMessage());
        }

        log.info("listenStockData topic {}", sendNotifyStocksTopic);
    }

    @KafkaListener(topics = americaTopicName, groupId = americaTopicGroupId)
    public void listenAmericaStockData(SendNotifyStocksTopic sendNotifyStocksTopic) {
        String title;

        if (sendNotifyStocksTopic.isOpening()) {
            title = sendNotifyStocksTopic.nickname() + "님의 관심 종목 개장 정보 🌝";
        } else {
            title = sendNotifyStocksTopic.nickname() + "님의 관심 종목 폐장 정보 🌞";
        }

        String body = sendNotifyStocksTopic.name() + "의 현재 가격 : $" +
                sendNotifyStocksTopic.price() + " 입니다!";

        Message message = Message.builder()
                .setToken(sendNotifyStocksTopic.fcmToken())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        // 메시지 전송
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            log.error("알림 전송 에러 발생 {}", e.getMessage());
        }

        log.info("listenStockData topic {}", sendNotifyStocksTopic);
    }
}
