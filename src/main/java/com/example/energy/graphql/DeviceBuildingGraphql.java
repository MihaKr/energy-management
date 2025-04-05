package com.example.energy.graphql;

import com.example.energy.entity.Device;
import com.example.energy.entity.DeviceBuilding;
import com.example.energy.graphql.input.DeviceBuildingInput;
import com.example.energy.service.DeviceBuildingService;
import graphql.GraphQLException;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

import java.util.List;

@GraphQLApi
public class DeviceBuildingGraphql {
    @Inject
    DeviceBuildingService deviceBuildingService;

    @Mutation("createDeviceBuilding")
    public DeviceBuilding createDeviceBuilding(DeviceBuildingInput input) {
        try {
            return deviceBuildingService.createDeviceBuilding(input);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Query("getDeviceBuilding")
    public DeviceBuilding getDeviceBuilding(@Name("deviceId") Integer deviceId, @Name("buildingId") Integer buildingId) {
        try {
            return deviceBuildingService.getDeviceBuilding(deviceId, buildingId);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Mutation("deleteDeviceBuilding")
    public boolean deleteDeviceBuilding(DeviceBuildingInput input) {
        try {
            return deviceBuildingService.deleteDeviceBuilding(input);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Mutation("updateDeviceBuilding")
    public DeviceBuilding updateDeviceBuilding(DeviceBuildingInput input) {
        try {
            return deviceBuildingService.updateDeviceBuilding(input);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Query("getAllDeviceBuilding")
    public List<DeviceBuilding> getAllDeviceBuilding(@DefaultValue("10") int limit, @DefaultValue("0") int offset, @DefaultValue("asc") String orderDirection) {
        try {
            return deviceBuildingService.getAllDeviceBuilding(limit, offset, orderDirection);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Query("getDevicesByBuilding")
    public List<Device> getDevicesByBuilding(Integer buildingId) {
        try {
            return deviceBuildingService.getDevicesByBuildingId(buildingId);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }
}
