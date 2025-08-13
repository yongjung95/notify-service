package com.example.service;

import com.example.domain.Member;
import com.example.exception.NotFoundMemberException;
import com.example.repository.user.MemberRepository;
import com.example.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<Member> findMember = memberRepository.findMemberForLoginById(id);
        if (findMember.isEmpty()) {
            throw new NotFoundMemberException();
        }

        return CustomUserDetails.builder()
                .memberUUID(findMember.get().getMemberUUID())
                .username(id)
                .password(findMember.get().getPasswd())
                .nickname(findMember.get().getNickname())
                .authorities(Collections.emptyList())
                .build();
    }

}
