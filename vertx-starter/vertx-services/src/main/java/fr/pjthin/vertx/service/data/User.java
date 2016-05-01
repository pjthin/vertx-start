package fr.pjthin.vertx.service.data;

import fr.pjthin.vertx.service.constante.Gender;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;

/**
 * User POJO.
 * <p>
 * <b>WARNING!</b> if you modify this class you have to delete manually and re-generate {@link UserConverter} (mvn
 * compile) because the processor would not re-generate it if class already exists.
 * 
 * @author Pidji
 */
@DataObject(generateConverter = true, inheritConverter = true)
public class User {

    public static final JsonObject ALL = new JsonObject();
    private int id;
    private String login;
    private String cryptedPasswd;
    private Gender gender;

    public User() {
    }

    public User(User other) {
        this.id = other.id;
        this.login = other.login;
        this.cryptedPasswd = other.cryptedPasswd;
        this.gender = other.gender;
    }

    public User(JsonObject json) {
        UserConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        UserConverter.toJson(this, json);
        return json;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getCryptedPasswd() {
        return cryptedPasswd;
    }

    @Fluent
    public User setCryptedPasswd(String cryptedPasswd) {
        this.cryptedPasswd = cryptedPasswd;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    @Fluent
    public User setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

}
