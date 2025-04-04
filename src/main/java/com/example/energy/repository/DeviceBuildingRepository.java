package com.example.energy.repository;


import com.example.energy.entity.DeviceBuilding;
import com.example.energy.entity.DeviceBuildingId;
import com.example.energy.entity.Measurement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class DeviceBuildingRepository {
    @Inject
    EntityManager entityManager;

    public DeviceBuilding createDeviceBuilding(DeviceBuilding deviceBuilding) {
        entityManager.persist(deviceBuilding);
        return deviceBuilding;
    }

    public DeviceBuilding getDeviceBuilding(Integer deviceId, Integer buildingId) {
        DeviceBuildingId id = new DeviceBuildingId(deviceId, buildingId);
        DeviceBuilding deviceBuilding = entityManager.find(DeviceBuilding.class, id);
        if (deviceBuilding == null) {
            throw new EntityNotFoundException("DeviceBuilding with deviceId " + deviceId +
                    " and buildingId " + buildingId + " not found");
        }
        return deviceBuilding;
    }

    public DeviceBuilding updateDeviceBuilding(DeviceBuilding deviceBuilding) {
        entityManager.merge(deviceBuilding);
        return deviceBuilding;
    }


    public DeviceBuilding deleteDeviceBuilding(Integer deviceId, Integer buildingId) {
        DeviceBuildingId id = new DeviceBuildingId(deviceId, buildingId);
        DeviceBuilding deviceBuilding = entityManager.find(DeviceBuilding.class, id);
        if (deviceBuilding != null) {
            entityManager.remove(deviceBuilding);
            entityManager.flush();
            return deviceBuilding;
        }
        return null;
    }

    public List<DeviceBuilding> getAllDeviceBuildings(int limit, int offset, String orderDirection) {
        return entityManager.createQuery("SELECT d FROM DeviceBuilding d order by d.deviceId, d.buildingId " +
                (orderDirection != null && orderDirection.equalsIgnoreCase("desc") ? "DESC" : "ASC")
                 ,DeviceBuilding.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<DeviceBuilding> getDevicesByBuilding(Integer buildingId) {
        return entityManager.createQuery(
            "SELECT db FROM DeviceBuilding db " +
            "WHERE db.building.id = :buildingId", DeviceBuilding.class)
                .setParameter("buildingId", buildingId)
                .getResultList();
    }
}
