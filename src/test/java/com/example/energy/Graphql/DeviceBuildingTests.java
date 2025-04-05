package com.example.energy.Graphql;

import com.example.energy.util.GraphQLClient;
import com.example.energy.util.RandomStringUtil;
import com.example.energy.util.RandomTypeUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class DeviceBuildingTests {
    private static final String GRAPHQL_ENDPOINT = "/graphql";
    GraphQLClient client = new GraphQLClient(GRAPHQL_ENDPOINT);

    private Integer createBuilding() {
        String randomBuildingName = RandomStringUtil.generate(10);
        String randomLocation = RandomStringUtil.generate(10);

        String createMutation = String.format("""
            mutation {
                createBuilding(input: {
                    name: "%s"
                    location: "%s"
                }) {
                    id
                }
            }""", randomBuildingName, randomLocation);

        Response response = client.sendGraphQLRequest(createMutation);
        return response.path("data.createBuilding.id");
    }

    private Integer createDevice() {
        String randomType = RandomTypeUtil.generate();
        String randomManufacturer = RandomStringUtil.generate(10);

        String createMutation = String.format("""
            mutation {
                createDevice(input: {
                    type: %s
                    manufacturer: "%s"
                }) {
                    id
                }
            }""", randomType, randomManufacturer);

        Response response = client.sendGraphQLRequest(createMutation);
        return response.path("data.createDevice.id");
    }

    private Response createDeviceBuilding(Integer deviceId, Integer buildingId, String installedSince) {
        String createMutation = String.format("""
            mutation {
                createDeviceBuilding(input: {
                    deviceId: %d
                    buildingId: %d
                    installedSince: "%s"
                }) {
                    deviceId
                    buildingId
                    installedSince
                }
            }""", deviceId, buildingId, installedSince);

        return client.sendGraphQLRequest(createMutation);
    }

    private Response getDeviceBuilding(Integer deviceId, Integer buildingId) {
        String query = String.format("""
            query {
                getDeviceBuilding(deviceId: %d, buildingId: %d) {
                    deviceId
                    buildingId
                    installedSince
                    device {
                        id
                        type
                        manufacturer
                    }
                    building {
                        id
                        name
                        location
                    }
                }
            }""", deviceId, buildingId);

        return client.sendGraphQLRequest(query);
    }

    private Response updateDeviceBuilding(Integer deviceId, Integer buildingId, String newInstalledSince) {
        String updateMutation = String.format("""
            mutation {
                updateDeviceBuilding(input: {
                    deviceId: %d
                    buildingId: %d
                    installedSince: "%s"
                }) {
                    deviceId
                    buildingId
                    installedSince
                }
            }""", deviceId, buildingId, newInstalledSince);

        return client.sendGraphQLRequest(updateMutation);
    }

    private Response deleteDeviceBuilding(Integer deviceId, Integer buildingId) {
        String deleteMutation = String.format("""
            mutation {
                deleteDeviceBuilding(input: {
                    deviceId: %d
                    buildingId: %d
                })
            }""", deviceId, buildingId);

        return client.sendGraphQLRequest(deleteMutation);
    }

    private Response getDevicesByBuilding(Integer buildingId) {
        String query = String.format("""
            query {
                getDevicesByBuilding(buildingId: %d) {
                    id
                    type
                    manufacturer
                }
            }""", buildingId);

        return client.sendGraphQLRequest(query);
    }

    @Test
    public void testCreateAndRetrieveDeviceBuilding() {
        Integer buildingId = createBuilding();
        Integer deviceId = createDevice();
        String installedSince = Instant.now().toString();

        Response createResponse = createDeviceBuilding(deviceId, buildingId, installedSince);
        assertNotNull(createResponse.path("data.createDeviceBuilding"));

        Response getResponse = getDeviceBuilding(deviceId, buildingId);
        assertEquals(deviceId, getResponse.path("data.getDeviceBuilding.deviceId"));
        assertEquals(buildingId, getResponse.path("data.getDeviceBuilding.buildingId"));

        String retrievedTimestamp = getResponse.path("data.getDeviceBuilding.installedSince");
        assertNotNull(retrievedTimestamp);

        Instant originalInstant = Instant.parse(installedSince);
        Instant retrievedInstant = Instant.parse(retrievedTimestamp);

        long timeDifference = Math.abs(originalInstant.getEpochSecond() - retrievedInstant.getEpochSecond());
        assertTrue(timeDifference < 1);
    }

    @Test
    public void testUpdateDeviceBuilding() {
        Integer buildingId = createBuilding();
        Integer deviceId = createDevice();
        String installedSince = Instant.now().toString();

        createDeviceBuilding(deviceId, buildingId, installedSince);

        String newInstalledSince = Instant.now().plusSeconds(3600).toString();
        Response updateResponse = updateDeviceBuilding(deviceId, buildingId, newInstalledSince);

        updateResponse.prettyPrint();

        Response getResponse = getDeviceBuilding(deviceId, buildingId);

        getResponse.prettyPrint();

        Instant expectedInstant = Instant.parse(newInstalledSince);
        Instant actualInstant = Instant.parse(getResponse.path("data.getDeviceBuilding.installedSince").toString());

        long differenceInMillis = Math.abs(
                Duration.between(expectedInstant, actualInstant).toMillis()
        );

        assertTrue(differenceInMillis <= 100,
                "Timestamps should be within 100 milliseconds, but difference was " +
                        differenceInMillis + " milliseconds");
    }

    @Test
    public void testDeleteDeviceBuilding() {
        Integer buildingId = createBuilding();
        Integer deviceId = createDevice();
        String installedSince = Instant.now().toString();

        createDeviceBuilding(deviceId, buildingId, installedSince);

        Response deleteResponse = deleteDeviceBuilding(deviceId, buildingId);
        assertEquals(true, deleteResponse.path("data.deleteDeviceBuilding"));

        Response getResponse = getDeviceBuilding(deviceId, buildingId);
        assertNull(getResponse.path("data.getDeviceBuilding"));
    }

    @Test
    public void testGetDevicesByBuilding() {
        Integer buildingId = createBuilding();

        Integer deviceId1 = createDevice();
        Integer deviceId2 = createDevice();
        String installedSince = Instant.now().toString();

        createDeviceBuilding(deviceId1, buildingId, installedSince);
        createDeviceBuilding(deviceId2, buildingId, installedSince);

        Response getDevicesResponse = getDevicesByBuilding(buildingId);
        List<Map<String, Object>> devices = getDevicesResponse.jsonPath().getList("data.getDevicesByBuilding");

        assertNotNull(devices);
        assertEquals(2, devices.size());

        boolean foundDevice1 = false;
        boolean foundDevice2 = false;

        for (Map<String, Object> device : devices) {
            Integer id = (Integer) device.get("id");
            if (id.equals(deviceId1)) {
                foundDevice1 = true;
            } else if (id.equals(deviceId2)) {
                foundDevice2 = true;
            }
        }

        assertTrue(foundDevice1);
        assertTrue(foundDevice2);
    }

    @Test
    public void testCreateDuplicateDeviceBuilding() {
        Integer buildingId = createBuilding();
        Integer deviceId = createDevice();
        String installedSince = Instant.now().toString();

        Response firstCreateResponse = createDeviceBuilding(deviceId, buildingId, installedSince);
        assertNotNull(firstCreateResponse.path("data.createDeviceBuilding"));

        Response secondCreateResponse = createDeviceBuilding(deviceId, buildingId, installedSince);

        assertNotNull(secondCreateResponse.path("errors"));
        assertNull(secondCreateResponse.path("data.createDeviceBuilding"));
    }

    @Test
    public void testCreateDeviceBuildingWithNonExistentDevice() {
        Integer buildingId = createBuilding();
        Integer nonExistentDeviceId = 99999;
        String installedSince = Instant.now().toString();

        Response createResponse = createDeviceBuilding(nonExistentDeviceId, buildingId, installedSince);

        assertNotNull(createResponse.path("errors"));
        assertNull(createResponse.path("data.createDeviceBuilding"));
    }

    @Test
    public void testCreateDeviceBuildingWithNonExistentBuilding() {
        Integer deviceId = createDevice();
        Integer nonExistentBuildingId = 99999;
        String installedSince = Instant.now().toString();

        Response createResponse = createDeviceBuilding(deviceId, nonExistentBuildingId, installedSince);

        assertNotNull(createResponse.path("errors"));
        assertNull(createResponse.path("data.createDeviceBuilding"));
    }

    @Test
    public void testGetAllDeviceBuilding() {
        Integer buildingId1 = createBuilding();
        Integer buildingId2 = createBuilding();
        Integer deviceId1 = createDevice();
        Integer deviceId2 = createDevice();
        String installedSince = Instant.now().toString();

        createDeviceBuilding(deviceId1, buildingId1, installedSince);
        createDeviceBuilding(deviceId2, buildingId2, installedSince);

        String query = """
            query {
                getAllDeviceBuilding(limit: 10, offset: 0) {
                    deviceId
                    buildingId
                    installedSince
                    device {
                        id
                    }
                    building {
                        id
                    }
                }
            }""";

        Response getAllResponse = client.sendGraphQLRequest(query);
        List<Map<String, Object>> allRelationships = getAllResponse.jsonPath().getList("data.getAllDeviceBuilding");

        assertNotNull(allRelationships);
        assertTrue(allRelationships.size() >= 2);

        boolean foundRelationship1 = false;
        boolean foundRelationship2 = false;

        for (Map<String, Object> relationship : allRelationships) {
            Integer deviceId = (Integer) relationship.get("deviceId");
            Integer buildingId = (Integer) relationship.get("buildingId");

            if (deviceId.equals(deviceId1) && buildingId.equals(buildingId1)) {
                foundRelationship1 = true;
            } else if (deviceId.equals(deviceId2) && buildingId.equals(buildingId2)) {
                foundRelationship2 = true;
            }
        }

        assertTrue(foundRelationship1);
        assertTrue(foundRelationship2);
    }
}