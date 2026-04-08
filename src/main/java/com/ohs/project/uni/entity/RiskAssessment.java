package com.ohs.project.uni.entity;

import com.ohs.project.uni.entity.enums.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "risk_assets")
@ToString
public class RiskAssessment {

    @Id
    private String id;

    private String userId;

    private String assetName;
    private AssetType assetType;
    private AssetCategory assetCategory;

    private List<Hazard> hazard;
    private Probability probability;
    private Severity severity;
    private int riskScore;

    private List<String> existingControls;

    private Date createdAt = new Date();

    public RiskAssessment(String userId, String assetName, AssetType assetType,
                   AssetCategory assetCategory, List<Hazard> hazard,
                   Probability probability, Severity severity,
                   int riskScore, List<String> existingControls) {
        this.userId = userId;
        this.assetName = assetName;
        this.assetType = assetType;
        this.assetCategory = assetCategory;
        this.hazard = hazard;
        this.probability = probability;
        this.severity = severity;
        this.riskScore = riskScore;
        this.existingControls = existingControls;
    }
}
