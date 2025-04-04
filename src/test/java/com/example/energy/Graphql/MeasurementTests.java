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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class MeasurementTests {
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
        Integer buildingId = response.path("data.createBuilding.id");
        return buildingId;
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
        Integer deviceId = response.path("data.createDevice.id");
        return deviceId;
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

    private Integer createMeasurement(Integer deviceId, Integer buildingId) {
        float energyKwh = 42.5f;
        String timestamp = Instant.now().toString();

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
            }""", deviceId, buildingId, timestamp, energyKwh);

        Response response = client.sendGraphQLRequest(createMutation);
        Integer measurementId = response.path("data.createMeasurement.id");
        return measurementId;
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
        Integer buildingId = createBuilding();
        Integer deviceId = createDevice();
        Integer measurementId = createMeasurement(deviceId, buildingId);
        Response measurementResponse = getMeasurement(measurementId);

        int responseDeviceId = measurementResponse.path("data.getMeasurement.device.id");
        int responseBuildingId = measurementResponse.path("data.getMeasurement.building.id");
        float energyKwh = measurementResponse.path("data.getMeasurement.energyKwh");
        String timestamp = measurementResponse.path("data.getMeasurement.timestamp");

        assertEquals(measurementId, measurementResponse.path("data.getMeasurement.id"));
        assertEquals(deviceId.intValue(), responseDeviceId);
        assertEquals(buildingId.intValue(), responseBuildingId);
        assertEquals(42.5f, energyKwh, 0.001f);
    }

    @Test
    public void testUpdateMeasurement() {
        Integer buildingId = createBuilding();
        Integer deviceId = createDevice();

        Integer measurementId = createMeasurement(deviceId, buildingId);

        Response initialResponse = getMeasurement(measurementId);
        String initialTimestamp = initialResponse.path("data.getMeasurement.timestamp");
        float initialEnergyKwh = initialResponse.path("data.getMeasurement.energyKwh");

        Integer newBuildingId = createBuilding();

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
        assertEquals(newTimestamp, updatedTimestamp);
        assertEquals(newEnergyKwh, updatedEnergyKwh, 0.001f);

        assertNotEquals(initialEnergyKwh, newEnergyKwh);
        assertNotEquals(initialTimestamp, newTimestamp);
    }

    @Test
    public void testRemoveMeasurement() {
        Integer buildingId = createBuilding();
        Integer deviceId = createDevice();

        Integer measurementId = createMeasurement(deviceId, buildingId);

        Response initialResponse = getMeasurement(measurementId);
        assertNotNull(initialResponse.path("data.getMeasurement"));

        Response removeResponse = removeMeasurement(measurementId);
        Response measurementResponse = getMeasurement(measurementId);

        assertNull(measurementResponse.jsonPath().get("data.getMeasurement"));
    }
}


