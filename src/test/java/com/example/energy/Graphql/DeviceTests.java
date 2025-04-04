package com.example.energy.Graphql;

import com.example.energy.util.GraphQLClient;
import com.example.energy.util.RandomStringUtil;
import com.example.energy.util.RandomTypeUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class DeviceTests {
    private static final String GRAPHQL_ENDPOINT = "/graphql";
    GraphQLClient client = new GraphQLClient(GRAPHQL_ENDPOINT);

    @Test
    public void testCreateAndRetrieveDevice() {
        String randomType = RandomTypeUtil.generate();
        String randomManufacturer = RandomStringUtil.generate(10);

        String createMutation = String.format("""
                mutation {
                      createDevice(input: {
                      type: %s
                      manufacturer: "%s"              
                   }) {
                     id
                     type
                     manufacturer
                   }
                }""", randomType, randomManufacturer);

        Response createResponse = client.sendGraphQLRequest(createMutation);
        Integer deviceId = createResponse.path("data.createDevice.id");

        assertNotNull(deviceId);

        String query = String.format("""
                query device {
                getDevice (deviceId: %d) {
                    id
                    type
                    manufacturer
                    }
                }""", deviceId);

        Response queryResponse = client.sendGraphQLRequest(query);

        assertEquals(deviceId, queryResponse.path("data.getDevice.id"));
        assertEquals(randomType, queryResponse.path("data.getDevice.type"));
        assertEquals(randomManufacturer, queryResponse.path("data.getDevice.manufacturer"));
    }

    @Test
    public void testUpdateDevice() {
        String randomType = RandomTypeUtil.generate();
        String randomManufacturer = RandomStringUtil.generate(10);

        String randomTypeUpdate = RandomTypeUtil.generate();
        String randomManufacturerUpdate = RandomStringUtil.generate(10);

        String createMutation = String.format("""
                mutation {
                      createDevice(input: {
                      type: %s
                      manufacturer: "%s"              
                   }) {
                     id
                     type
                     manufacturer
                   }
                }""", randomType, randomManufacturer);

        Response createResponse = client.sendGraphQLRequest(createMutation);
        Integer deviceId = createResponse.path("data.createDevice.id");

        String updateMutation = String.format("""
           mutation {
                updateDevice(input: {
                    id: %d,
                    type: %s
                    manufacturer: "%s"          
              }) {
                id
                type
                manufacturer
              }
           }""", deviceId, randomTypeUpdate, randomManufacturerUpdate);

        Response updateResponse = client.sendGraphQLRequest(updateMutation);

        String query = String.format("""
            query building {
            getDevice (deviceId: %d) {
                id,
                type,
                manufacturer,
                }
            }""", deviceId);

        Response queryResponse = client.sendGraphQLRequest(query);

        queryResponse.prettyPrint();

        assertEquals(deviceId, queryResponse.path("data.getDevice.id"));
        assertEquals(randomTypeUpdate, queryResponse.path("data.getDevice.type"));
        assertEquals(randomManufacturerUpdate, queryResponse.path("data.getDevice.manufacturer"));
    }

    @Test
    public void testDeleteDevice() {
        String randomType = RandomTypeUtil.generate();
        String randomManufacturer = RandomStringUtil.generate(10);

        String createMutation = String.format("""
                mutation {
                      createDevice(input: {
                      type: %s
                      manufacturer: "%s"              
                   }) {
                     id
                     type
                     manufacturer
                   }
                }""", randomType, randomManufacturer);

        Response createResponse = client.sendGraphQLRequest(createMutation);
        Integer deviceId = createResponse.path("data.createDevice.id");

        String deleteMutation = String.format("""
           mutation {
                deleteDevice(input: {
                    id: %d
              }) 
           }""", deviceId);

        Response updateResponse = client.sendGraphQLRequest(deleteMutation);

        String query = String.format("""
            query building {
            getDevice (id: %d) {
                id,
                type,
                manufacturer,
                }
            }""", deviceId);

        Response queryResponse = client.sendGraphQLRequest(query);
        assertNull(queryResponse.jsonPath().get("data.getDevice"));
    }

    @Test
    public void testCreateDeviceInvalidParameters() {
        String randomType = RandomTypeUtil.generate();
        String randomManufacturer = RandomStringUtil.generate(10);

        String createMutation = String.format("""
                mutation {
                      createDevice(input: {
                      manufacturer: "%s"              
                   }) {
                     id
                     manufacturer
                   }
                }""", randomManufacturer);

        Response createResponse = client.sendGraphQLRequest(createMutation);
        Integer deviceId = createResponse.path("data.createDevice.id");

        assertNotNull(createResponse.path("errors"));

        String errorMessage = createResponse.path("errors[0].message").toString();
        assertEquals("System error", errorMessage);

        assertEquals("createDevice", createResponse.path("errors[0].path[0]"));
        assertNull(createResponse.path("data.createDevice"));

    }

    @Test
    public void testGetNonExistentDevice() {
        int nonExistentId = 999999;

        String query = String.format("""
            query {
                getDevice(deviceId: %d) {
                    id
                    type
                    manufacturer
                }
            }""", nonExistentId);

        Response response = client.sendGraphQLRequest(query);
        assertNull(response.path("data.getDevice"));
    }

    @Test
    public void testUpdateNonExistentDevice() {
        int nonExistentId = 999999;
        String randomType = RandomTypeUtil.generate();
        String randomManufacturer = RandomStringUtil.generate(10);

        String updateMutation = String.format("""
           mutation {
                updateDevice(input: {
                    id: %d,
                    type: %s
                    manufacturer: "%s"          
              }) {
                id
                type
                manufacturer
              }
           }""", nonExistentId, randomType, randomManufacturer);

        Response updateResponse = client.sendGraphQLRequest(updateMutation);

        assertNotNull(updateResponse.path("errors"));
        assertNull(updateResponse.path("data.updateDevice"));
    }

    @Test
    public void testDeviceTypeValidation() {
        String invalidType = "INVALID_TYPE";
        String randomManufacturer = RandomStringUtil.generate(10);

        String createMutation = String.format("""
            mutation {
                  createDevice(input: {
                  type: %s
                  manufacturer: "%s"              
               }) {
                 id
                 type
                 manufacturer
               }
            }""", invalidType, randomManufacturer);

        Response createResponse = client.sendGraphQLRequest(createMutation);

        assertNotNull(createResponse.path("errors"));
        assertNull(createResponse.path("data.createDevice"));
    }

    @Test
    public void testGetListPagination() {
        for (int i = 0; i < 20; i++) {
            String randomType = RandomTypeUtil.generate();
            String randomManufacturer = RandomStringUtil.generate(10);

            String createMutation = String.format("""
                   mutation {
                        createDevice(input: {
                         type: %s
                         manufacturer: "%s"              
                      }) {
                        id
                        type
                        manufacturer
                      }
                   }""", randomType, randomManufacturer);

            Response createResponse = client.sendGraphQLRequest(createMutation);
            Integer deviceId = createResponse.path("data.createDevice.id");
        }

        String query1 = String.format("""
            query device {
            getAllDevices (limit: %s offset: 0, orderDirection: "desc") {
                id,
                type,
                manufacturer
                }
            }""", 20);

        Response queryResponse1 = client.sendGraphQLRequest(query1);
        List<Map<String, Object>> allDevices = queryResponse1.jsonPath().getList("data.getAllDevices");

        String query = ("""
            query device {
            getAllDevices (limit: 5 offset: 10, orderDirection: "desc"){
                id,
                type,
                manufacturer
                }
            }""");

        Response queryResponse = client.sendGraphQLRequest(query);
        List<Map<String, Object>> paginatedDevices = queryResponse.jsonPath().getList("data.getAllDevices");

        Assertions.assertNotNull(paginatedDevices);
        Assertions.assertEquals(5, paginatedDevices.size(), "Should return exactly 5 devices");

        for (int i = 0; i < paginatedDevices.size(); i++) {
            Map<String, Object> paginatedDevice = paginatedDevices.get(i);
            Map<String, Object> expectedDevice = allDevices.get(i + 10);

            Assertions.assertEquals(expectedDevice.get("id"), paginatedDevice.get("id"),
                    "Device ID at index " + i + " should match");
            Assertions.assertEquals(expectedDevice.get("type"), paginatedDevice.get("type"),
                    "Device type at index " + i + " should match");
            Assertions.assertEquals(expectedDevice.get("manufacturer"), paginatedDevice.get("manufacturer"),
                    "Device manufacturer at index " + i + " should match");
        }
    }

}

