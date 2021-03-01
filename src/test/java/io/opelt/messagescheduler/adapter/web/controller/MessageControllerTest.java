package io.opelt.messagescheduler.adapter.web.controller;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import org.springframework.test.jdbc.JdbcTestUtils;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageControllerTest {

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private static final DateTimeFormatter ISO_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");

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
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
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
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
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
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
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
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
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
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
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
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
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
        var schedule = LocalDateTime.now().minusMinutes(5).format(ISO_DATE_TIME_FORMAT);
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
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
        var id = createMessage(schedule, "EMAIL");

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
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
        var id = createMessage(schedule, "EMAIL");

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
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void givenAScheduledMessageWhenDeleteThenReturnNoContent() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
        var id = createMessage(schedule, "EMAIL");

        RestAssured
                .given()
                .pathParam("id", id)
                .when()
                .delete("/v1/messages/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenASentMessageWhenDeleteThenReturnBadRequest() throws Exception {
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
        var id = createMessage(schedule, "EMAIL");
        sendMessage(id);

        RestAssured
                .given()
                .pathParam("id", id)
                .when()
                .delete("/v1/messages/{id}")
                .then()
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
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void givenTwoMessagesWhenGetFirstPageThenReturnPage() throws JSONException {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "MESSAGE");
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
        var id = createMessage(schedule, "EMAIL");

        RestAssured
                .given()
                .queryParam("page", 0)
                .queryParam("size", 1)
                .when()
                .get("/v1/messages")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("_embedded.messages", hasSize(1))
                .body("_embedded.messages[0].id", equalTo(id))
                .body("_embedded.messages[0].schedule", notNullValue())
                .body("_embedded.messages[0].recipient", equalTo("erick@opelt.dev"))
                .body("_embedded.messages[0].channel", equalTo("EMAIL"))
                .body("_embedded.messages[0].body", equalTo("Hello"))
                .body("_embedded.messages[0].status", equalTo("SCHEDULED"))
                .body("_embedded.messages[0]._links.self.href", matchMessageResourceURIWithId(id))
                .body("page.size", equalTo(1))
                .body("page.totalElements", equalTo(1))
                .body("page.totalPages", equalTo(1))
                .body("page.number", equalTo(0))
                .body("_links.self.href", equalTo(String.format("http://localhost:%s/v1/messages?page=0&size=1", port)));
    }

    @Test
    void givenTwoMessagesWhenGetPageWithChannelFilterThenReturnPage() throws JSONException {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "MESSAGE");
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
        createMessage(schedule, "EMAIL");
        var smsId = createMessage(schedule, "SMS");

        RestAssured
                .given()
                .queryParam("page", 0)
                .queryParam("size", 20)
                .queryParam("channel", "SMS")
                .when()
                .get("/v1/messages")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("_embedded.messages", hasSize(1))
                .body("_embedded.messages[0].id", equalTo(smsId))
                .body("_embedded.messages[0].schedule", notNullValue())
                .body("_embedded.messages[0].recipient", equalTo("erick@opelt.dev"))
                .body("_embedded.messages[0].channel", equalTo("SMS"))
                .body("_embedded.messages[0].body", equalTo("Hello"))
                .body("_embedded.messages[0].status", equalTo("SCHEDULED"))
                .body("_embedded.messages[0]._links.self.href", matchMessageResourceURIWithId(smsId))
                .body("page.size", equalTo(20))
                .body("page.totalElements", equalTo(1))
                .body("page.totalPages", equalTo(1))
                .body("page.number", equalTo(0))
                .body("_links.self.href", equalTo(String.format("http://localhost:%s/v1/messages?channel=SMS&page=0&size=20", port)));
    }

    @Test
    void givenTwoMessagesWhenGetPageWithStatusFilterThenReturnPage() throws JSONException {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "MESSAGE");
        var schedule = LocalDateTime.now().plusMinutes(5).format(ISO_DATE_TIME_FORMAT);
        createMessage(schedule, "EMAIL");
        var sentId = createMessage(schedule, "EMAIL");
        sendMessage(sentId);

        RestAssured
                .given()
                .queryParam("page", 0)
                .queryParam("size", 20)
                .queryParam("status", "SENT")
                .when()
                .get("/v1/messages")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("_embedded.messages", hasSize(1))
                .body("_embedded.messages[0].id", equalTo(sentId))
                .body("_embedded.messages[0].schedule", notNullValue())
                .body("_embedded.messages[0].recipient", equalTo("erick@opelt.dev"))
                .body("_embedded.messages[0].channel", equalTo("EMAIL"))
                .body("_embedded.messages[0].body", equalTo("Hello"))
                .body("_embedded.messages[0].status", equalTo("SENT"))
                .body("_embedded.messages[0]._links.self.href", matchMessageResourceURIWithId(sentId))
                .body("page.size", equalTo(20))
                .body("page.totalElements", equalTo(1))
                .body("page.totalPages", equalTo(1))
                .body("page.number", equalTo(0))
                .body("_links.self.href", equalTo(String.format("http://localhost:%s/v1/messages?status=SENT&page=0&size=20", port)));
    }

    private Matcher<String> matchMessageResourceURI() {
        var baseURI = String.format("http://localhost:%d/v1/messages/", port);
        return matchesRegex(baseURI + UUID_REGEX);
    }

    private Matcher<String> matchMessageResourceURIWithId(String id) {
        var baseURI = String.format("http://localhost:%d/v1/messages/", port);
        return matchesRegex(baseURI + id);
    }

    private String createMessage(String schedule, String email) throws JSONException {
        return RestAssured
                .given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(new JSONObject()
                        .put("schedule", schedule)
                        .put("recipient", "erick@opelt.dev")
                        .put("body", "Hello")
                        .put("channel", email)
                        .toString())
                .when()
                .post("/v1/messages")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath()
                .getString("id");
    }

    private void sendMessage(String id) {
        jdbcTemplate.update("UPDATE MESSAGE SET status = 'SENT' WHERE ID=?", id);
    }

}