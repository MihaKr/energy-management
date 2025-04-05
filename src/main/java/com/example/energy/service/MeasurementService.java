package com.example.energy.service;

import com.example.energy.entity.Building;
import com.example.energy.entity.Device;
import com.example.energy.entity.Measurement;
import com.example.energy.graphql.input.MeasurementInput;
import com.example.energy.repository.BuildingRepository;
import com.example.energy.repository.DeviceRepository;
import com.example.energy.repository.MeasurementRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class MeasurementService {
    @Inject
    MeasurementRepository measurementRepository;
    @Inject
    BuildingRepository buildingRepository;
    @Inject
    DeviceRepository deviceRepository;

    @Transactional
    public Measurement createMeasurement(MeasurementInput input) {
        if (input.getBuildingId() == null) {
            throw new IllegalArgumentException("Building is required");
        }

        if (input.getDeviceId() == null) {
            throw new IllegalArgumentException("Device is required");
        }

        if (input.getTimestamp() == null) {
            throw new IllegalArgumentException("Timestamp is required");
        }

        if (input.getEnergyKwh() == null) {
            throw new IllegalArgumentException("Energy usage is required");
        }

        Building b = buildingRepository.getBuilding(input.getBuildingId());
        Device d = deviceRepository.getDevice(input.getDeviceId());

        Measurement m = new Measurement();
        m.setTimestamp(input.getTimestamp());
        m.setEnergyKwh(input.getEnergyKwh());
        m.setBuilding(b);
        m.setDevice(d);

        b.addMeasurement(m);
        d.addMeasurement(m);

        return measurementRepository.createMeasurement(m);
    }

    @Transactional
    public boolean removeMeasurement(MeasurementInput input) {
        try {
            if (input.getId() == null) {
                throw new IllegalArgumentException("Measurement ID cannot be null");
            }

            Measurement m = measurementRepository.getMeasurement(input.getId());
            if (m == null) {
                return false;
            }

            if (m.getBuilding() != null) {
                Building b = buildingRepository.getBuilding(m.getBuilding().getId());
                if (b != null) {
                    b.removeMeasurement(m);
                }
            }

            if (m.getDevice() != null) {
                Device d = deviceRepository.getDevice(m.getDevice().getId());
                if (d != null) {
                    d.removeMeasurement(m);
                }
            }

            measurementRepository.deleteMeasurement(input.getId());
            return true;

        } catch (NotFoundException e) {
            return false;
        }
    }

    public Measurement getMeasurement(Integer measurementId) {
        if (measurementId == null) {
            return null;
        }
        return measurementRepository.getMeasurement(measurementId);
    }

    public List<Measurement> getAllMeasurements(int limit, int offset, String orderDirection) {
        if (limit < 0) {
            limit = 0;
        }
        return measurementRepository.getAllMeasurements(limit, offset, orderDirection);
    }

    @Transactional
    public Measurement updateMeasurement(MeasurementInput input) {
        Measurement measurement = measurementRepository.getMeasurement(input.getId());

        if (measurement == null) {
            throw new NotFoundException("Measurement not found with id: " + input.getId());
        }

        if (input.getBuildingId() != null) {
            Building b = buildingRepository.getBuilding(input.getBuildingId());
            measurement.setBuilding(b);
        }

        if (input.getDeviceId() != null) {
            Device d = deviceRepository.getDevice(input.getDeviceId());
            measurement.setDevice(d);
        }

        if (input.getEnergyKwh() != null) {
           measurement.setEnergyKwh(input.getEnergyKwh());
        }

        if (input.getTimestamp() != null) {
           measurement.setTimestamp(input.getTimestamp());
        }

        return measurementRepository.updateMeasurement(measurement);
    }

    public Float getEnergyConsumptionByBuilding(Integer buildingId, Instant from, Instant to) {
        if (buildingId == null) {
            throw new IllegalArgumentException("Building ID cannot be null");
        }

        if (from == null) {
            throw new IllegalArgumentException("Timestamp From cannot be null");
        }
        if (to == null) {
            throw new IllegalArgumentException("Timestamp To cannot be null");
        }

        Building building = buildingRepository.getBuilding(buildingId);

        if (building == null) {
            throw new NotFoundException("Building not found with id: " + buildingId);
        }

        List<Measurement> measurements = measurementRepository.findByBuildingIdAndTimestamp(buildingId, from, to);

        return Float.valueOf((float) measurements.stream()
                .mapToDouble(Measurement::getEnergyKwh)
                .sum());
    }

    public Float getEnergyConsumptionByDevice(Integer deviceId, Instant from, Instant to) {
        if (deviceId == null) {
            throw new IllegalArgumentException("Building ID cannot be null");
        }

        if (from == null) {
            throw new IllegalArgumentException("Timestamp From cannot be null");
        }
        if (to == null) {
            throw new IllegalArgumentException("Timestamp To cannot be null");
        }

        Device device = deviceRepository.getDevice(deviceId);

        if (device == null) {
            throw new NotFoundException("Device not found with id: " + deviceId);
        }

        List<Measurement> measurements = measurementRepository.findByDeviceIdAndTimestamp(deviceId, from, to);

        return Float.valueOf((float) measurements.stream()
                .mapToDouble(Measurement::getEnergyKwh)
                .sum());
    }
}
