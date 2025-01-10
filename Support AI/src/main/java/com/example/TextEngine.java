package com.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TextEngine {
    // for the API
    private static final String API_URL = "https://api-inference.huggingface.co/models/meta-llama/Llama-3.2-11B-Vision-Instruct";
    private static final String API_KEY = "hf_xxxxx"; // get your own api key from the
    // hugging face website :) (it's free)

    public static JPanel anonTextMessege(String message, int sender) {
        // 1 == AI
        // 2 == USER
        JPanel temp = new JPanel(new BorderLayout());

        temp.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextArea output = new JTextArea(message);
        output.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        if (sender == 1) {
            // to differeniate the text boxes
            output.setBackground(Color.LIGHT_GRAY);
            temp.add(output, BorderLayout.WEST);
        } else if (sender == 3) {// this is filler
            temp.setBackground(Color.decode("#F2F2F2"));
        } else {
            output.setBackground(Color.WHITE);
            temp.add(output, BorderLayout.EAST);
        }

        // make the text look good atleast with Swing...
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFocusable(false);

        return temp;
    }

    public static String aiOutput(String inputPrompt) throws IOException {
        String bestAnswer = null;

        OkHttpClient client = new OkHttpClient();
        String inputConstructor = "Answer the question clearly and concisely, don't list the possible answers but instead just give the final answer: ";

        JsonObject jsonInput = new JsonObject();
        jsonInput.addProperty("inputs", inputConstructor + inputPrompt);

        JsonObject parameters = new JsonObject();
        parameters.addProperty("max_tokens", 30); // Limit response length to approximately 30 words
        jsonInput.add("parameters", parameters);
        // the way this request should be built can be found in the huggingface ai
        // doc
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonInput.toString(), MediaType.parse("application/json")))
                .build();
        // System.out.println("Request: " + request);

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {

                String jsonResponse = response.body().string();
                System.out.println("Raw Response: " + jsonResponse);
                // gives multiple generated responses so it put them in an array
                JsonArray jsonArray = JsonParser.parseString(jsonResponse).getAsJsonArray();

                String[] generatedText = new String[jsonArray.size()];

                for (int i = 0; i < jsonArray.size(); i++) {
                    // adds reponse to array
                    JsonObject responsObject = jsonArray.get(i).getAsJsonObject();
                    generatedText[i] = responsObject.get("generated_text").getAsString().trim();

                }

                for (String text : generatedText) {
                    String[] parts = text.split("\n");
                    for (String part : parts) {
                        part = part.trim();// remove space
                        // this needs to be done because the AI sometimes get confuse and ask a question
                        // for further clarfication even though it gets the answer right in the 'second'
                        // best answer
                        if (!part.contains("?") && !part.isEmpty() && !(part.charAt(0) == '.')) {
                            bestAnswer = part;
                            break;
                        }
                    }
                    if (bestAnswer != null) {
                        return bestAnswer;// exit loop since the best answer that does not contain '?' is found
                    }
                }
            } else {
                System.out.println("Wasn't successful");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bestAnswer;
    }

}
