package com.example.service;

import com.example.dto.SendNotifyNewsTopic;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ListenNewsData {

    private final String newsTopicName = "news-management-topic";
    private final String newsTopicGroupId = "news-management-topic-group";

    @KafkaListener(topics = newsTopicName, groupId = newsTopicGroupId)
    public void listenNewsData(SendNotifyNewsTopic sendNotifyNewsTopic) {
        String title = sendNotifyNewsTopic.nickname() + "님의 " + sendNotifyNewsTopic.keyword() + " 뉴스 알림 🙂";

        Message message = Message.builder()
                .setToken(sendNotifyNewsTopic.fcmToken())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody("뉴스 확인하러 가기!")
                        .build())
                .putData("newsDataId", String.valueOf(sendNotifyNewsTopic.newsDataId()))
                .putData("type", "news")
                .build();

        // 메시지 전송
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            log.error("알림 전송 에러 발생 {}", e.getMessage());
        }

        log.info("listenNewsData topic {}", sendNotifyNewsTopic);
    }
}
