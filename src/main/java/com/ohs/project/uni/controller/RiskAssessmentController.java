package com.ohs.project.uni.controller;

import com.ohs.project.uni.dto.RiskAssessmentDTO;
import com.ohs.project.uni.dto.RiskAssessmentDetailsDTO;
import com.ohs.project.uni.service.RiskAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping(path = "/api/v1/risks")
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    @Autowired
    public RiskAssessmentController(RiskAssessmentService riskAssessmentService) {
        this.riskAssessmentService = riskAssessmentService;
    }

    @PostMapping
    public ResponseEntity<String> riskAssessment(@RequestBody RiskAssessmentDTO riskAssessmentDTO, Principal principal) {
        return ResponseEntity.ok().body(riskAssessmentService.save(riskAssessmentDTO, principal.getName()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<byte[]> getRiskAssessment(@PathVariable String id) {
        return ResponseEntity.ok().body(riskAssessmentService.getRiskAssessment(id));
    }

    @GetMapping(path = "/user")
    public ResponseEntity<List<RiskAssessmentDetailsDTO>> getAllRiskAssessmentByUserId(Principal principal) {
        return ResponseEntity.ok().body(riskAssessmentService.getUserHistory(principal.getName()));
    }
}
