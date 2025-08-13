package com.example.repository;

import com.example.dto.StocksManagementNotify;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.domain.QMember.member;
import static com.example.domain.QStocks.stocks;
import static com.example.domain.QStocksManagement.stocksManagement;


@Repository
@RequiredArgsConstructor
public class StocksManagementQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<StocksManagementNotify> findStocksManagementNotifyForKoreaStock() {
        return queryFactory.select(
                        Projections.constructor(StocksManagementNotify.class,
                                stocks.ticker,
                                stocks.name,
                                stocks.exchangeCountry,
                                stocks.exchange,
                                member.memberUUID,
                                member.fcmToken,
                                member.nickname
                        )
                )
                .from(stocksManagement)
                .rightJoin(stocks).on(stocksManagement.stocksId.eq(stocks.id).and(
                        stocks.exchangeCountry.eq("KOREA")
                ))
                .join(member).on(stocksManagement.memberUUID.eq(member.memberUUID))
                .where(stocks.id.isNotNull())
                .fetch();
    }

    public List<StocksManagementNotify> findStocksManagementNotifyForAmericaStock() {
        return queryFactory.select(
                        Projections.constructor(StocksManagementNotify.class,
                                stocks.ticker,
                                stocks.name,
                                stocks.exchangeCountry,
                                stocks.exchange,
                                member.memberUUID,
                                member.fcmToken,
                                member.nickname
                        )
                )
                .from(stocksManagement)
                .rightJoin(stocks).on(stocksManagement.stocksId.eq(stocks.id).and(
                        stocks.exchangeCountry.eq("AMERICA")
                ))
                .join(member).on(stocksManagement.memberUUID.eq(member.memberUUID))
                .where(stocks.id.isNotNull())
                .fetch();
    }
}
