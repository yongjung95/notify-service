package com.example.repository;

import com.example.dto.NewsManagementNotify;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.domain.QMember.member;
import static com.example.domain.QNewsManagement.newsManagement;

@Repository
@RequiredArgsConstructor
public class NewsManagementQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<NewsManagementNotify> findNewsManagementNotify() {
        return jpaQueryFactory.select(Projections.constructor(NewsManagementNotify.class,
                        newsManagement.keyword,
                        member.memberUUID,
                        member.nickname,
                        member.fcmToken))
                .from(newsManagement)
                .leftJoin(member).on(newsManagement.memberUUID.eq(member.memberUUID))
                .where(member.id.isNotNull())
                .fetch();
    }
}
