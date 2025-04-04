package com.example.energy.service;

import com.example.energy.entity.Building;
import com.example.energy.entity.Device;
import com.example.energy.entity.DeviceBuilding;
import com.example.energy.entity.DeviceBuildingId;
import com.example.energy.graphql.input.DeviceBuildingInput;
import com.example.energy.graphql.input.DeviceInput;
import com.example.energy.repository.DeviceBuildingRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.graphql.Query;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DeviceBuildingService {
    @Inject
    DeviceBuildingRepository deviceBuildingRepository;

    @Transactional
    public DeviceBuilding createDeviceBuilding(DeviceBuildingInput input) {
        if (input.getBuildingId() == null) {
            throw new IllegalArgumentException("Building is required");
        }

        if(input.getDeviceId() == null) {
            throw new IllegalArgumentException("Device is required");
        }

        if (input.getInstalledSince() != null) {
            throw new IllegalArgumentException("Installed Since is required");
        }
        DeviceBuilding deviceBuilding = new DeviceBuilding(input.getDeviceId(), input.getBuildingId(), input.getInstalledSince());
        return deviceBuildingRepository.createDeviceBuilding(deviceBuilding);
    }

    public DeviceBuilding getDeviceBuilding(Integer deviceId, Integer buildingId) {
        if (deviceId == null || buildingId == null) {
            throw new IllegalArgumentException("DeviceId and Building id are required");
        }
        return deviceBuildingRepository.getDeviceBuilding(deviceId, buildingId);
    }

    @Transactional
    public Boolean deleteDeviceBuilding(DeviceBuildingInput input) {
        try {
            if (input.getBuildingId() == null) {
                throw new IllegalArgumentException("Building ID cannot be null");
            }
            if (input.getDeviceId() == null) {
                throw new IllegalArgumentException("Device ID cannot be null");
            }

            DeviceBuilding deviceBuilding = deviceBuildingRepository.getDeviceBuilding(input.getBuildingId(), input.getDeviceId());
            if (deviceBuilding == null) {
                throw new NotFoundException("Device building not found with id: " + input.getBuildingId()
                        + input.getDeviceId());
            }

            deviceBuildingRepository.deleteDeviceBuilding(input.getDeviceId(), input.getBuildingId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public DeviceBuilding updateDeviceBuilding(DeviceBuildingInput input) {
        if (input.getBuildingId() == null) {
            throw new IllegalArgumentException("Building ID cannot be null");
        }
        if (input.getDeviceId() == null) {
            throw new IllegalArgumentException("Device ID cannot be null");
        }
        DeviceBuilding deviceBuilding = deviceBuildingRepository.getDeviceBuilding(input.getDeviceId(), input.getBuildingId());

        if (deviceBuilding == null) {
            throw new NotFoundException("DeviceBuilding not found with device id: " + input.getDeviceId() + " and building id: " + input.getBuildingId());
        }

        if (input.getInstalledSince() != null) {
            deviceBuilding.setInstalledSince(input.getInstalledSince());
        }

        return deviceBuildingRepository.updateDeviceBuilding(deviceBuilding);
    }

    public List<DeviceBuilding> getAllDeviceBuilding(int limit, int offset, String orderDirection) {
        if (limit < 0) {
            limit = 0;
        }
        return deviceBuildingRepository.getAllDeviceBuildings(limit, offset, orderDirection);
    }

    public List<Device> getDevicesByBuildingId(Integer buildingId) {
        if (buildingId == null) {
            throw new IllegalArgumentException("Building id is required");
        }
        List<DeviceBuilding> deviceBuildings = deviceBuildingRepository.getDevicesByBuilding(buildingId);

        List<Device> devices = new ArrayList<>();
        for (DeviceBuilding deviceBuilding : deviceBuildings) {
            devices.add(deviceBuilding.getDevice());
        }
        return devices;
    }
}
