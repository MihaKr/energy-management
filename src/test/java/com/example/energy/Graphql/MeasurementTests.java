package com.example.energy.Graphql;

import com.example.energy.util.GraphQLClient;
import com.example.energy.util.RandomStringUtil;
import com.example.energy.util.RandomTypeUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hibernate.sql.Update;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class MeasurementTests {
    private static final String GRAPHQL_ENDPOINT = "/graphql";

    GraphQLClient client = new GraphQLClient(GRAPHQL_ENDPOINT);

    private Response createBuilding() {
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

        return client.sendGraphQLRequest(createMutation);
    }

    private Response createDevice() {
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

        return client.sendGraphQLRequest(createMutation);
    }

    private Response getMeasurement(Integer measurementId) {
        String query = String.format("""
        query {
            getMeasurement(measurementId: %d) {
                id
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
                timestamp
                energyKwh
            }
        }""", measurementId);

        return client.sendGraphQLRequest(query);
    }

    private Response createMeasurement(Integer deviceId, Integer buildingId, Instant timestamp, Float energyKwh) {
        Instant actualTimestamp = (timestamp != null) ? timestamp : Instant.now();

        String createMutation = String.format("""
            mutation {
                createMeasurement(input: {
                    deviceId: %d
                    buildingId: %d
                    timestamp: "%s"
                    energyKwh: %f
                }) {
                    id
                }
            }""", deviceId, buildingId, actualTimestamp, energyKwh);

        return client.sendGraphQLRequest(createMutation);
    }

    private Response updateMeasurement(Integer measurementId, String timestamp, float energyKwh) {
        String updateMutation = String.format("""
        mutation {
            updateMeasurement(input: {
                id: %d
                timestamp: "%s"
                energyKwh: %f
            }) {
                id
                device {
                    id
                }
                building {
                    id
                }
                timestamp
                energyKwh
            }
        }""", measurementId, timestamp, energyKwh);

        return client.sendGraphQLRequest(updateMutation);
    }

    private Response removeMeasurement(Integer measurementId) {
        String updateMutation = String.format("""
        mutation {
            removeMeasurement(input: {
                id: %d
            })
        }""", measurementId);

        return client.sendGraphQLRequest(updateMutation);
    }

    @Test
    public void testCreateAndRetrieveMeasurement() {
        Response buildingResponse = createBuilding();
        Response deviceResponse = createDevice();
        Integer buildingId = buildingResponse.path("data.createBuilding.id");
        Integer deviceId = deviceResponse.path("data.createDevice.id");
        Float energyKwh = 42.5f;


        Response measurementCreate = createMeasurement(deviceId, buildingId, null, energyKwh);
        Integer measurementId = measurementCreate.path("data.createMeasurement.id");

        Response measurementResponse = getMeasurement(measurementId);

        int responseDeviceId = measurementResponse.path("data.getMeasurement.device.id");
        int responseBuildingId = measurementResponse.path("data.getMeasurement.building.id");
        Float energyKwhResp = measurementResponse.path("data.getMeasurement.energyKwh");
        String timestamp = measurementResponse.path("data.getMeasurement.timestamp");

        assertEquals(measurementId, measurementResponse.path("data.getMeasurement.id"));
        assertEquals(deviceId.intValue(), responseDeviceId);
        assertEquals(buildingId.intValue(), responseBuildingId);
        assertEquals(energyKwh, energyKwhResp, 0.001f);
    }

    @Test
    public void testUpdateMeasurement() {
        Response buildingResponse = createBuilding();
        Response deviceResponse = createDevice();
        Integer buildingId = buildingResponse.path("data.createBuilding.id");
        Integer deviceId = deviceResponse.path("data.createDevice.id");

        Float energyKwh = 42.5f;

        Response measurementCreate = createMeasurement(deviceId, buildingId, null, energyKwh);
        Integer measurementId = measurementCreate.path("data.createMeasurement.id");

        Response initialResponse = getMeasurement(measurementId);
        String initialTimestamp = initialResponse.path("data.getMeasurement.timestamp");
        float initialEnergyKwh = initialResponse.path("data.getMeasurement.energyKwh");

        float newEnergyKwh = 75.8f;
        String newTimestamp = Instant.now().plusSeconds(3600).toString(); // One hour later

        Response updateResponse = updateMeasurement(
                measurementId, newTimestamp, newEnergyKwh);

        Response updatedResponse = getMeasurement(measurementId);

        int responseDeviceId = updatedResponse.path("data.getMeasurement.device.id");
        int responseBuildingId = updatedResponse.path("data.getMeasurement.building.id");
        float updatedEnergyKwh = updatedResponse.path("data.getMeasurement.energyKwh");
        String updatedTimestamp = updatedResponse.path("data.getMeasurement.timestamp");

        assertEquals(measurementId, updatedResponse.path("data.getMeasurement.id"));

        Instant originalInstant = Instant.parse(initialTimestamp);
        Instant newInstant = Instant.parse(newTimestamp);
        Instant updatedInstant = Instant.parse(updatedTimestamp);

        long timeDifference = Math.abs(newInstant.getEpochSecond() - updatedInstant.getEpochSecond());
        assertTrue(timeDifference < 1);

        assertEquals(newEnergyKwh, updatedEnergyKwh, 0.001f);

        assertNotEquals(initialEnergyKwh, updatedEnergyKwh);

        assertNotEquals(initialTimestamp, updatedTimestamp);
    }

    @Test
    public void testRemoveMeasurement() {
        Response buildingResponse = createBuilding();
        Response deviceResponse = createDevice();
        Integer buildingId = buildingResponse.path("data.createBuilding.id");
        Integer deviceId = deviceResponse.path("data.createDevice.id");

        Float energyKwh = 42.5f;

        Response measurementCreate = createMeasurement(deviceId, buildingId, null, energyKwh);
        Integer measurementId = measurementCreate.path("data.createMeasurement.id");

        Response initialResponse = getMeasurement(measurementId);
        assertNotNull(initialResponse.path("data.getMeasurement"));

        Response removeResponse = removeMeasurement(measurementId);
        Response measurementResponse = getMeasurement(measurementId);

        assertNull(measurementResponse.jsonPath().get("data.getMeasurement"));
    }

    @Test
    public void testCreateMeasurementWithNonExistentDevice() {
        Response createResponseB = createBuilding();
        Integer buildingId = createResponseB.path("data.createBuilding.id");

        Response createResponse = createMeasurement(999999, buildingId, null,42.5f);
        createResponse.prettyPrint();

        assertNotNull(createResponse.path("errors"));
        assertInstanceOf(List.class, createResponse.path("errors"));
        assertFalse(((List<?>)createResponse.path("errors")).isEmpty());
    }

    @Test
    public void testCreateMeasurementWithNonExistentBuilding() {
        Response createResponseB = createDevice();
        Integer deviceId = createResponseB.path("data.createDevice.id");

        Response createResponse = createMeasurement(deviceId, 999999, null, 42.5f);
        createResponse.prettyPrint();

        assertNotNull(createResponse.path("errors"));
        assertInstanceOf(List.class, createResponse.path("errors"));
        assertFalse(((List<?>)createResponse.path("errors")).isEmpty());
    }

    @Test
    public void testUpdateNonExistentMeasurement() {
        Response updateResponse = updateMeasurement(999999, "2023-01-01T12:00:00Z", 42.5f);

        assertNotNull(updateResponse.path("errors"));
        assertNull(updateResponse.path("data.updateMeasurement"));
    }

    @Test
    public void testDeleteNonExistentMeasurement() {
        Response removeResponse = removeMeasurement(999999);
        assertNotNull(removeResponse);
    }

    @Test
    public void testGetEnergyConsumptionByDevice() {
        Response buildingResponse = createBuilding();
        Response deviceResponse = createDevice();
        Integer buildingId = buildingResponse.path("data.createBuilding.id");
        Integer deviceId = deviceResponse.path("data.createDevice.id");

        Instant now = Instant.now();
        Instant oneWeekAgo = now.minus(Duration.ofDays(7));
        Instant twoWeeksAgo = now.minus(Duration.ofDays(14));

        createMeasurement(deviceId, buildingId, now.minus(Duration.ofDays(1)), 10.5f);
        createMeasurement(deviceId, buildingId, now.minus(Duration.ofDays(2)), 15.3f);
        createMeasurement(deviceId, buildingId, now.minus(Duration.ofDays(3)), 12.7f);

        createMeasurement(deviceId, buildingId, twoWeeksAgo, 25.0f);

        float expectedEnergySum = 38.5f;

        // Test the energy consumption by device query
        String query = String.format("""
            query {
                getEnergyConsumptionByDevice(
                    deviceId: %d,
                    from: "%s",
                    to: "%s"
                )
            }""", deviceId, oneWeekAgo.toString(), now.toString());

        Response response = client.sendGraphQLRequest(query);
        Float actualEnergyConsumption = response.path("data.getEnergyConsumptionByDevice");

        assertNotNull(actualEnergyConsumption);
        assertEquals(expectedEnergySum, actualEnergyConsumption, 0.1f);
    }
}


