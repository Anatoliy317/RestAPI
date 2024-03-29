package tests;


import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


public class RestApiTests extends TestBase {

    @Test
    @DisplayName("Просмотр пользователей c первой страницы")
    void listUser() {
        Response response = given()
                .log().uri()
                .log().method()
                .when()
                .get("users?page=1")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/list-user-schema.json"))
                .extract().response();

        assertThat(response.path("total"), is(12));
        assertThat(response.path("data[0].id"), is(1));
        assertThat(response.path("data[0].first_name"), is("George"));
        assertThat(response.path("data[0].last_name"), is("Bluth"));
        assertThat(response.path("data[0].email"), is("george.bluth@reqres.in"));
        assertThat(response.path("data[0].avatar"), is("https://reqres.in/img/faces/1-image.jpg"));

    }
    @Test
    @DisplayName("Просмотр данных пользователя №1")
    void singleUser() {
        Response response = given()
                .log().uri()
                .log().method()
                .when()
                .get("/users/1")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/single-user-schema.json"))
                .extract().response();

        assertThat(response.path("data.id"), is(1));
        assertThat(response.path("data.email"), is("george.bluth@reqres.in"));
        assertThat(response.path("data.first_name"), is("George"));
        assertThat(response.path("data.last_name"), is("Bluth"));
        assertThat(response.path("data.avatar"), is("https://reqres.in/img/faces/1-image.jpg"));
    }

    @Test
    @DisplayName("Создание нового пользователя")
    public void create() {
        String body = "{ \"name\": \"Anatoliy\", \"job\": \"QA\" }";

        Response response = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body(body)
                .when()
                .post("users")
                .then()
                .log().status()
                .log().body(true)
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/create-schema.json"))
                .extract().response();

        assertThat(response.path("name"), equalTo("Anatoliy"));
        assertThat(response.path("job"), equalTo("QA"));
    }

    @Test
    @DisplayName("Обнавление пользователя")
    public void update() {
        String body = "{ \"name\": \"Nik\", \"job\": \"QA\" }";

        Response response = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body(body)
                .when()
                .put("users/2")
                .then()
                .log().status()
                .log().body(true)
                .statusCode(200)
                .extract().response();

        assertThat(response.path("name"), equalTo("Nik"));
        assertThat(response.path("job"), equalTo("QA"));
    }

    @Test
    @DisplayName("Успешная регистрация")
    public void registerSuccessful() {
        String body = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\" }";

        Response response = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body(body)
                .when()
                .post("register")
                .then()
                .log().status()
                .log().body(true)
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/register-successful-schema.json"))
                .extract().response();

        assertThat(response.path("id"), equalTo(4));
        assertThat(response.path("token"), equalTo("QpwL5tke4Pnpja7X4"));
    }
    @Test
    @DisplayName("Успешная авторизация")
    public void loginSuccessful() {
        String body = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }";

        Response response = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body(body)
                .when()
                .post("register")
                .then()
                .log().status()
                .log().body(true)
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/register-successful-schema.json"))
                .extract().response();

        assertThat(response.path("token"), equalTo("QpwL5tke4Pnpja7X4"));
    }
    @Test
    @DisplayName("Неуспешная авторизация")
    public void loginUnSuccessful() {
        String body = "{ \"email\": \"peter@klaven\"}";

        Response response = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(JSON)
                .body(body)
                .when()
                .post("register")
                .then()
                .log().status()
                .log().body(true)
                .statusCode(400)
                .extract().response();

        assertThat(response.path("error"), equalTo("Missing password"));
    }
}
