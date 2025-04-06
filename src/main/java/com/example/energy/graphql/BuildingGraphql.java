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
        try {
            return buildingService.getBuilding(buildingId);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Query("getAllBuildings")
    public List<Building> getAllBuildings(@DefaultValue("10") int limit, @DefaultValue("0") int offset, @DefaultValue("asc") String orderDirection) {
        try {
            return buildingService.getAllBuildings(limit, offset, orderDirection);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Mutation("createBuilding")
    public Building createBuilding(BuildingInput input) {
        try {
            return buildingService.addBuilding(input);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Mutation("deleteBuilding")
    public Boolean deleteBuilding(BuildingInput input) {
        try {
            return buildingService.deleteBuilding(input);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Mutation("updateBuilding")
    public Building updateBuilding(BuildingInput input) {
        try {
            return buildingService.updateBuilding(input);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }
}
