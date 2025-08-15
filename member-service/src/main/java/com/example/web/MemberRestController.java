package com.example.web;

import com.example.dto.CommonResultRecord;
import com.example.dto.MemberRecord;
import com.example.dto.RequestRecord;
import com.example.dto.ResponseRecord;
import com.example.service.MemberService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<?> createMember(@RequestBody @Valid RequestRecord.MembersRequestRecord memberRequestRecord) {
        MemberRecord saveMember = memberService.saveMember(memberRequestRecord.id(),
                memberRequestRecord.passwd(), memberRequestRecord.nickname());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResultRecord.successResult(
                        HttpStatus.CREATED.value(),
                        "success",
                        ResponseRecord.MemberResponseRecord.fromMemberRecord(saveMember)));
    }

    @PostMapping("/members/fcm-token")
    public ResponseEntity<?> saveFcmToken(@RequestBody @Valid RequestRecord.MembersFcmTokenRequestRecord memberRequestRecord) {
        memberService.changeFcmToken(memberRequestRecord.jwtToken(), memberRequestRecord.fcmToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResultRecord.successResult(HttpStatus.OK.value(), "success", null));
    }

    @PostMapping("/members/send-push")
    public ResponseEntity<String> sendPush(@RequestParam String token) {
        try {
            Message message = Message.builder()
                    .setToken(token)  // 앞서 받은 FCM 토큰
                    .setNotification(Notification.builder()
                            .setTitle("테스트 알림")
                            .setBody("Spring Boot에서 보낸 푸시 알림입니다!")
                            .build())
                    .putData("newsDataId", "14")
                    .build();

            // 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);

            return ResponseEntity.ok("알림 전송 성공: " + response);
        } catch (FirebaseMessagingException e) {
            System.out.println("Firebase 에러 코드: " + e.getErrorCode());
            System.out.println("Firebase 에러 메시지: " + e.getMessage());
            return ResponseEntity.status(500).body("Firebase 에러: " + e.getErrorCode() + " - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("일반 에러: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("일반 에러: " + e.getMessage());
        }
    }

    @PostMapping("/members/news-send-push")
    public ResponseEntity<String> newsSendPush(@RequestParam String token) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .putData("title", "뉴스 발행 테스트")
                    .putData("body", "뉴스 확인하러 가기!")
                    .putData("newsDataId", String.valueOf(45))
                    .putData("type", "news")
                    .build();

            // 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);

            return ResponseEntity.ok("알림 전송 성공: " + response);
        } catch (FirebaseMessagingException e) {
            System.out.println("Firebase 에러 코드: " + e.getErrorCode());
            System.out.println("Firebase 에러 메시지: " + e.getMessage());
            return ResponseEntity.status(500).body("Firebase 에러: " + e.getErrorCode() + " - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("일반 에러: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("일반 에러: " + e.getMessage());
        }
    }
}
