package com.example.service;

import com.example.dto.NewsDataRecord;

public interface NewsDataService {
    NewsDataRecord findNewsDataById(Long id);
}
