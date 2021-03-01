package io.opelt.messagescheduler.adapter.web.controller;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.LocalDateTime;

import org.hamcrest.Matcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageControllerTest {

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void givenAMessageWhenPostThenReturnResponseWithValidSchema() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).toString();
        RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", schedule)
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
    void givenAMessageWhenPostThenReturnBodyWithLinks() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).toString();
        RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", schedule)
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
                .body("schedule", equalTo(schedule))
                .body("recipient", equalTo("erick@opelt.dev"))
                .body("channel", equalTo("EMAIL"))
                .body("body", equalTo("Hello"))
                .body("status", equalTo("SCHEDULED"))
                .body("_links.self.href", matchMessageResourceURI());
    }

    @Test
    void givenAMessageWithoutRecipientFieldWhenPostThenReturnBadRequest() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).toString();
        RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", schedule)
                        .put("body", "Hello")
                        .put("channel", "EMAIL")
                        .toString())
                .when()
                .post("/v1/messages")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("Field 'recipient' error: must not be blank"));
    }

    @Test
    void givenAMessageWithoutBodyFieldWhenPostThenReturnBadRequest() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).toString();
        RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", schedule)
                        .put("recipient", "erick@opelt.dev")
                        .put("channel", "EMAIL")
                        .toString())
                .when()
                .post("/v1/messages")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("Field 'body' error: must not be blank"));
    }

    @Test
    void givenAMessageWithoutChannelFieldWhenPostThenReturnBadRequest() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).toString();
        RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", schedule)
                        .put("recipient", "erick@opelt.dev")
                        .put("body", "Hello")
                        .toString())
                .when()
                .post("/v1/messages")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("Field 'channel' error: must not be null"));
    }

    @Test
    void givenAMessageWithoutScheduleFieldWhenPostThenReturnBadRequest() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).toString();
        RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("recipient", "erick@opelt.dev")
                        .put("body", "Hello")
                        .put("channel", "EMAIL")
                        .toString())
                .when()
                .post("/v1/messages")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenAMessageWithPastScheduleWhenScheduleThenReturnBadRequest() throws Exception {
        var schedule = LocalDateTime.now().minusMinutes(5).toString();
        RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", schedule)
                        .put("recipient", "erick@opelt.dev")
                        .put("body", "Hello")
                        .put("channel", "EMAIL")
                        .toString())
                .when()
                .post("/v1/messages")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(String.format("Schedule field schedule=%s must have a future date-time", schedule)));
    }

    @Test
    void givenAMessageIdWhenGetThenReturnResponseWithValidSchema() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).toString();
        var id = createMessage(schedule);

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
    void givenAMessageIdWhenGetThenReturnBodyWithLinks() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).toString();
        var id = createMessage(schedule);

        RestAssured
                .given()
                .pathParam("id", id)
                .when()
                .get("/v1/messages/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("schedule", notNullValue())
                .body("recipient", equalTo("erick@opelt.dev"))
                .body("channel", equalTo("EMAIL"))
                .body("body", equalTo("Hello"))
                .body("status", equalTo("SCHEDULED"))
                .body("_links.self.href", matchMessageResourceURIWithId(id));
    }

    @Test
    void givenAnInvalidIdWhenGetThenReturnNotFound() {
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

    @Test
    void givenAScheduledMessageWhenDeleteThenReturnNoContent() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).toString();
        var id = createMessage(schedule);

        RestAssured
                .given()
                .pathParam("id", id)
                .when()
                .delete("/v1/messages/{id}")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenASentMessageWhenDeleteThenReturnBadRequest() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).toString();
        var id = createMessage(schedule);
        sendMessage(id);

        RestAssured
                .given()
                .pathParam("id", id)
                .when()
                .delete("/v1/messages/{id}")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenAnInvalidIdWhenCancelThenReturnNotFound() throws Exception {
        var id = "invalid-id";

        RestAssured
                .given()
                .pathParam("id", id)
                .when()
                .delete("/v1/messages/{id}")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private Matcher<String> matchMessageResourceURI() {
        var baseURI = String.format("http://localhost:%d/v1/messages/", port);
        return matchesRegex(baseURI + UUID_REGEX);
    }

    private Matcher<String> matchMessageResourceURIWithId(String id) {
        var baseURI = String.format("http://localhost:%d/v1/messages/", port);
        return matchesRegex(baseURI + id);
    }

    private String createMessage(String schedule) throws JSONException {
        return RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", schedule)
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

    private void sendMessage(String id) {
        jdbcTemplate.update("UPDATE MESSAGE SET status = 'SENT' WHERE ID=?", id);
    }

}