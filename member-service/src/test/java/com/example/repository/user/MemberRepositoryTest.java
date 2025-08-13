package com.example.repository.user;

import com.example.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원_생성() throws Exception {
        //given
        String id = "yongjung";
        String passwd = "12345";
        String nickname = "aaaaaa";

        //when
        Member saveMember = memberRepository.saveAndFlush(Member.of(id, passwd, nickname));

        //then
        Optional<Member> findMember = memberRepository.findById(saveMember.getMemberUUID());
        assertThat(findMember.get().getId()).isEqualTo(id);
    }

    @Test
    void 회원_조회() throws Exception {
        //given
        String id = "yongjung";
        String passwd = "12345";
        String nickname = "aaaaaa";

        memberRepository.saveAndFlush(Member.of(id, passwd, nickname));

        //when
        String findMemberId = "yongjung";

        //then
        Optional<Member> findMember = memberRepository.findMemberById(findMemberId);
        assertThat(findMember.get().getId()).isEqualTo(findMemberId);
    }
    
    @Test
    void 회원_로그인_정보_조회() throws Exception {
        //given
        String id = "yongjung";
        String passwd = "12345";
        String nickname = "aaaaaa";

        memberRepository.saveAndFlush(Member.of(id, passwd, nickname));
        
        //when
        String findMemberId = "yongjung";
        
        //then
        Optional<Member> findMember = memberRepository.findMemberForLoginById(findMemberId);
        assertThat(findMember.get().getId()).isEqualTo(findMemberId);
    }
}