package com.ohs.project.uni.dto;

import com.ohs.project.uni.entity.enums.AssetCategory;
import com.ohs.project.uni.entity.enums.AssetType;
import lombok.Data;

@Data
public class RiskAssessmentDTO {
    private String assetName;
    private AssetType assetType;
    private AssetCategory assetCategory;

    private int usageFrequency;
    private int exposureLevel;
    private int incidentHistory;

    private int humanImpact;
    private int businessImpact;
    private int damageImpact;
}
