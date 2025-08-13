package com.example.repository.user;

import com.example.domain.Member;

import java.util.Optional;

public interface MemberDetailRepository {

    Optional<Member> findMemberById(String id);

    Optional<Member> findMemberForLoginById(String id);

    Optional<Member> findMemberByMemberUUID(String memberUUID);
}
