package com.example.energy.graphql;

import com.example.energy.entity.Device;
import com.example.energy.entity.DeviceBuilding;
import com.example.energy.graphql.input.DeviceBuildingInput;
import com.example.energy.service.DeviceBuildingService;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLApi
public class DeviceBuildingGraphql {
    @Inject
    DeviceBuildingService deviceBuildingService;

    @Mutation("createDeviceBuilding")
    public DeviceBuilding createDeviceBuilding(DeviceBuildingInput input) {
        return deviceBuildingService.createDeviceBuilding(input);
    }

    @Query("getDeviceBuilding")
    public DeviceBuilding getDeviceBuilding(Integer deviceId, Integer buildingId) {
        return deviceBuildingService.getDeviceBuilding(deviceId, buildingId);
    }

    @Mutation("deleteDeviceBuilding")
    public boolean deleteDeviceBuilding(DeviceBuildingInput input) {
        return deviceBuildingService.deleteDeviceBuilding(input);
    }

    @Mutation("updateDeviceBuilding")
    public DeviceBuilding updateDeviceBuilding(DeviceBuildingInput input) {
        return deviceBuildingService.updateDeviceBuilding(input);
    }

    @Query("getAllDeviceBuilding")
    public List<DeviceBuilding> getAllDeviceBuilding(@DefaultValue("10") int limit, @DefaultValue("0") int offset, @DefaultValue("asc") String orderDirection) {
        return deviceBuildingService.getAllDeviceBuilding(limit, offset, orderDirection);
    }

    @Query("getDevicesByBuilding")
    public List<Device> getDevicesByBuilding(Integer buildingId) {
        return deviceBuildingService.getDevicesByBuildingId(buildingId);
    }
}
