package com.ohs.project.uni.dto;

import com.ohs.project.uni.entity.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskAssessmentDetailsDTO {
    private String id;
    private String AssetName;
    private AssetType assetType;
    private AssetCategory assetCategory;
    private List<Hazard> hazards;
    private Probability probability;
    private Severity severity;
    private int riskScore;
    private Date createdAt;
}
