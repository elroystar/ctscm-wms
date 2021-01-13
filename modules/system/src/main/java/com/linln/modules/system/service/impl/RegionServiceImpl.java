package com.linln.modules.system.service.impl;

import com.linln.modules.system.domain.Region;
import com.linln.modules.system.repository.RegionRepository;
import com.linln.modules.system.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public List<Region> getRegionByUserId(String userId) {

        return regionRepository.getRegionByUserId(userId);
    }

    @Override
    public void save(Region region) {
        regionRepository.save(region);
    }

    @Override
    public void deleteByUserId(String userId) {
        regionRepository.deleteByUserId(userId);
    }
}
