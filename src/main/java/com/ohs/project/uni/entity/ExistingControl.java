package com.ohs.project.uni.entity;

import com.ohs.project.uni.entity.enums.AssetType;
import com.ohs.project.uni.entity.enums.Hazard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "existing_control")
@Getter
@Setter
@NoArgsConstructor
public class ExistingControl {

    @Id
    private String id;
    private AssetType assetType;
    private Hazard hazard;
    private List<String> ExistingControl;

    public ExistingControl(AssetType assetType,
                           Hazard hazard,
                           List<String> ExistingControl) {
        this.assetType = assetType;
        this.hazard = hazard;
        this.ExistingControl = ExistingControl;
    }
}
