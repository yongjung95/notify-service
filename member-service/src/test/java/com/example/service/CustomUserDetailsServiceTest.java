package com.example.service;

import com.example.domain.Member;
import com.example.exception.NotFoundMemberException;
import com.example.repository.user.MemberRepository;
import com.example.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void 회원_로그인() throws Exception {
        //given
        String id = "yongjung";
        String passwd = bCryptPasswordEncoder.encode("12345");
        String nickname = "aaaaaa";

        memberRepository.saveAndFlush(Member.of(id, passwd, nickname));

        //when
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername("yongjung");

        //then
        assertThat(userDetails).isNotNull();
        assertThat(id).isEqualTo(userDetails.getUsername());
        assertThat(passwd).isEqualTo(userDetails.getPassword());
        assertThat(userDetails.getMemberUUID()).isNotNull();
    }

    @Test
    void 회원_로그인_아이디_에러_발생() throws Exception {
        //given
        String id = "yongjung";
        String passwd = bCryptPasswordEncoder.encode("12345");
        String nickname = "aaaaaa";

        memberRepository.saveAndFlush(Member.of(id, passwd, nickname));

        //when
        String failId = "yongjung2";

        //then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(failId)).isInstanceOf(NotFoundMemberException.class);
    }
}