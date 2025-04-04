package com.example.energy.util;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class GraphQLClient {
    private static String graphQLEndpoint;

    public GraphQLClient(String graphQLEndpoint) {
        GraphQLClient.graphQLEndpoint = graphQLEndpoint;
    }

    public Response sendGraphQLRequest(String query) {
        return given()
                .contentType(ContentType.JSON)
                .body(Map.of("query", query))
                .post(graphQLEndpoint)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public Response validateResponse(Response response) {
        if (response.statusCode() != 200) {
            throw new RuntimeException("GraphQL request failed with status code: " + response.statusCode());
        }

        if (response.path("errors") != null) {
            throw new RuntimeException("GraphQL request returned errors: " + response.path("errors"));
        }

        return response;
    }
}