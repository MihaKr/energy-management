package com.example.energy.graphql;

import com.example.energy.entity.Device;
import com.example.energy.graphql.input.DeviceInput;
import com.example.energy.service.DeviceService;
import graphql.GraphQLException;
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
        try {
            return deviceService.getDevice(deviceId);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Query("getAllDevices")
    public List<Device> getAllDevices(@DefaultValue("10") int limit, @DefaultValue("0") int offset, @DefaultValue("asc") String orderDirection) {
        try {
            return deviceService.getAllDevices(limit, offset, orderDirection);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Mutation("createDevice")
    public Device createDevice(DeviceInput input) {
        try {
            return deviceService.addDevice(input);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Mutation("deleteDevice")
    public boolean deleteDevice(DeviceInput input) {
        try {
            return deviceService.deleteDevice(input);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @Mutation("updateDevice")
    public Device updateDevice(DeviceInput input) {
        try {
        return deviceService.updateDevice(input);
        } catch (Exception e) {
            throw new GraphQLException(e.getMessage());
        }
    }
}
