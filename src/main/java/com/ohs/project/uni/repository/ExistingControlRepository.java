package com.ohs.project.uni.repository;

import com.ohs.project.uni.entity.ExistingControl;
import com.ohs.project.uni.entity.enums.AssetType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExistingControlRepository extends MongoRepository<ExistingControl, String> {
    List<String> findExistingControlByAssetType(AssetType assetType);
}
