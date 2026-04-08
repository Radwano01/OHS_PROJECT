package com.ohs.project.uni.repository;

import com.ohs.project.uni.entity.RiskAssessment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RiskAssessmentRepository extends MongoRepository<RiskAssessment, String> {
    List<RiskAssessment> findByUserId(String userId);
}
