package us.reindeers.idgeneratorservice.service;

import us.reindeers.idgeneratorservice.domain.dto.CurrentNumberDto;

public interface IdGeneratorService {
    void addIdToPool(String id);
    String generateUniqueId();
    String generateId();
    CurrentNumberDto getCurrentNumber();
}
