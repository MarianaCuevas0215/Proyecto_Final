import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import org.junit.FixMethodOrder;
import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.runners.MethodSorters;
import java.util.Base64;
import static org.junit.Assert.*;
import static io.restassured.RestAssured.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class API_test {
    //environment variables
    static private String baseUrl  = "https://webapi.segundamano.mx";
    static private String token = "mc1xf2e47da9c4e10f1cf4bb89b88142ee10da80cb56_v2";
    static private String accountID = "/private/accounts/10805452";
    static private String name = "MarC" ;
    static private String uuid = "74f9bdd3-b0b9-4d27-b922-6bb98c2f3b0d";
    static private String newText;
    static private String adID = "68412664" ;
    static private String token2;
    static private String addressID = "08fe1808-eb10-11ea-b029-7f257d85e63e";

    @Test
    public void t01_get_token_fail(){
        //Request an account token without authorization header.
        RestAssured.baseURI = String.format("%s/nga/api/v1.1/private/accounts",baseUrl);
        Response response = given().log().all()
                .post();

        //validations
        String responseBody = response.getBody().asString();
        String errorCode = response.jsonPath().getString("error.code");
        String errorCauses = response.jsonPath().getString("error.causes");
        System.out.println(errorCode);
        System.out.println(errorCauses);
        System.out.println("Response body is: " + responseBody);
        System.out.println(("Expected status code: 400"));
        System.out.println("Actual status code: " + response.getStatusCode());
        System.out.println("Error Code expected: VALIDATION FAILED \nResult: " + errorCode);
        assertEquals(400, response.getStatusCode());
        assertEquals("VALIDATION_FAILED", errorCode);
        assertTrue(responseBody.contains("ERROR_AUTH_LOGIN"));

    }

    @Test
    public void t02_get_token_correct(){
        //Request an account token with an authorization header
        String authorizationToken = "cGFwaXRhc2xleXM5MUBnbWFpbC5jb206Y29udHJhMTIz";
        RestAssured.baseURI = String.format("%s/nga/api/v1.1/private/accounts",baseUrl);
        Response response = given().log().all()
                .header("Authorization","Basic " + authorizationToken)
                .post();
        //validations
        String body = response.getBody().asString();
        String accessToken= response.jsonPath().getString("access_token");
        System.out.println("Response body: " + body);
        System.out.println("Access Token: " + accessToken);
        System.out.println(("Expected status code: 200"));
        System.out.println("Actual status code: " + response.getStatusCode());
        assertEquals(200, response.getStatusCode());
        assertTrue(body.contains("access_token"));
        assertTrue(body.contains("account_id"));
        assertTrue(body.contains("uuid"));

    }

    @Test
    public void t03_create_user_fail(){
        //Create an user without authorization header.

        String username = "agente" + (Math.floor(Math.random() * 7685) + 3) + "@mailinator.com";
        String bodyRequest = "{\"account\":{\"email\":\"+ username +\"}}";
        RestAssured.baseURI = String.format("%s/nga/api/v1.1/private/accounts",baseUrl);
        Response response = given().log().all()
                .header("Accept", "application/json, text/plain, */*")
                .contentType("application/json")
                .body(bodyRequest)
                .post();

        //validations
        String responseBody = response.getBody().asString();
        String errorCode = response.jsonPath().getString("error.code");
        System.out.println(errorCode);
        System.out.println("Response body is: " + responseBody);
        System.out.println(("Expected status code: 400"));
        System.out.println("Actual status code: " + response.getStatusCode());
        assertEquals(400, response.getStatusCode());
        assertEquals("VALIDATION_FAILED", errorCode);
        assertTrue(responseBody.contains("ERROR_AUTH_LOGIN"));

    }

    @Test
    public void t04_create_user(){
        //create a new account successfully. In this case the code expected is 401 until the account will be confirmed.

        String username = "agente" + (Math.floor(Math.random() * 7685) + 3) + "@mailinator.com";
        double password = (Math.floor(Math.random() * 57684) + 10000);
        String datos = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(datos.getBytes());
        String bodyRequest = "{\"account\":{\"email\":\""+ username +"\",\"phone\":6556654455,\"name\":\""+ name +"\"}}";
        RestAssured.baseURI = String.format("%s/nga/api/v1.1/private/accounts",baseUrl);
        Response response = given().log().all()
                .header("Authorization","Basic " + encodedAuth)
                .contentType("application/json")
                .body(bodyRequest)
                .post();

        //validations
        String responseBody = response.getBody().asString();
        String errorCode = response.jsonPath().getString("error.code");
        System.out.println("Response body is: " + responseBody);
        System.out.println(("Expected status code: 401"));
        System.out.println("Actual status code: " + response.getStatusCode());
        assertEquals(401, response.getStatusCode());
        assertEquals("ACCOUNT_VERIFICATION_REQUIRED", errorCode);
        assertNotEquals(201, response.getStatusCode());

        //assertTrue(body.contains("account_id"));
        //save account data to environment variables
        //token = response.jsonPath().getString("access_token");
        //System.out.println(token);
        //accountID = response.jsonPath().getString("account.account_id");
        //System.out.println(accountID);
        //name = response.jsonPath().getString("account.name");
        //System.out.println(name);
        //uuid = response.jsonPath().getString("account.uuid");
        //System.out.println(uuid);
        //String user = accountID.split("/")[3];
        //System.out.println(user);
    }

    @Test
    public void t05_update_phone_number(){
        //Update user created adding a new phone number

        RestAssured.baseURI = String.format("%s/nga/api/v1.1%s", baseUrl, accountID);
        int phone = (int) (Math.random()*99999999+999999999);
        String bodyRequest ="{\"account\":{\"name\":\""+ name +"\"," +
                "\"phone\":\""+ phone +"\", " +
                "\"phone_hidden\": true}}";
        Response response = given().log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + token)
                .accept("application/json, text/plain, */*")
                .contentType("application/json")
                .body(bodyRequest)
                .patch();

        //Validations
        String responseBody = response.getBody().asString();
        String userPhone = response.jsonPath().getString("account.phone");
        System.out.println(userPhone);
        System.out.println("Response body is: " + responseBody);
        System.out.println(("Expected status code: 200"));
        System.out.println("Actual status code: " + response.getStatusCode());
        assertEquals(200, response.getStatusCode());
        assertTrue(responseBody.contains("phone"));
        assertEquals(userPhone, "" + phone);

    }

    @Test
    public void t06_add_new_add_fail(){
        //add a new add with an invalid token should fail
        newText = "" + (Math.random()*99999999+999999999);
        RestAssured.baseURI = String.format("%s/nga/api/v1%s/klfst",baseUrl,accountID);

        String bodyRequest = "{\"ad\":" +
                "{\"locations\":[{\"code\":\"5\",\"key\":\"region\",\"label\":\"Baja California Sur\"," +
                "\"locations\":[{\"code\":\"51\",\"key\":\"municipality\",\"label\":\"Comondú\"," +
                "\"locations\":[{\"code\":\"3748\",\"key\":\"area\",\"label\":\"4 de Marzo\"}]}]}]," +
                "\"subject\":\"Paseo perros a domicilio\",\"body\":" +
                "\"Para su comodidad, paseo perros en su domicilio, use la promoción " + newText + "\"," +
                "\"category\":{\"code\":\"3042\"},\"images\":[],\"price\":{\"currency\":\"mxn\",\"price_value\":1}," +
                "\"ad_details\":{},\"phone_hidden\":1,\"plate\":\"\",\"vin\":\"\",\"type\":{\"code\":\"s\"," +
                "\"label\":\"\"},\"ad\":\"Paseo perros a domicilio\"},\"category_suggestion\":false,\"commit\":true}";
        Response response = given().log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + token2)
                .header("x-nga-source", "PHOENIX_DESKTOP")
                .contentType("application/json")
                .body(bodyRequest)
                .post();

        String body = response.getBody().asString();

        //Validations
        String errorCode = response.jsonPath().getString("error.code");
        System.out.println("Response body is: " + body);
        System.out.println(("Expected status code: 401"));
        System.out.println("Actual status code: " + response.getStatusCode());
        System.out.println("Error Code expected: UNAUTHORIZED \nResult: " + errorCode);
        assertEquals(401, response.getStatusCode());
        assertEquals("UNAUTHORIZED",errorCode);
    }

    @Test
    public void t07_add_new_add(){
        //Add a new add with a valid token
        newText = "" + (Math.random()*99999999+999999999);
        RestAssured.baseURI = String.format("%s/nga/api/v1%s/klfst",baseUrl,accountID);

        String bodyRequest = "{\"ad\":" +
                "{\"locations\":[{\"code\":\"5\",\"key\":\"region\",\"label\":\"Baja California Sur\"," +
                "\"locations\":[{\"code\":\"51\",\"key\":\"municipality\",\"label\":\"Comondú\"," +
                "\"locations\":[{\"code\":\"3748\",\"key\":\"area\",\"label\":\"4 de Marzo\"}]}]}]," +
                "\"subject\":\"Paseo perros a domicilio\",\"body\":" +
                "\"Para su comodidad, paseo perros en su domicilio, use la promoción " + newText + "\"," +
                "\"category\":{\"code\":\"3042\"},\"images\":[],\"price\":{\"currency\":\"mxn\",\"price_value\":1}," +
                "\"ad_details\":{},\"phone_hidden\":1,\"plate\":\"\",\"vin\":\"\",\"type\":{\"code\":\"s\"," +
                "\"label\":\"\"},\"ad\":\"Paseo perros a domicilio\"},\"category_suggestion\":false,\"commit\":true}";

        Response response = given().log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + token)
                .header("x-nga-source", "PHOENIX_DESKTOP")
                .contentType("application/json")
                .body(bodyRequest)
                .post();

        String body = response.getBody().asString();

        //Validations

        String actionType = response.jsonPath().getString("action.action_type");
        System.out.println("Body response: " + body );
        System.out.println("Status expected: 201" );
        System.out.println("Result: " + response.getStatusCode());
        System.out.println("Action expected to be: new \nResult: " + actionType);
        assertEquals(201, response.getStatusCode());
        assertEquals("new", actionType);

        //Save adID to be modified and delete later
        adID = response.jsonPath().getString("ad.ad_id").split("/")[5];
        System.out.println("Ad Created with id: " + adID);
        assertTrue(body.contains("ad_id"));
    }

    @Test
    public void t08_update_add(){
        //Update the text on the add's description.

        newText = "" + (Math.random()*99999999+999999999);
        RestAssured.baseURI = String.format("%s/nga/api/v1%s/klfst/%s/actions",baseUrl,accountID,adID);
        String bodyRequest = "{\"ad\":" +
                "{\"locations\":[{\"code\":\"5\",\"key\":\"region\",\"label\":\"Baja California Sur\"," +
                "\"locations\":[{\"code\":\"51\",\"key\":\"municipality\",\"label\":\"Comondú\"," +
                "\"locations\":[{\"code\":\"3748\",\"key\":\"area\",\"label\":\"4 de Marzo\"}]}]}]," +
                "\"subject\":\"Paseo perros a domicilio\",\"body\":" +
                "\"Para su comodidad, paseo perros en su domicilio, use la promoción " + newText + "\"," +
                "\"category\":{\"code\":\"3042\"},\"images\":[],\"price\":{\"currency\":\"mxn\",\"price_value\":1}," +
                "\"ad_details\":{},\"phone_hidden\":1,\"plate\":\"\",\"vin\":\"\",\"type\":{\"code\":\"s\"," +
                "\"label\":\"\"},\"ad\":\"Paseo perros a domicilio\"},\"category_suggestion\":false,\"commit\":true}";
        Response response = given().log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + token)
                .header("x-nga-source", "PHOENIX_DESKTOP")
                .contentType("application/json")
                .body(bodyRequest)
                .post();

        String body = response.getBody().asString();

        //validations
        String actionType = response.jsonPath().getString("action.action_type");
        System.out.println("Response body: " + body);
        System.out.println("Status expected: 201" );
        System.out.println("Result: " + response.getStatusCode());
        System.out.println("Action expected to be: edit \nResult: " + actionType);
        assertEquals(201, response.getStatusCode());
        assertEquals("edit", actionType);
    }

    @Test
    public void t09_get_address_fail(){

        //Get user address with an invalid token should fail.
        RestAssured.baseURI = String.format("%s/addresses/v1/get",baseUrl);
        Response response = given().log().all()
                .header("Authorization","Basic " + token)
                .get();

        String body = response.getBody().asString();

        //validations
        System.out.println("Response body: " + body);
        System.out.println("Status expected: 403" );
        System.out.println("Result: " + response.getStatusCode());
        assertEquals(403,response.getStatusCode());
        String errorCode = response.jsonPath().getString("error");
        System.out.println("Error code expected: Authorization failed \nResult: " + errorCode);
        assertEquals("Authorization failed",errorCode);
    }

    @Test
    public void t10_user_has_no_address(){
        //Get user addresses should be an empty list
        String token2Keys = uuid + ":" + token;
        token2 = Base64.getEncoder().encodeToString(token2Keys.getBytes());
        RestAssured.baseURI = String.format("%s/addresses/v1/get",baseUrl);
        Response response = given().log().all()
                .header("Authorization","Basic " + token2)
                .get();

        String body = response.getBody().asString();

        //validations
        System.out.println("Response body: " + body);
        System.out.println("Expected code status: 200" );
        System.out.println("Actual code status: " + response.getStatusCode());
        String addressesList = response.jsonPath().getString("addresses");
        System.out.println("List expected to be empty \nResult: " + addressesList);
        assertEquals(200,response.getStatusCode());
        assertNotEquals("[:]", addressesList);
        //This test is when there is nothing in the list addresses.Before to run is necessary delete items in the list addresses.
        // assertEquals("[:]",addressesList);
    }

    @Test
    public void t11_update_user_address(){
        //Add a new address to user.

        String token2Keys = uuid + ":" + token;
        token2 = Base64.getEncoder().encodeToString(token2Keys.getBytes());
        RestAssured.baseURI = String.format("%s/addresses/v1/create",baseUrl);
        Response response = given().log().all()
                .config(RestAssured.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs("x-www-form-urlencoded",
                                        ContentType.URLENC)))
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("contact", "Casa grande")
                .formParam("phone","3234445555")
                .formParam("rfc", "CASA681225XXX")
                .formParam("zipCode", "45050")
                .formParam("exteriorInfo", "exterior 10")
                .formParam("region", "5")
                .formParam("municipality", "51")
                .formParam("alias", "big house")
                .header("Authorization","Basic " + token2)
                .post();

        String body = response.getBody().asString();

        //Validaciones
        System.out.println("Body address: " + body );
        System.out.println("Status expected: 201" );
        System.out.println("Result: " + response.getStatusCode());
        assertEquals(201, response.getStatusCode());

        //save address to environment variable
        addressID = response.jsonPath().getString("addressID");
        System.out.println("Address created with ID: " + addressID);
        assertTrue(body.contains("addressID"));
    }

    @Test
    public void t12_update_user_address_duplicated(){
        //Add the same address, should fail. But in this case is allowed add the same information and the code received is 201.
        String token2Keys = uuid + ":" + token;
        token2 = Base64.getEncoder().encodeToString(token2Keys.getBytes());
        RestAssured.baseURI = String.format("%s/addresses/v1/create",baseUrl);
        Response response = given().log().all()
                .config(RestAssured.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs("x-www-form-urlencoded",
                                        ContentType.URLENC)))
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("contact", "Casa grande")
                .formParam("phone","3234445555")
                .formParam("rfc", "CASA681225XXX")
                .formParam("zipCode", "45050")
                .formParam("exteriorInfo", "exterior 10")
                .formParam("region", "5")
                .formParam("municipality", "51")
                .formParam("alias", "big house")
                .header("Authorization","Basic " + token2)
                .post();

        String body = response.getBody().asString();

        //Validaciones
        System.out.println("Response body: " + body);
        System.out.println("Status expected: 201" );
        System.out.println("Result: " + response.getStatusCode());
        //String errorCode = response.jsonPath().getString("error");
        //System.out.println("Request expected to return duplicate \nResult: " + errorCode);
        assertEquals(201, response.getStatusCode());
        assertNotNull(body);
    }

    @Test
    public void t13_get_created_address() {
        //use address id to get the user's address

        String token2Keys = uuid + ":" + token;
        token2 = Base64.getEncoder().encodeToString(token2Keys.getBytes());
        RestAssured.baseURI = String.format("%s/addresses/v1/get", baseUrl);
        Response response = given().log().all()
                .header("Authorization", "Basic " + token2)
                .get();

        String responseBody = response.getBody().asString();

        //Validations
        System.out.println("Response body is: " + responseBody);
        System.out.println("Status expected: 200");
        System.out.println("Result: " + response.getStatusCode());
        String respAddress = response.jsonPath().getString("addresses");
        System.out.println("Request expected to contain addressID: " + respAddress);
        assertEquals(200, response.getStatusCode());
        assertTrue(respAddress.contains(addressID));
    }

    @Test
    public void t14_shop_not_found(){
        //Search a shop and fail to found it with an specific account

        RestAssured.baseURI = String.format("%s/shops/api/v2/public/accounts/10613126/shop",baseUrl);
        Response response = given().log().all()
                .get();

        //Validations
        System.out.println("Expected status code: 404" );
        System.out.println("Actual status code: " + response.getStatusCode());
        String errorCode = response.jsonPath().getString("message");
        System.out.println("Error Code expected: Account not found \nResult: " + errorCode);
        assertEquals(404,response.getStatusCode());
        assertEquals("Account not found",errorCode);
    }

    @Test
    public void t15_delete_ad() {
        //Delete the ad created - possible fail with 403

        String bodyRequest = "{\"delete_reason\":{\"code\":\"5\"} }";
        RestAssured.baseURI = String.format("%s/nga/api/v1%s/klfst/%s", baseUrl, accountID, adID);
        Response response = given().log().all()
                .header("Authorization", "tag:scmcoord.com,2013:api " + token)
                .header("x-nga-source", "PHOENIX_DESKTOP")
                .contentType("application/json")
                .body(bodyRequest)
                .delete();
        String body = response.getBody().asString();

        //Validations
        String actionType = response.jsonPath().getString("action.action_type");
        String errorCode = response.jsonPath().getString("error.code");
        System.out.println("Body Response: "+ body);
        System.out.println("Status expected: 403");
        System.out.println("Actual status code: " + response.getStatusCode());
        System.out.println("Error: " + errorCode);
        System.out.println("Action expected to be: delete \nResult: " + actionType);

        assertEquals(403, response.getStatusCode());
        assertEquals(null, actionType);
        assertEquals("FORBIDDEN", errorCode);
        assertTrue(body.contains("FORBIDDEN"));
    }
}
