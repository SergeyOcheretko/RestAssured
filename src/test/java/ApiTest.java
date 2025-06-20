import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import io.qameta.allure.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("Restful Booker API")
@Feature("Управление бронированиями")
@Story("Создание и удаление брони")
@Owner("SergeyQA")
@Severity(SeverityLevel.CRITICAL)

public class ApiTest {

    private String token;
    private int bookingId;

    @BeforeAll

    public void init() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"admin\", \"password\": \"password123\"}")
                .post("/auth");

        token = response.path("token");

    }
    @BeforeEach
    public void setupAllureFilter() {
        RestAssured.filters(new AllureRestAssured());
    }
    @Test
    @Order(1)
    @Severity(SeverityLevel.BLOCKER)
    @Owner("SergeyQa")
    @DisplayName("Проверка /auth — получение токена")
    public void GetToken() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"admin\", \"password\": \"password123\"}")
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .log().body()
                .extract().response();

        token = response.path("token");
    }


    @Test
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Cоздание бронирования")

    public void createBooking() {
        Response response = given()
                .header("Cookie", "token=" + token)

                .contentType(ContentType.JSON)
                .body("{\"firstname\" : \"Jim\","
                        + "\"lastname\" : \"Brown\","
                        + "\"totalprice\" : 111,"
                        + "\"depositpaid\" : true,"
                        + "\"bookingdates\" : {"
                        + "\"checkin\" : \"2018-01-01\","
                        + "\"checkout\" : \"2019-01-01\"}"
                        + "}")
                .when()
                .post("booking");

        response.then()
                .statusCode(200)
                .log().body();
        bookingId = response.path("bookingid");
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Обновление бронирования")
    public void updateBooking() {

        given()
                .header("Cookie", "token=" + token)
                .contentType(ContentType.JSON)
                .body("{\"firstname\" : \"James\","
                        + "\"lastname\" : \"Brown\","
                        + "\"totalprice\" : 111,"
                        + "\"depositpaid\" : true,"
                        + "\"bookingdates\" : {"
                        + "\"checkin\" : \"2018-01-01\","
                        + "\"checkout\" : \"2019-01-01\"}"
                        + "}")
                .when()
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .log().body();

    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Частичное обновление бронирования")
    public void partialUpdateBooking() {
        given()
                .header("Cookie", "token=" + token)
                .contentType(ContentType.JSON)
                .body("{\"firstname\" : \"Jack\","
                        + "\"lastname\" : \"Bro\"}")
                .when()
                .patch("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .log().body();


    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Удаление записи о бронировании")
    public void deleteBooking() {
        given()
                .header("Cookie", "token=" + token)
                .contentType(ContentType.JSON)
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201)
                .log().body();
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Взятие всех записей о бронировании")
    public void GetBooking() {
        given()
                .header("Cookie", "token=" + token)
                .when()
                .get("/booking/")
                .then()
                .statusCode(200)
                .log().body();

    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Взятие всех айдишников бронирования")
    public void GetBookingIds() {
        given()
                .header("Cookie", "token=" + token)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .log().body();
    }

  @Test
@DisplayName("Проверка работоспособности API")
  @Severity(SeverityLevel.MINOR)
  public void healthCheck(){
        given()
                .header("Cookie", "token=" + token)
                .when()
                .get("https://restful-booker.herokuapp.com/ping\n")
                .then()
                .statusCode(201)
                .log().body();

  }

}
