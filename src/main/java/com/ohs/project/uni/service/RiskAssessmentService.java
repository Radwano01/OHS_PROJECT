package com.ohs.project.uni.service;

import com.ohs.project.uni.dto.RiskAssessmentDTO;
import com.ohs.project.uni.dto.RiskAssessmentDetailsDTO;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface RiskAssessmentService {
    String save(RiskAssessmentDTO riskAssessmentDTO, String userId);
    byte[] getRiskAssessment(String id);
    List<RiskAssessmentDetailsDTO> getUserHistory(String userId);
}
