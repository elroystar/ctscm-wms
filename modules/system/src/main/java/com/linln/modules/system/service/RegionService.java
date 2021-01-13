package com.linln.modules.system.service;

import com.linln.modules.system.domain.Region;

import java.util.List;

public interface RegionService {
    List<Region> getRegionByUserId(String userId);

    void save(Region region);

    void deleteByUserId(String userId);
}
