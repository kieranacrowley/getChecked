import io.qameta.allure.Allure;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonAPITest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        RestAssured.filters(new AllureRestAssured()); // Attach requests and responses to Allure report
    }

    @Test
    public void testGetPost() {
        Response response = given()
                .when()
                .get("/posts/1")
                .then()
                .extract().response();
        // Log request and response
        logToAllure("GET /posts/1", response);

        // Validate response
        assertEquals(200, response.getStatusCode(), "Status code should be 200");
        assertEquals(1, response.jsonPath().getInt("id"), "Post ID should be 1");
        Assertions.assertNotNull(response.jsonPath().getString("title"), "Title should not be null");
        Assertions.assertNotNull(response.jsonPath().getString("body"), "Body should not be null");
    }

    @Test
    public void testCreatePost() {
        Map<String, Object> newPost = new HashMap<>();
        newPost.put("userId", 1);
        newPost.put("title", "New Post Title");
        newPost.put("body", "New Post Body");

        Response response = given()
                .contentType("application/json")
                .body(newPost)
                .when()
                .post("/posts")
                .then()
                .extract().response();

        // Log request and response
        logToAllure("POST /posts", response);

        // Validate response
        assertEquals(201, response.getStatusCode(), "Status code should be 201");
        assertEquals(newPost.get("title"), response.jsonPath().getString("title"), "Title mismatch");
        assertEquals(newPost.get("body"), response.jsonPath().getString("body"), "Body mismatch");
    }

    @Test
    public void testPutRequest() {
        Map<String, Object> updatedPost = new HashMap<>();
        updatedPost.put("userId", 1);
        updatedPost.put("title", "Updated Title");
        updatedPost.put("body", "Updated Body");

        Response response = given()
                .contentType("application/json")
                .body(updatedPost)
                .when()
                .put("/posts/1")
                .then()
                .extract().response();

        // Log request and response
        logToAllure("PUT /posts/1", response);

        // Validate response
        assertEquals(200, response.getStatusCode(), "Status code should be 200");
        assertEquals(updatedPost.get("title"), response.jsonPath().getString("title"), "Title mismatch");
        assertEquals(updatedPost.get("body"), response.jsonPath().getString("body"), "Body mismatch");
    }

    @Test
    public void testDeletePost() {
        Response response = given()
                .when()
                .delete("/posts/1")
                .then()
                .extract().response();

        // Log request and response
        logToAllure("DELETE /posts/1", response);

        // Validate response
        Assertions.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 204, "Invalid status code");
        System.out.println("!!!!!!response.asString()"+response.asString());
        assertEquals(response.asString(),"{}", "Response body should be empty");
    }

    private void logToAllure(String requestDescription, Response response) {
        Allure.addAttachment(requestDescription + " - Request", response.getBody().toString());
        Allure.addAttachment(requestDescription + " - Response", response.asString());
    }
}
