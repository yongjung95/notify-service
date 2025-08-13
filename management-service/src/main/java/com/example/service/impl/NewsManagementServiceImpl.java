package com.example.service.impl;

import com.example.domain.NewsManagement;
import com.example.dto.NewsManagementRecord;
import com.example.exception.DuplicateNewsManagementException;
import com.example.exception.NewsManagementAccessException;
import com.example.repository.NewsManagementRepository;
import com.example.service.NewsManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsManagementServiceImpl implements NewsManagementService {

    private final NewsManagementRepository newsManagementRepository;

    @Override
    public List<NewsManagementRecord> findNewsManagementByMemberUUID(String memberUUID) {
        return newsManagementRepository.findNewsManagementByMemberUUID(memberUUID)
                .stream()
                .map(NewsManagementRecord::fromNewsManagement)
                .toList();
    }

    @Override
    public Long save(String keyword, String memberUUID) {
        Optional<NewsManagement> newsManagement = newsManagementRepository.findNewsManagementByKeywordAndMemberUUID(keyword, memberUUID);

        if (newsManagement.isPresent()) {
            throw new DuplicateNewsManagementException();
        }

        return newsManagementRepository.save(NewsManagement.of(keyword, memberUUID)).getId();
    }

    @Override
    public void delete(String keyword, String memberUUID) {
        Optional<NewsManagement> newsManagement = newsManagementRepository.findNewsManagementByKeywordAndMemberUUID(keyword, memberUUID);

        if (newsManagement.isEmpty()) {
            throw new NewsManagementAccessException();
        }

        newsManagementRepository.delete(newsManagement.get());
    }
}
