package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import growthbook.sdk.java.Feature;
import growthbook.sdk.java.GBContext;
import growthbook.sdk.java.GrowthBook;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException, ExecutionException {
        JsonObject features = getFeatureJson();

//        for (String key : features.keySet()) {
//            JsonElement feature = features.get(key);
//            //String type = getType(feature.getAsJsonObject().get("defaultValue"));
//            //System.out.println(key + " " + type);
//        }

        runFeature(features);
    }

    private static JsonObject getFeatureJson() throws IOException, InterruptedException, URISyntaxException {
        String featuresEndpointEnv = System.getenv("FEATURES_ENDPOINT");
        if (featuresEndpointEnv == null || featuresEndpointEnv.isEmpty()) {
            throw new IllegalArgumentException("Environment variable FEATURES_ENDPOINT is not set");
        }
        URI featuresEndpoint = new URI(featuresEndpointEnv);

        HttpRequest request = HttpRequest.newBuilder().uri(featuresEndpoint).GET().build();
        HttpResponse<String> response = HttpClient.newBuilder().build()
                .send(request, HttpResponse.BodyHandlers.ofString());
        String featuresJson = new JSONObject(response.body()).get("features").toString();

        Gson gson = new Gson();

        return gson.fromJson(featuresJson, JsonObject.class);
    }
    private static void runFeature(JsonObject features) throws ExecutionException, InterruptedException {
        int enabled = 0;
        int disabled = 0;
        LocalDateTime start = LocalDateTime.now();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Boolean>> futures = new ArrayList<>();
        int numIterations = 1000_000;
        for (long i = 0; i < numIterations; i++) {
            futures.add(executor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    GBContext context = GBContext.builder()
                            .features(features)
                            .attributesJson(getUserAttributesObj().toString()) // Optional
                            .trackingCallback(null)
                            .build();
                    context.setFeatures(features);
                    GrowthBook growthBook = new GrowthBook(context);

                    return growthBook.isOn("test_boolean");
                }
            }));
        }

        for (Future<Boolean> future : futures) {
            if (future.get()) {
                enabled++;
            } else {
                disabled++;
            }
        }

        executor.shutdown();
        LocalDateTime end = LocalDateTime.now();
        int timeTakenInSeconds = (end.getHour() - start.getHour()) * 3600 +
                60 * (end.getMinute() - start.getMinute())
                + end.getSecond() - start.getSecond();
        System.out.println(end);
        System.out.println("Enabled: " + enabled + " Disabled: " + disabled + " TimeTaken: "
                + timeTakenInSeconds + " seconds");
    }

    private static JSONObject getUserAttributesObj() {
        JSONObject userAttributesObj = new JSONObject();
        userAttributesObj.put("id", UUID.randomUUID().toString());
        userAttributesObj.put("url", "foo");
        userAttributesObj.put("path", "foo");
        userAttributesObj.put("host", "foo");
        userAttributesObj.put("query", "foo");
        userAttributesObj.put("deviceType", "desktop");
        userAttributesObj.put("browser", "chrome");
        userAttributesObj.put("utmSource", "foo");
        userAttributesObj.put("utmMedium", "foo");
        userAttributesObj.put("utmCampaign", "foo");
        userAttributesObj.put("utmTerm", "foo");
        userAttributesObj.put("utmContent", "foo");
        userAttributesObj.put("os_id", 123);
        userAttributesObj.put("placement_format", "fsVideo");
        userAttributesObj.put("dsp_id", "foo");
        userAttributesObj.put("winner_primary_dsp_id", "foo");
        return userAttributesObj;
    }

    private static String getType(JsonElement element) {
        if (element.isJsonNull()) {
            return "null";
        } else if (element.isJsonPrimitive()) {
            if (element.getAsJsonPrimitive().isBoolean()) {
                return "boolean";
            } else if (element.getAsJsonPrimitive().isNumber()) {
                return "number";
            } else if (element.getAsJsonPrimitive().isString()) {
                return "string";
            }
        } else if (element.isJsonArray()) {
            return "array";
        } else if (element.isJsonObject()) {
            return "object";
        }
        return "unknown";
    }

}