package com.example.energy.graphql;

import com.example.energy.entity.Device;
import com.example.energy.graphql.input.DeviceInput;
import com.example.energy.service.DeviceService;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLApi
public class DeviceGraphql {
    @Inject
    DeviceService deviceService;

    @Query("getDevice")
    public Device getDevice(Integer deviceId) {
        return deviceService.getDevice(deviceId);
    }

    @Query("getAllDevices")
    public List<Device> getAllDevices(@DefaultValue("10") int limit, @DefaultValue("0") int offset, @DefaultValue("asc") String orderDirection) {
        return deviceService.getAllDevices(limit, offset, orderDirection);
    }

    @Mutation("createDevice")
    public Device createDevice(DeviceInput input) {
        return deviceService.addDevice(input);
    }

    @Mutation("deleteDevice")
    public boolean deleteDevice(DeviceInput input) {
        return deviceService.deleteDevice(input);
    }

    @Mutation("updateDevice")
    public Device updateDevice(DeviceInput input) {
        return deviceService.updateDevice(input);
    }
}
