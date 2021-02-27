package io.opelt.messagescheduler.adapter.web.controller;

import io.restassured.RestAssured;
import org.hamcrest.Matcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;
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
    public void givenAMessageWhenPostThenReturnResponseWithValidSchema() throws Exception {
        RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", "2021-02-26T20:13:23.47")
                        .put("recipient", "erick@opelt.dev")
                        .put("body", "Hello")
                        .put("channel", "EMAIL")
                        .toString())
                .when()
                .post("/v1/messages")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("message_schema.json"));
    }

    @Test
    public void givenAMessageWhenPostThenReturnBodyWithLinks() throws Exception {
        RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", "2021-02-26T20:13:23.47")
                        .put("recipient", "erick@opelt.dev")
                        .put("body", "Hello")
                        .put("channel", "EMAIL")
                        .toString())
                .when()
                .post("/v1/messages")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .header(LOCATION, matchMessageResourceURI())
                .body("id", matchesRegex(UUID_REGEX))
                .body("schedule", equalTo("2021-02-26T20:13:23.47"))
                .body("recipient", equalTo("erick@opelt.dev"))
                .body("channel", equalTo("EMAIL"))
                .body("body", equalTo("Hello"))
                .body("status", equalTo("SCHEDULED"))
                .body("_links.self.href", matchMessageResourceURI());
    }

    @Test
    public void givenAMessageIdWhenGetThenReturnResponseWithValidSchema() throws Exception {
        var id = createMessage();

        RestAssured
                .given()
                .pathParam("id", id)
                .when()
                .get("/v1/messages/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("message_schema.json"));
    }


    @Test
    public void givenAMessageIdWhenGetThenReturnBodyWithLinks() throws Exception {
        var id = createMessage();

        RestAssured
                .given()
                .pathParam("id", id)
                .when()
                .get("/v1/messages/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", matchesRegex(UUID_REGEX))
                .body("schedule", equalTo("2021-02-26T20:13:23.47"))
                .body("recipient", equalTo("erick@opelt.dev"))
                .body("channel", equalTo("EMAIL"))
                .body("body", equalTo("Hello"))
                .body("status", equalTo("SCHEDULED"))
                .body("_links.self.href", matchMessageResourceURI());
    }

    @Test
    public void givenAnInvalidIdWhenGetThenReturnNotFound() {
        var id = "invalid-id";

        RestAssured
                .given()
                .pathParam("id", id)
                .when()
                .get("/v1/messages/{id}")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private Matcher<String> matchMessageResourceURI() {
        var baseURI = String.format("http://localhost:%d/v1/messages/", port);
        return matchesRegex(baseURI + UUID_REGEX);
    }

    private String createMessage() throws JSONException {
        return RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", "2021-02-26T20:13:23.47")
                        .put("recipient", "erick@opelt.dev")
                        .put("body", "Hello")
                        .put("channel", "EMAIL")
                        .toString())
                .when()
                .post("/v1/messages")
                .then()
                .extract()
                .jsonPath()
                .getString("id");
    }

}