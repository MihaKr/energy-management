package com.example.energy.repository;

import com.example.energy.entity.Building;
import com.example.energy.entity.Device;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class DeviceRepository {
    @Inject
    EntityManager entityManager;

    public Device createDevice(Device device) {
        entityManager.persist(device);
        return device;
    }

    public Device deleteDevice(Integer deviceId) {
        Device device = entityManager.find(Device.class, deviceId);
        if (device != null) {
            entityManager.remove(device);
            entityManager.flush();
            return device;
        }
        return null;
    }

    public Device getDevice(Integer deviceId) {
        Device device = entityManager.find(Device.class, deviceId);
        if (device == null) {
            throw new EntityNotFoundException("Device with id " + deviceId + " not found");
        }
        return device;
    }

    public Device updateDevice(Device device) {
        return entityManager.merge(device);
    }

    public List<Device> getAllDevices(int limit, int offset, String orderDirection) {
        return entityManager.createQuery("SELECT d FROM Device d order by d.id " +
                        (orderDirection != null && orderDirection.equalsIgnoreCase("desc") ? "DESC" : "ASC"),
                        Device.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
