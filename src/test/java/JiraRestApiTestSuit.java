import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import static io.restassured.RestAssured.given;

public class JiraRestApiTestSuit {
    String issueIdOrKey = "";
    String BASE_URL = "https://jira-auto.codecool.metastage.net";
    String SERVICE_URL_ISSUE = "/rest/api/latest/issue/";


    @BeforeEach
    void setUpTesting() {
        System.out.println("New test case starts:");
    }


    @AfterEach
    void tearDown() {
        if (issueIdOrKey != null) {
            try {
                given().
                        auth().preemptive().basic(TestingHelper.getProperty("username"), TestingHelper.getProperty("password")).
                        when().
                        request("DELETE", BASE_URL + SERVICE_URL_ISSUE + issueIdOrKey).
                        then().
                        statusCode(204);
//                    log().all();
                System.out.println("Deleted Issue: " + issueIdOrKey + "\n");
            } catch (Exception e) {
                System.out.println("Error: deleting of " + issueIdOrKey + "issue was not successful" + e);
            } finally {
                issueIdOrKey = null;
            }
        }
    }


    @Test
    void createIssue_withValidInputs() throws IOException {

        ValidatableResponse validatableResponse = given().
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
                request("POST", BASE_URL + SERVICE_URL_ISSUE).then();

        validatableResponse.statusCode(201);
        setIssueIdOrKey(validatableResponse.using().extract().response().getBody().as(Pojo.class));
    }


    @Test
    void getAnIssueByKey_withValidInputs() throws IOException {
//        Creating preconditions:
        String testSummaryText = "API test instance issue";
        Response response = createTestIssueAsPrecondition(testSummaryText);
        setIssueIdOrKey(response.getBody().as(Pojo.class));

//        The test case starts from here
        Response testResponse = given().
                auth().preemptive().basic(TestingHelper.getProperty("username"), TestingHelper.getProperty("password")).
                header("Content-Type", "application/json").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                when().
                request("GET", BASE_URL + SERVICE_URL_ISSUE + issueIdOrKey).
                then().
                using().extract().response();

        assertEquals(testSummaryText, testResponse.getBody().jsonPath().get("fields.summary"));
    }


    @Test
    void createACommentToAnExistingIssue_withValidInputs() throws IOException {
        //        Creating preconditions:
        String testSummaryText = "API test instance issue to test commenting";
        String testCommentText = "Shining comment for testing.";
        Response response = createTestIssueAsPrecondition(testSummaryText);
        setIssueIdOrKey(response.getBody().as(Pojo.class));

//        The test case starts from here
        Response testResponse = given().
                auth().preemptive().basic(TestingHelper.getProperty("username"), TestingHelper.getProperty("password")).
                header("Content-Type", "application/json").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body("{\n" +
                        "    \"body\": \"" + testCommentText + "\"\n" +
                        "}").
                when().
                request("POST", BASE_URL + SERVICE_URL_ISSUE + issueIdOrKey + "/comment").
                then().
                using().extract().response();

        assertEquals(testCommentText, testResponse.getBody().jsonPath().get("body"));
    }


    @Test
    void updateAnExistingComment_withValidInputs() throws IOException {
        //        Creating preconditions:
        String testSummaryText = "API test instance issue to test commenting";
        String testCommentText = "Shining comment for testing.";
        String testUpdatedCommentText = "Modified comment for testing of Jira";
        Response response = createTestIssueAsPrecondition(testSummaryText);
        setIssueIdOrKey(response.getBody().as(Pojo.class));

        given().
                auth().preemptive().basic(TestingHelper.getProperty("username"), TestingHelper.getProperty("password")).
                header("Content-Type", "application/json").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body("{\n" +
                        "    \"body\": \"" + testCommentText + "\"\n" +
                        "}").
                when().
                request("POST", BASE_URL + SERVICE_URL_ISSUE + issueIdOrKey + "/comment");

        //        The test case starts from here
        Response testResponse = given().
                auth().preemptive().basic(TestingHelper.getProperty("username"), TestingHelper.getProperty("password")).
                header("Content-Type", "application/json").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body("{     \"body\": \"" + testUpdatedCommentText + "\" }").
                when().
                request("POST", BASE_URL + SERVICE_URL_ISSUE + issueIdOrKey + "/comment").
                then().
                using().extract().response();

        assertEquals(testUpdatedCommentText, testResponse.getBody().jsonPath().get("body"));
    }


    @Test
    void deleteAnExistingComment_withValidInputs() throws IOException {
        //        Creating preconditions:
        String testSummaryText = "API test instance issue to test commenting";
        String testCommentText = "Shining comment for testing.";
        String testCommentId = "";

        Response response = createTestIssueAsPrecondition(testSummaryText);
        setIssueIdOrKey(response.getBody().as(Pojo.class));

        Response testCommentResponse = given().
                auth().preemptive().basic(TestingHelper.getProperty("username"), TestingHelper.getProperty("password")).
                header("Content-Type", "application/json").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body("{\n" +
                        "    \"body\": \"" + testCommentText + "\"\n" +
                        "}").
                when().
                request("POST", BASE_URL + SERVICE_URL_ISSUE + issueIdOrKey + "/comment").
                then().
                using().extract().response();

        testCommentId = testCommentResponse.getBody().jsonPath().get("id");

        //        The test case starts from here
        given().
                auth().preemptive().basic(TestingHelper.getProperty("username"), TestingHelper.getProperty("password")).
                header("Content-Type", "application/json").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                when().
                request("DELETE", BASE_URL + SERVICE_URL_ISSUE + issueIdOrKey + "/comment/" + testCommentId).
                then().statusCode(204);
    }


    private Response createTestIssueAsPrecondition(String testSummaryText) throws IOException {
        return given().
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
    }


    private void setIssueIdOrKey(Pojo pojo) {
        if (!pojo.getKey().equals("") || pojo.getKey() != null) {
            issueIdOrKey = pojo.getKey();
        }
        System.out.println("Created Issue: " + pojo.getKey());
    }
}
