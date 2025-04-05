package com.example.energy.graphql;


import com.example.energy.entity.Measurement;
import com.example.energy.graphql.input.MeasurementInput;
import com.example.energy.service.MeasurementService;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.time.Instant;
import java.util.List;

@GraphQLApi
public class MeasurementGraphql {
    @Inject
    MeasurementService measurementService;

    @Mutation("createMeasurement")
    public Measurement createMeasurement(MeasurementInput input) {
        try {
            return measurementService.createMeasurement(input);
        } catch (Exception e) {
            throw new RuntimeException("Error creating measurement: " + e.getMessage());
        }
    }

    @Mutation("updateMeasurement")
    public Measurement uppdateMeasurement(MeasurementInput input) {
        try {
            return measurementService.updateMeasurement(input);
        } catch (Exception e) {
            throw new RuntimeException("Error updating measurement: " + e.getMessage());
        }
    }

    @Mutation("removeMeasurement")
    public boolean removeMeasurement(MeasurementInput input) {
        try {
            return measurementService.removeMeasurement(input);
        } catch (Exception e) {
            throw new RuntimeException("Error removing measurement: " + e.getMessage());
        }
    }

    @Query("getMeasurement")
    public Measurement getMeasurement(Integer measurementId) {
        try {
            return measurementService.getMeasurement(measurementId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error getting measurement: " + e.getMessage());
        }
    }

    @Query("getAllMeasurements")
    public List<Measurement> getMeasurements(@DefaultValue("10") int limit, @DefaultValue("0") int offset, @DefaultValue("asc") String orderDirection) {
        try {
            return measurementService.getAllMeasurements(limit, offset, orderDirection);
        } catch (Exception e) {
            throw new RuntimeException("Error getting measurements: " + e.getMessage());
        }
    }

    @Query("getEnergyConsumptionByBuilding")
    public Float getEnergyConsumptionByBuilding(Integer buildingId, Instant from, Instant to) {
        try {
            return measurementService.getEnergyConsumption(buildingId, from, to);
        } catch (Exception e) {
            throw new RuntimeException("Error getting energy consumption: " + e.getMessage());
        }
    }

    @Query("getEnergyConsumptionByDevice")
    public Float getEnergyConsumptionByDevice(Integer deviceId, Instant from, Instant to) {
        try {
            return measurementService.getEnergyConsumptionByDevice(deviceId, from, to);
        } catch (Exception e) {
            throw new RuntimeException("Error getting energy consumption: " + e.getMessage());
        }
    }

}
