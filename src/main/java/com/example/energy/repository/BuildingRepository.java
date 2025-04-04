package com.example.energy.repository;

import com.example.energy.entity.Building;
import com.example.energy.entity.Measurement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BuildingRepository {
    @Inject
    EntityManager entityManager;

    public Building createBuilding(Building building) {
        entityManager.persist(building);
        return building;
    }

    public Building deleteBuilding(Integer buildingId) {
        Building building = entityManager.find(Building.class, buildingId);
        if (building != null) {
            entityManager.remove(building);
            entityManager.flush();
            return building;
        }
        return null;
    }

    public Building updateBuilding(Building building) {
        return entityManager.merge(building);
    }

    public Building getBuilding(Integer buildingId) {
        Building building = entityManager.find(Building.class, buildingId);
        if (building == null) {
            throw new EntityNotFoundException("Building with id " + buildingId + " not found");
        }
        return building;
    }

    public List<Measurement> getBuildingMeasurements(Integer buildingId) {
        Building building = entityManager.find(Building.class, buildingId);
        if (building == null) {
            throw new EntityNotFoundException("Building with id " + buildingId + " not found");
        }
        return building.getMeasurements();
    }



    public List<Building> getAllBuildings(int limit, int offset, String orderDirection) {
        return entityManager.createQuery("SELECT b FROM Building b ORDER BY b.id " +
               (orderDirection != null && orderDirection.equalsIgnoreCase("desc") ? "DESC" : "ASC"),
                        Building.class)
                        .setFirstResult(offset)
                        .setMaxResults(limit)
                        .getResultList();
    }
}