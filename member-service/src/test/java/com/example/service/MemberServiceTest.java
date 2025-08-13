package com.example.service;

import com.example.domain.Member;
import com.example.dto.MemberRecord;
import com.example.exception.DuplicateMemberIdException;
import com.example.repository.user.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원_ID_중복_체크() throws Exception {
        //given
        String id = "yongjung";
        String passwd = "12345";
        String nickname = "aaaaaa";

        memberRepository.saveAndFlush(Member.of(id, passwd, nickname));

        //when
        String findFirstMemberId = "yongjung";
        String findSecondMemberId = "yongjung2";

        //then
        boolean firstResult = memberService.isUseId(findFirstMemberId);
        boolean secondResult = memberService.isUseId(findSecondMemberId);
        assertThat(firstResult).isTrue();
        assertThat(secondResult).isFalse();
    }

    @Test
    void 회원_생성() throws Exception {
        //given
        String id = "yongjung";
        String passwd = "12345";
        String nickname = "aaaaaa";

        //when
        MemberRecord saveMember = memberService.saveMember(id, passwd, nickname);

        //then
        Optional<Member> resultMember = memberRepository.findMemberById(id);
        assertThat(resultMember.get().getId()).isEqualTo(id);
    }

    @Test
    void 회원_생성시_패스워드_암호화_확인() throws Exception {
        //given
        String id = "yongjung";
        String passwd = "12345";
        String nickname = "aaaaaa";

        //when
        MemberRecord saveMember = memberService.saveMember(id, passwd, nickname);

        //then
        Optional<Member> resultMember = memberRepository.findMemberById(id);
        System.out.println("resultMember.getPasswd() = " + resultMember.get().getPasswd());
    }

    @Test
    void 회원_생성_중복_아이디_에러발생() throws Exception {
        //given
        String id = "yongjung";
        String passwd = "12345";
        String nickname = "aaaaaa";

        memberRepository.saveAndFlush(Member.of(id, passwd, nickname));

        //then
        assertThatThrownBy(() -> memberService.saveMember(id, passwd, nickname))
                .isInstanceOf(DuplicateMemberIdException.class);
    }

    @Test
    void 회원_FCM_TOKEN_수정() throws Exception {
        //given
        String id = "yongjung";
        String passwd = "12345";
        String nickname = "aaaaaa";

        memberRepository.saveAndFlush(Member.of(id, passwd, nickname));

        //when
        String fcmToken = "token";
        memberService.changeFcmToken(id, fcmToken);

        //then
        Optional<Member> memberForLoginById = memberRepository.findMemberForLoginById(id);
        assertThat(memberForLoginById.get().getFcmToken()).isEqualTo(fcmToken);

    }
}