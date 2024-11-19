package us.reindeers.idgeneratorservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.reindeers.idgeneratorservice.domain.dto.CurrentNumberDto;
import us.reindeers.idgeneratorservice.manager.IdPoolManager;
import us.reindeers.idgeneratorservice.service.IdGeneratorService;

@RestController
@RequestMapping("/id")
@AllArgsConstructor
public class IdController {

    private final IdGeneratorService idGeneratorService;

    private final IdPoolManager idPoolManager;

    @PostMapping("/refill")
    public void addIdToPoolManually(){
        idPoolManager.manuallyRefill();
    }

    @GetMapping("/currentNumber")
    public CurrentNumberDto getCurrentNumber(){
        return idGeneratorService.getCurrentNumber();
    }
}
