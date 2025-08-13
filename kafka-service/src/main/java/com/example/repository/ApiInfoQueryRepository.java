package com.example.repository;

import com.example.dto.ApiInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.example.domain.QApiInfo.apiInfo;

@Repository
@RequiredArgsConstructor
public class ApiInfoQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Cacheable(key = "#today", cacheNames = "apiInfo")
    @Transactional(readOnly = true)
    public ApiInfo findApiInfo(String today) {
        return queryFactory.select(
                        Projections.constructor(ApiInfo.class,
                                apiInfo.token,
                                apiInfo.opndYn)
                )
                .from(apiInfo)
                .where(apiInfo.issueDate.eq(today))
                .fetchOne();
    }
}
