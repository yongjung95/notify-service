package com.example.repository.user.impl;

import com.example.domain.Member;
import com.example.repository.user.MemberDetailRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.domain.QMember.member;


@Repository
public class MemberDetailRepositoryImpl implements MemberDetailRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public MemberDetailRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Optional<Member> findMemberById(String id) {
        return Optional.ofNullable(queryFactory.select(member)
                .from(member)
                .where(member.id.eq(id))
                .fetchOne());
    }

    @Override
    public Optional<Member> findMemberForLoginById(String id) {
        return Optional.ofNullable(queryFactory.select(member)
                .from(member)
                .where(member.id.eq(id)
                        .and(member.isUse.eq(true)))
                .fetchOne());
    }

    @Override
    public Optional<Member> findMemberByMemberUUID(String memberUUID) {
        return Optional.ofNullable(queryFactory.select(member)
                .from(member)
                .where(member.memberUUID.eq(memberUUID)
                        .and(member.isUse.eq(true)))
                .fetchOne());
    }
}
