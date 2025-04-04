package com.example.energy.graphql;

import com.example.energy.entity.Building;
import com.example.energy.entity.Device;
import com.example.energy.graphql.input.BuildingInput;
import com.example.energy.service.BuildingService;
import graphql.GraphQLException;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@GraphQLApi
public class BuildingGraphql {
    @Inject
    BuildingService buildingService;

    @Query("getBuilding")
    public Building getBuilding(Integer buildingId) {
        return buildingService.getBuilding(buildingId);
    }

    @Query("getAllBuildings")
    public List<Building> getAllBuildings(@DefaultValue("10") int limit, @DefaultValue("0") int offset, @DefaultValue("asc") String orderDirection) {
        return buildingService.getAllBuildings(limit, offset, orderDirection);
    }

    @Mutation("createBuilding")
    public Building createBuilding(BuildingInput input) {
        List<String> errors = new ArrayList<>();

        if (input == null) {
            throw new GraphQLException("Building input cannot be null");
        }

        if (input.getName() == null || input.getName().trim().isEmpty()) {
            errors.add("Building name is required");
        }

        if (input.getLocation() == null || input.getLocation().trim().isEmpty()) {
            errors.add("Building location is required");
        }

        if (!errors.isEmpty()) {
            throw new GraphQLException("Validation failed: " + String.join(", ", errors));
        }

        return buildingService.addBuilding(input);
    }

    @Mutation("deleteBuilding")
    public Boolean deleteBuilding(BuildingInput input) {
        return buildingService.deleteBuilding(input);
    }

    @Mutation("updateBuilding")
    public Building updateBuilding(BuildingInput input) {
        return buildingService.updateBuilding(input);
    }
}
