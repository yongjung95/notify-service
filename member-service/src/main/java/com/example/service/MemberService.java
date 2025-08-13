package com.example.service;

import com.example.domain.Member;
import com.example.dto.MemberRecord;
import com.example.exception.DuplicateMemberIdException;
import com.example.exception.NotFoundMemberException;
import com.example.repository.user.MemberRepository;
import com.example.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public boolean isUseId(String id) {
        return memberRepository.findMemberById(id).isPresent();
    }

    public MemberRecord saveMember(String id, String passwd, String nickname) {
        if (memberRepository.findMemberById(id).isPresent()) {
            throw new DuplicateMemberIdException();
        }

        Member saveMember = memberRepository.save(Member.of(id, bCryptPasswordEncoder.encode(passwd), nickname));

        return MemberRecord.fromMember(saveMember);
    }

    public void changeFcmToken(String jwtToken, String fcmToken) {
        String memberUUID = jwtUtil.getMemberUUID(jwtToken);
        Member member = memberRepository.findMemberByMemberUUID(memberUUID).orElseThrow(NotFoundMemberException::new);

        member.changeFcmToken(fcmToken);
    }
}
