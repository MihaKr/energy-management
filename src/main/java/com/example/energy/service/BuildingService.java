package com.example.energy.service;

import com.example.energy.entity.Building;
import com.example.energy.graphql.input.BuildingInput;
import com.example.energy.repository.BuildingRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class BuildingService {

    @Inject
    BuildingRepository buildingRepository;

    public Building getBuilding(Integer buildingId) {
        if (buildingId == null) {
            return null;
        }
        return buildingRepository.getBuilding(buildingId);
    }

    public List<Building> getAllBuildings(int limit, int offset, String orderDirection) {
        if (limit < 0) {
            limit = 0;
        }
        return buildingRepository.getAllBuildings(limit, offset, orderDirection);
    }

    @Transactional
    public Building addBuilding(BuildingInput input) {
        if (input.getName() == null || input.getName().isEmpty()) {
            throw new IllegalArgumentException("Building name is required");
        }

        if (input.getLocation() == null || input.getLocation().isEmpty()) {
            throw new IllegalArgumentException("Location name is required");
        }

        Building building = new Building(null, input.getName(), input.getLocation());
        return buildingRepository.createBuilding(building);
    }

    @Transactional
    public boolean deleteBuilding(BuildingInput input) {
        try {
            if (input.getBuildingId() == null) {
                throw new IllegalArgumentException("Building ID cannot be null");
            }

            Building building = buildingRepository.getBuilding(input.getBuildingId());
            if (building == null) {
                throw new NotFoundException("Building not found with id: " + input.getBuildingId());
            }

            buildingRepository.deleteBuilding(input.getBuildingId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public Building updateBuilding(BuildingInput input) {
        if (input.getBuildingId() == null) {
            throw new IllegalArgumentException("Building ID cannot be null");
        }

        Building building = buildingRepository.getBuilding(input.getBuildingId());

        if (building == null) {
            throw new NotFoundException("Building not found with id: " + input.getBuildingId());
        }

        if (input.getName() != null) {
            building.setName(input.getName());
        }

        if (input.getLocation() != null) {
            building.setLocation(input.getLocation());
        }

        return buildingRepository.updateBuilding(building);
    }
}
