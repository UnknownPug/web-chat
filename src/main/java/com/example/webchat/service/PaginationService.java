package com.example.webchat.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PaginationService {

    public <T> List<T> getPaginatedList(List<T> allItems, int limit, int offset) {
        int totalItems = allItems.size();
        int startIndex = offset * limit;
        int endIndex = Math.min(startIndex + limit, totalItems);

        if (startIndex >= totalItems || startIndex < 0) {
            return Collections.emptyList(); // Offset is out of range or negative
        }
        return allItems.subList(startIndex, endIndex);
    }
}


