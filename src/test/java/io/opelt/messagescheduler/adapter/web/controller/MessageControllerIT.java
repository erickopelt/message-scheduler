package io.opelt.messagescheduler.adapter.web.controller;

import io.restassured.RestAssured;
import org.hamcrest.Matcher;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.apache.http.HttpHeaders.LOCATION;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageControllerIT {

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void givenAMessageWhenPostThenReturnBodyWithLinks() throws Exception {
        RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", "2021-02-26T20:13:23.47")
                        .put("destiny", "erick@opelt.dev")
                        .put("body", "Hello")
                        .put("channel", "EMAIL")
                        .toString())
                .when()
                .post("/v1/messages")
                .then()
                .log()
                .all()
                .statusCode(SC_CREATED)
                .header(LOCATION, matchMessageResourceURI())
                .body("id", matchesRegex(UUID_REGEX))
                .body("schedule", equalTo("2021-02-26T20:13:23.47"))
                .body("destiny", equalTo("erick@opelt.dev"))
                .body("channel", equalTo("EMAIL"))
                .body("body", equalTo("Hello"))
                .body("_links.self.href", matchMessageResourceURI());
    }

    private Matcher<String> matchMessageResourceURI() {
        var baseURI = String.format("http://localhost:%d/v1/messages/", port);
        return matchesRegex(baseURI + UUID_REGEX);
    }

}