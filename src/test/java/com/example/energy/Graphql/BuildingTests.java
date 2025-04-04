package com.example.energy.Graphql;

import com.example.energy.util.GraphQLClient;
import com.example.energy.util.RandomStringUtil;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class BuildingTests {
    private static final String GRAPHQL_ENDPOINT = "/graphql";
    GraphQLClient client = new GraphQLClient(GRAPHQL_ENDPOINT);

    private Response createBuilding(String name, String location) {
        String createMutation = String.format("""
            mutation {
                createBuilding(input: {
                    name: "%s"
                    location: "%s"
                }) {
                    id
                }
            }""", name, location);

        return client.sendGraphQLRequest(createMutation);
    }

    private Response getBuilding(Integer buildingId) {
        String query = String.format("""
            query building {
            getBuilding (buildingId: %d) {
                id,
                name,
                location,
                }
            }""", buildingId);

        return client.sendGraphQLRequest(query);
    }

    private Response updateBuilding(Integer buildingId, String newName, String newLocation) {
        String updateMutation = String.format("""
           mutation {
                updateBuilding(input: {
                    buildingId: %d,
                    name: "%s"
                    location: "%s"          
              }) {
                id
                name
                location
              }
           }""", buildingId, newName, newLocation);

        return client.sendGraphQLRequest(updateMutation);
    }

    private Response deleteBuilding(Integer buildingId) {
        String deleteMutation = String.format("""
           mutation {
                deleteBuilding(input: {
                    buildingId: %d
              })
           }""", buildingId);

        return client.sendGraphQLRequest(deleteMutation);
    }

    @Test
    public void testCreateAndRetrieveBuilding() {
        String randomBuildingName = RandomStringUtil.generate(10);
        String randomLocation = RandomStringUtil.generate(10);

        Response building = createBuilding(randomBuildingName, randomLocation);
        Integer buildingId = building.path("data.createBuilding.id");

        assertNotNull(buildingId);
        Response buildingResponse = getBuilding(buildingId);
        assertEquals(buildingId, buildingResponse.path("data.getBuilding.id"));
    }

    @Test
    public void testDuplicateNames() {
        String randomBuildingName = RandomStringUtil.generate(10);
        String randomLocation = RandomStringUtil.generate(10);

        Response createResponse = createBuilding(randomBuildingName, randomLocation);
        createResponse.then()
                .statusCode(200)
                .body("data.createBuilding.id", notNullValue());

        Response createResponse2 = createBuilding(randomBuildingName, randomLocation);
        createResponse2.then()
                .statusCode(200)
                .body("errors", notNullValue())
                .body("errors[0].message", equalTo("System error"));

        createResponse2.then()
                .body("data.createBuilding", nullValue());
    }

    @Test
    public void testUpdateBuilding() {
        String randomBuildingName = RandomStringUtil.generate(10);
        String randomLocation = RandomStringUtil.generate(10);

        String updatedRandomBuildingName = RandomStringUtil.generate(10);
        String UpdatedRandomLocation = RandomStringUtil.generate(10);

        Response createResponse = createBuilding(randomBuildingName, randomLocation);
        Integer buildingId = createResponse.path("data.createBuilding.id");

        Response updateResponse = updateBuilding(buildingId, updatedRandomBuildingName, UpdatedRandomLocation);

        Response queryResponse = getBuilding(buildingId);

        assertEquals(buildingId, queryResponse.path("data.getBuilding.id"));
        assertEquals(updatedRandomBuildingName, queryResponse.path("data.getBuilding.name"));
        assertEquals(UpdatedRandomLocation, queryResponse.path("data.getBuilding.location"));
    }

    @Test
    public void testDeleteBuilding() {
        String randomBuildingName = RandomStringUtil.generate(10);
        String randomLocation = RandomStringUtil.generate(10);

        Response createResponse = createBuilding(randomBuildingName, randomLocation);
        Integer buildingId = createResponse.path("data.createBuilding.id");

        Response deleteResponse = deleteBuilding(buildingId);
        deleteResponse.prettyPrint();
        assertEquals(true, deleteResponse.jsonPath().get("data.deleteBuilding"));

        Response queryResponse = getBuilding(buildingId);
        assertNull(queryResponse.jsonPath().get("data.getBuilding"));
    }

    @Test
    public void testGetListPagination() {
        for (int i = 0; i < 20; i++) {
            String randomBuildingName = RandomStringUtil.generate(10);
            String randomLocation = RandomStringUtil.generate(10);

            String createMutation = String.format("""
               mutation {
                    createBuilding(input: {
                     name: "%s"
                     location: "%s"
                  }) {
                    id
                    name
                    location
                  }
               }""", randomBuildingName, randomLocation);

            Response createResponse = client.sendGraphQLRequest(createMutation);
            Integer buildingId = createResponse.path("data.createBuilding.id");
        }

        String query1 = String.format("""
            query building {
            getAllBuildings (limit: %s offset: 0, orderDirection: "desc") {
                id,
                name,
                location,
                }
            }""", 20);

        Response queryResponse1 = client.sendGraphQLRequest(query1);
        List<Map<String, Object>> allBuildings = queryResponse1.jsonPath().getList("data.getAllBuildings");

        String query = ("""
            query building {
            getAllBuildings (limit: 5 offset: 10, orderDirection: "desc"){
                id,
                name,
                location,
                }
            }""");

        Response queryResponse = client.sendGraphQLRequest(query);
        List<Map<String, Object>> paginatedBuildings = queryResponse.jsonPath().getList("data.getAllBuildings");

        Assertions.assertNotNull(paginatedBuildings);
        Assertions.assertEquals(5, paginatedBuildings.size(), "Should return exactly 5 buildings");

        for (int i = 0; i < paginatedBuildings.size(); i++) {
            Map<String, Object> paginatedBuilding = paginatedBuildings.get(i);
            Map<String, Object> expectedBuilding = allBuildings.get(i + 10);

            Assertions.assertEquals(expectedBuilding.get("id"), paginatedBuilding.get("id"),
                    "Building ID at index " + i + " should match");
            Assertions.assertEquals(expectedBuilding.get("name"), paginatedBuilding.get("name"),
                    "Building name at index " + i + " should match");
            Assertions.assertEquals(expectedBuilding.get("location"), paginatedBuilding.get("location"),
                    "Building location at index " + i + " should match");
        }
    }

    @Test
    public void testEmptyPaginationResults() {
        String query = ("""
            query building {
            getAllBuildings (limit: 5 offset: 100000){
                id,
                name,
                location,
                }
            }""");

        Response queryResponse = client.sendGraphQLRequest(query);
        List<Map<String, Object>> buildings = queryResponse.jsonPath().getList("data.getAllBuildings");

        Assertions.assertNotNull(buildings, "Response should not be null");
        Assertions.assertTrue(buildings.isEmpty(), "Building list should be empty");
    }

    @Test
    public void testInvalidDirection() {
        String query = ("""
            query building {
            getAllBuildings (limit: 5 offset: 10, orderDirection: "abc") {
                id,
                name,
                location,
                }
            }""");

        Response queryResponse = client.sendGraphQLRequest(query);
        List<Map<String, Object>> buildings = queryResponse.jsonPath().getList("data.getAllBuildings");

        Assertions.assertNotNull(buildings, "Response should not be null");
    }

    @Test
    public void testCreateBuildingInvalidParameters() {
        String randomLocation = RandomStringUtil.generate(10);

        String createMutation = String.format("""
            mutation {
                createBuilding(input: {
                 location: "%s"
              }) {
                id
                location
              }
            }""", randomLocation);

        Response createResponse = client.sendGraphQLRequest(createMutation);

        assertNotNull(createResponse.path("errors"));

        String errorMessage = createResponse.path("errors[0].message").toString();
        assertEquals("System error", errorMessage);

        assertEquals("createBuilding", createResponse.path("errors[0].path[0]"));
        assertNull(createResponse.path("data.createBuilding"));
    }
    @Test
    public void testCreateMeasurementWithNonExistentDevice() {
        String createMutationB = String.format("""
               mutation {
                    createBuilding(input: {
                     name: "%s"
                     location: "%s"
                  }) {
                    id
                    name
                    location
                  }
               }""", "testiranje", "test street");

        Response createResponseB = client.sendGraphQLRequest(createMutationB);
        Integer buildingId = createResponseB.path("data.createBuilding.id");

        String createMutation = String.format("""
            mutation {
                createMeasurement(input: {
                deviceId: %d
                buildingId: %d
                timestamp: "%s"
                energyKwh: %.2f
            }) {
                id
            }
            }""", 999999, buildingId, "2023-01-01T12:00:00Z", 42.5);

        Response createResponse = client.sendGraphQLRequest(createMutation);
        createResponse.prettyPrint();

        assertNotNull(createResponse.path("errors"));
        assertInstanceOf(List.class, createResponse.path("errors"));
        assertFalse(((List<?>)createResponse.path("errors")).isEmpty());
    }
}


