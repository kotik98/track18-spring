package ru.track;

/**
 * TASK:
 * POST request to  https://guarded-mesa-31536.herokuapp.com/track
 * fields: name,github,email
 *
 * LIB: http://unirest.io/java.html
 *
 *
 */
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

public class App {

    public static final String URL = "http://guarded-mesa-31536.herokuapp.com/track";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_GITHUB = "github";
    public static final String FIELD_EMAIL = "email";

    public static void main(String[] args) throws Exception {
        // 1) Use Unirest.post()
        // 2) Get response .asJson()
        HttpResponse<JsonNode> response = Unirest.post(URL)
                .field(FIELD_NAME, "Bogdan")
                .field(FIELD_GITHUB, "kotik98")
                .field(FIELD_EMAIL, "zaharin.98@mail.ru")
                .field("success", false)
                .asJson();
        // 3) Get json body and JsonObject
        JSONObject jsonObject = response.getBody().getObject();
        // 4) Get field "success" from JsonObject
        boolean success = jsonObject.getBoolean("success");
        System.out.println(success);
    }
}
