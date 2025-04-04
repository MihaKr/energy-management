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
        return measurementService.createMeasurement(input);
    }

    @Mutation("updateMeasurement")
    public Measurement uppdateMeasurement(MeasurementInput input) {
        return measurementService.updateMeasurement(input);
    }

    @Mutation("removeMeasurement")
    public boolean removeMeasurement(MeasurementInput input) {
        return measurementService.removeMeasurement(input);
    }

    @Query("getMeasurement")
    public Measurement getMeasurement(Integer measurementId) {
        return measurementService.getMeasurement(measurementId);
    }

    @Query("getAllMeasurements")
    public List<Measurement> getMeasurements(@DefaultValue("10") int limit, @DefaultValue("0") int offset, @DefaultValue("asc") String orderDirection) {
        return measurementService.getAllMeasurements(limit, offset, orderDirection);
    }

    @Query("getEnergyConsumptionBuilding")
    public Float getEnergyConsumptionBuilding(Integer buildingId, Instant from, Instant to) {
        return measurementService.getEnergyConsumption(buildingId, from, to);
    }
}
