package com.adaptivemfa.acs.controller;

import com.adaptivemfa.acs.service.AIService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {
    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/predict")
    public Map<String, Object> predict() {
        return aiService.predictRisk(
                1,
                1,
                1,
                0
        );
    }
}
