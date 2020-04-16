//package pt.teamloan.test;
//
//import static io.restassured.RestAssured.given;
//
//import org.junit.jupiter.api.Test;
//
//import io.quarkus.test.common.QuarkusTestResource;
//import io.quarkus.test.junit.QuarkusTest;
//import io.restassured.http.ContentType;
//import pt.teamloan.test.resources.JwtPropertiesResource;
//import pt.teamloan.test.resources.PostgreSQLTestResource;
//import pt.teamloan.test.utils.TokenUtils;
//
////@QuarkusTest
////@QuarkusTestResource(JwtPropertiesResource.class)
////@QuarkusTestResource(PostgreSQLTestResource.class)
//public class CompanyPostingsResourceTest {
//
//    @Test
//    public void testUpdatePostingEndpoint() throws Exception {
//    	String authorizationHeader = "Bearer " + TokenUtils.generateTokenString("/jwt/JwtClaims.json", null); 
//        given()
//          .when().header("Authorization", authorizationHeader).contentType(ContentType.JSON).body("{\"title\":\"Updated Title!\"}").patch("/api/company/a2d1c7bb-a2f7-4273-8453-03c0599be24a/postings/e8985196-4544-4afd-84ac-d8b72dd6dfb0")
//          .then()
//             .statusCode(202);
//    }
//}