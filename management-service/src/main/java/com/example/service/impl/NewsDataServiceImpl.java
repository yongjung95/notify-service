package com.example.service.impl;

import com.example.domain.NewsData;
import com.example.dto.NewsDataRecord;
import com.example.exception.NotFoundNewsException;
import com.example.repository.NewsDataRepository;
import com.example.service.NewsDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsDataServiceImpl implements NewsDataService {

    private final NewsDataRepository newsDataRepository;

    @Override
    public NewsDataRecord findNewsDataById(Long id) {
        NewsData newsData = newsDataRepository.findById(id).orElseThrow(NotFoundNewsException::new);
        return NewsDataRecord.fromNewsDataEntity(newsData);
    }
}
