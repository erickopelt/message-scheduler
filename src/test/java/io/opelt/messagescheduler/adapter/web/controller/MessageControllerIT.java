package io.opelt.messagescheduler.adapter.web.controller;

import io.restassured.RestAssured;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static java.lang.String.format;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageControllerIT {

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
                .statusCode(SC_CREATED)
                .header(LOCATION, matchesPattern(format("http://localhost:%d/v1/messages/.*", port)))
                .body("id", notNullValue())
                .body("schedule", equalTo("2021-02-26T20:13:23.47"))
                .body("destiny", equalTo("erick@opelt.dev"))
                .body("channel", equalTo("EMAIL"))
                .body("body", equalTo("Hello"))
                .body("_links.self.href", matchesPattern(format("http://localhost:%d/v1/messages/.*", port)));
    }
}