package com.iarlaith.personalassistant;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpeechInputHandler {

    private String input;

    public SpeechInputHandler(String input) {
        this.input = input;
    }

    public static Map<String, Integer> countOccurrences(Context context, String inputString, List<String> filenames) throws IOException {
        Map<String, Integer> counts = new HashMap<>();
        for (String filename : filenames) {
            String text = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                try {
                    InputStream inputStream = context.getAssets().open(filename);
                    int size = inputStream.available();
                    byte[] buffer = new byte[size];
                    inputStream.read(buffer);
                    text = new String(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            List<String> words = Arrays.asList(text.split("\\W"));
            List<String> input = Arrays.asList(inputString.split(" "));
            int count = 0;
            for(String i : input){
                count += words.stream().filter(word -> word.equals(i)).collect(Collectors.toList()).size();
            }
            counts.put(filename, count);
            }
        }
        return counts;
    }

    public static String getMaxCountFilename(Map<String, Integer> counts) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(counts.entrySet().stream().max(Map.Entry.comparingByValue()).get().getValue() != 0)
            return counts.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
        }
        return "Not Sure";
    }
}
