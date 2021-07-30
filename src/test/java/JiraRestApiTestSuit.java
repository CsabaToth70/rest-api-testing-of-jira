import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import static io.restassured.RestAssured.given;


public class JiraRestApiTestSuit {
    String BASE_URL = "https://jira-auto.codecool.metastage.net";
    String SERVICE_URL_ISSUE = "/rest/api/latest/issue/";
    String createdIssueId;


    @BeforeEach
    void setUpTesting() {
        System.out.println("Tests start\n");
    }


    @AfterEach
    void tearDown() {
        if (createdIssueId != null) {
            try {
                given().
                        auth().preemptive().basic(TestingHelper.getProperty("username"), TestingHelper.getProperty("password")).
                        when().
                        request("DELETE", BASE_URL + SERVICE_URL_ISSUE + createdIssueId).
                        then().
                        statusCode(204);
//                    log().all();
                System.out.println("Deleted Issue: " + createdIssueId);
            } catch (Exception e) {
                System.out.println("Error: deleting of " + createdIssueId + "issue was not successful" + e);
            } finally {
                createdIssueId = null;
            }
        }
    }

    @Test
    void CreateIssue_withValidInputs() throws IOException {

        Response response = given().
                auth().preemptive().basic(TestingHelper.getProperty("username"), TestingHelper.getProperty("password")).
                header("Content-Type", "application/json").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body("{\n" +
                        "  \"fields\": {\n" +
                        "    \"project\":\n" +
                        "    {\n" +
                        "      \"key\": \"MTP\"\n" +
                        "    },\n" +
                        "    \"summary\": \"API create issue from java\",\n" +
                        "    \"issuetype\": {\n" +
                        "      \"name\": \"Bug\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}").
                when().
                request("POST", BASE_URL + SERVICE_URL_ISSUE).
                then().
                using().extract().response();

        Pojo pojo = response.getBody().as(Pojo.class);

        if (!pojo.getKey().equals("") || pojo.getKey() != null) {
            createdIssueId = pojo.getKey();
        }
        System.out.println("Created Issue: " + pojo.getKey());
    }

    @Test
    void GetAnIssueByKey_withValidInputs() throws IOException {
//        Creating preconditions:
        String testSummaryText = "API test instance issue";
        Response response = given().
                auth().preemptive().basic(TestingHelper.getProperty("username"), TestingHelper.getProperty("password")).
                header("Content-Type", "application/json").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body("{\n" +
                        "  \"fields\": {\n" +
                        "    \"project\":\n" +
                        "    {\n" +
                        "      \"key\": \"MTP\"\n" +
                        "    },\n" +
                        "    \"summary\": \"" + testSummaryText + "\",\n" +
                        "    \"issuetype\": {\n" +
                        "      \"name\": \"Bug\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}").
                when().
                request("POST", BASE_URL + SERVICE_URL_ISSUE).
                then().
                using().extract().response();


        Pojo pojo = response.getBody().as(Pojo.class);

        if (!pojo.getKey().equals("") || pojo.getKey() != null) {
            createdIssueId = pojo.getKey();
        }
        System.out.println("Created Issue for testing: " + pojo.getKey());

//        The test case starts from here
        Response testResponse = given().
                auth().preemptive().basic(TestingHelper.getProperty("username"), TestingHelper.getProperty("password")).
                header("Content-Type", "application/json").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                when().
                request("GET", BASE_URL + SERVICE_URL_ISSUE + createdIssueId).
                then().
                using().extract().response();

        assertEquals(testSummaryText, testResponse.getBody().jsonPath().get("fields.summary"));

    }


}
