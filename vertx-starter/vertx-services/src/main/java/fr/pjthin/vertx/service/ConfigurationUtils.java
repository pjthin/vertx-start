package fr.pjthin.vertx.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.io.File;

import org.apache.commons.io.FileUtils;

/**
 * Util class for configuration.
 * 
 * @author Pidji
 */
public abstract class ConfigurationUtils {

    public static final String MONGODB = "mongodb-conf";
    public static final String MONGODB_PATH = "/mongodb.json";
    public static final String CLUSTER = "cluster-conf";
    public static final String CLUSTER_PATH = "/clustered-vertx.json";

    /**
     * Combine :
     * <ul>
     * <li> {@link ConfigurationUtils#loadJsonFromClassPath(String)}
     * <li> {@link ConfigurationUtils#putConfiguration(Vertx, String, JsonObject)}
     * </ul>
     * 
     * @param vertx
     * @param classPath
     * @param key
     */
    public static void loadJsonFromClassPathAndPutConfiguration(Vertx vertx, String classPath, String key) {
        putConfiguration(vertx, key, loadJsonFromClassPath(classPath));
    }

    /**
     * Loading a file in classpath containing a json.
     * 
     * @param classPath
     * @return the json representation of the file
     */
    public static JsonObject loadJsonFromClassPath(String classPath) {
        try {
            return new JsonObject(FileUtils.readFileToString(new File(ConfigurationUtils.class.getResource(classPath)
                    .toURI()), "UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(String.format("Fail loading configuration %s: %s", classPath, e.getMessage()), e);
        }
    }

    /**
     * Put in Vertx sharedData the (key, configuration) -> (String, JsonObject).
     * 
     * @param vertx
     * @param key
     * @param configuration
     */
    public static void putConfiguration(Vertx vertx, String key, JsonObject configuration) {
        vertx.sharedData().getLocalMap(key).put(key, configuration);
    }

    /**
     * Get from Vertx sharedData the JsonObject associated to key.
     * 
     * @param vertx
     * @param key
     * @return
     * @throws IllegalArgumentException
     *             if Object associated to key is not JsonObject
     */
    public static JsonObject getConfiguration(Vertx vertx, String key) {
        Object result = vertx.sharedData().getLocalMap(key).get(key);
        if (result instanceof JsonObject) {
            return (JsonObject) result;
        }
        throw new IllegalArgumentException(String.format("Configuration[%s] is not an instance of JsonObject"));
    }

}
