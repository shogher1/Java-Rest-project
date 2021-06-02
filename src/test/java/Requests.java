import com.devskiller.jfairy.Fairy;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class Requests {
    static Map<String, Object> map = new HashMap<String, Object>();
    static Fairy fairy = Fairy.create();
    static Integer id;
    static MongoClient mongoClient = new MongoClient("localhost", 27017);
    static MongoDatabase database = mongoClient.getDatabase("test");
    static MongoCollection<Document> collection = database.getCollection("users_data");


    @Test
    public void GetRequest() {
        given().
                get("https://gorest.co.in/public-api/posts").
                then().
                statusCode(200).
                body("data.id[0]", equalTo(35)).
                body("data.user_id", hasItems(35, 61));
    }

    @Test
    public void PostRequest() {
        String name = fairy.person().getFirstName();
        String gender = Functions.gender();
        map.put("name", name);
        map.put("gender", gender);
        map.put("email", Functions.Email() + "@gmail.com");
        map.put("status", "Active");
        Gson gson = new Gson();
        String postJson = gson.toJson(map);
        Response res = given().
                header("Content-Type", "application/json").
                header("Authorization", Functions.AccessToken).
                contentType(ContentType.JSON).accept(ContentType.JSON).
                body(postJson).
                when().
                post("https://gorest.co.in/public-api/users").
                then().
                body("code", equalTo(201)).
                body("data.name", equalTo(name)).
                contentType(ContentType.JSON).extract().response();
        JsonObject user = gson.fromJson(res.getBody().asString(), JsonObject.class);
        JsonElement data = user.get("data");
        id = data.getAsJsonObject().get("id").getAsInt();
        Document doc = new Document("_id", new ObjectId());
        doc.append("user_id", id);
        collection.insertOne(doc);
    }

    @Test(priority = 1)
    public void PatchRequest() {
        String name = fairy.person().getFirstName();
        String gender = Functions.gender();
        Gson gson = new Gson();
        Document doc = collection.find().first();
        assert doc != null;
        String id = doc.get("user_id").toString();
        map.put("name", name);
        map.put("gender", gender);
        map.put("email", Functions.Email() + "@gmail.com");
        map.put("status", "Active");
        String patchJson = gson.toJson(map);
        given().
                header("Accept", "application/json").
                header("Content-Type", "application/json").
                header("Authorization", Functions.AccessToken).
                contentType(ContentType.JSON).accept(ContentType.JSON).
                body(patchJson).
                when().
                patch("https://gorest.co.in/public-api/users/" + id).
                then().
                body("code", equalTo(200)).
                body("data.name", equalTo(name));
    }

    @Test(priority = 1)
    public void PutRequest() {
        String name = fairy.person().getFirstName();
        String gender = Functions.gender();
        Gson gson = new Gson();
        Document doc = collection.find().first();
        assert doc != null;
        String id = doc.get("user_id").toString();
        String email = Functions.Email() + "@gmail.com";
        map.put("name", name);
        map.put("gender", gender);
        map.put("email", email);
        String putJson = gson.toJson(map);
        given().
                header("Accept", "application/json").
                header("Content-Type", "application/json").
                header("Authorization", Functions.AccessToken).
                contentType(ContentType.JSON).accept(ContentType.JSON).
                body(putJson).
                when().
                put("https://gorest.co.in/public-api/users/" + id).
                then().
                body("code", equalTo(200)).
                body("data.name", equalTo(name)).
                body("data.gender", equalTo(gender)).
                body("data.email", equalTo(email));
    }


    @Test(priority = 1)
    public void OptionsRequest() {
        Gson gson = new Gson();
        Document doc = collection.find().first();
        assert doc != null;
        String id = doc.get("user_id").toString();
        map.put("status", "Inactive");
        String optionJson = gson.toJson(map);
        given().
                header("Accept", "application/json").
                header("Content-Type", "application/json").
                header("Authorization", Functions.AccessToken).
                contentType(ContentType.JSON).accept(ContentType.JSON).
                body(optionJson).
                when().
                put("https://gorest.co.in/public-api/users/" + id).
                then().
                body("code", equalTo(200)).
                body("data.status", equalTo("Inactive"));
    }

    @Test(priority = 1)
    public void NegativePost1() {
        String name = fairy.person().getFirstName();
        String gender = Functions.gender();
        map.put("name", name);
        map.put("gender", gender);
        map.put("email", Functions.Email() + "@gmail.com");
        map.put("status", "Active");
        Gson gson = new Gson();
        String postJson = gson.toJson(map);
        given().
                header("Content-Type", "application/json").
                contentType(ContentType.JSON).accept(ContentType.JSON).
                body(postJson).
                when().
                post("https://gorest.co.in/public-api/users").
                then().
                body("code", equalTo(401));
    }

    @Test(priority = 1)
    public void NegativeDelete() {
        given().
                header("Accept", "application/json").
                header("Content-Type", "application/json").
                header("Authorization", Functions.AccessToken).
                contentType(ContentType.JSON).accept(ContentType.JSON).
                when().
                delete("https://gorest.co.in/public-api/users/0").
                then().
                body("code", equalTo(404));
    }

    @Test(priority = 1)
    public void NegativePost2() {
        given().
                header("Content-Type", "application/json").
                header("Authorization", Functions.AccessToken).
                contentType(ContentType.JSON).accept(ContentType.JSON).
                when().
                post("https://gorest.co.in/public-api/users").
                then().
                body("code", equalTo(422));
    }

    @Test(priority = 2)
    public void DeleteRequest() {
        Document doc = collection.find().first();
        assert doc != null;
        String id = doc.get("user_id").toString();
        given().
                header("Accept", "application/json").
                header("Content-Type", "application/json").
                header("Authorization", Functions.AccessToken).
                contentType(ContentType.JSON).accept(ContentType.JSON).
                when().
                delete("https://gorest.co.in/public-api/users/" + id).
                then().
                body("code", equalTo(204));
    }


    @AfterTest
    public static void cleanUp() {
        database.drop();
    }
}
