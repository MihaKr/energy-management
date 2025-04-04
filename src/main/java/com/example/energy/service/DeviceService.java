package com.example.energy.service;

import com.example.energy.entity.Building;
import com.example.energy.entity.Device;
import com.example.energy.graphql.input.DeviceInput;
import com.example.energy.repository.DeviceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class DeviceService {

    @Inject
    DeviceRepository deviceRepository;

    public Device getDevice(Integer deviceId) {
        if (deviceId == null) {
            return null;
        }
        return deviceRepository.getDevice(deviceId);
    }

    public List<Device> getAllDevices(int limit, int offset, String orderDirection) {
        if (limit < 0) {
            limit = 0;
        }
        return deviceRepository.getAllDevices(limit, offset, orderDirection);
    }

    @Transactional
    public Device addDevice(DeviceInput input) {
        if (input.getManufacturer() == null || input.getManufacturer().isEmpty()) {
            throw new IllegalArgumentException("Device manufacturer is required");
        }
        if (input.getType() == null || input.getType().toString().isEmpty()) {
            throw new IllegalArgumentException("Location name is required");
        }

        Device device = new Device(null, input.getType(), input.getManufacturer());
        return deviceRepository.createDevice(device);
    }

    @Transactional
    public boolean deleteDevice(DeviceInput input) {
        try {
            if (input.getId() == null) {
                throw new IllegalArgumentException("Device ID cannot be null");
            }
            Device device = deviceRepository.getDevice(input.getId());
            if (device == null) {
                throw new NotFoundException("Device not found with id: " + input.getId());
            }
            deviceRepository.deleteDevice(input.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public Device updateDevice(DeviceInput input) {
        Device device = deviceRepository.getDevice(input.getId());

        if (device == null) {
            throw new NotFoundException("Device not found with id: " + input.getId());
        }

        if (input.getType() != null) {
            device.setType(input.getType());
        }

        if (input.getManufacturer() != null) {
            device.setManufacturer(input.getManufacturer());
        }

        return deviceRepository.updateDevice(device);
    }
}