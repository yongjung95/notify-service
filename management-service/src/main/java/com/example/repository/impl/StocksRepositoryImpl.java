package com.example.repository.impl;

import com.example.domain.Stocks;
import com.example.dto.StocksRecord;
import com.example.repository.StocksRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.domain.QStocks.stocks;
import static com.example.domain.QStocksManagement.stocksManagement;


@Repository
public class StocksRepositoryImpl implements StocksRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public StocksRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<StocksRecord> findStocksList(Pageable pageable, String exchangeCountry, String searchText, String memberUUID) {
        List<StocksRecord> stocksList = queryFactory.select(Projections.constructor(StocksRecord.class,
                        stocks.id,
                        stocks.ticker,
                        stocks.name,
                        stocks.exchangeCountry,
                        stocks.exchange,
                        stocks.stocksType.stringValue(),
                        stocksManagement.id.isNotNull()
                ))
                .from(stocks)
                .leftJoin(stocksManagement)
                .on(stocks.id.eq(stocksManagement.stocksId)
                        .and(stocksManagement.memberUUID.eq(memberUUID)))
                .where(stocks.name.contains(searchText)
                        .and(stocks.exchangeCountry.eq(exchangeCountry)))
                .orderBy(stocks.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long stockListCount = queryFactory.select(stocks.count())
                .from(stocks)
                .where(stocks.name.contains(searchText)
                        .and(stocks.exchangeCountry.eq(exchangeCountry)))
                .fetchOne();

        return new PageImpl<>(stocksList, pageable, stockListCount);
    }

    @Override
    public Optional<Stocks> findStocksByTicker(String ticker) {
        return Optional.ofNullable(queryFactory.select(stocks)
                .from(stocks)
                .where(stocks.ticker.eq(ticker))
                .fetchOne());
    }
}
