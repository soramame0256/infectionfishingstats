package com.github.soramame0256.infectionfishingstats.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonUtils implements IDataUtil<JsonElement>{
    private final String filePath;
    private final JsonObject root;
    public JsonUtils(String path) throws IOException {
        this.filePath = path;
        if(Files.notExists(Paths.get(filePath))){
            File f = new File(path);
            f.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(filePath);
            PrintWriter writer = new PrintWriter(fw);
            writer.print("{}");
            writer.flush();
            writer.close();
        }
        this.root = convertFileToJSON(filePath);
    }
    @Override
    public String getStringData(String index) {
        if (this.root.has(index)) {
            return this.root.get(index).getAsString();
        }else{
            return "";
        }
    }

    @Override
    public void saveStringData(String index, String value) {
        this.root.addProperty(index, value);
        try {
            flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Number getNumberData(String index) {
        if (this.root.has(index)) {
            return this.root.get(index).getAsNumber();
        }else{
            return 0;
        }
    }

    @Override
    public void saveNumberData(String index, Number value) {
        this.root.addProperty(index, value);
        try {
            flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean getBooleanData(String index) {
        if (this.root.has(index)) {
            return this.root.get(index).getAsBoolean();
        }else{
            return false;
        }
    }

    @Override
    public void saveBooleanData(String index, boolean value) {
        this.root.addProperty(index, value);
        try {
            flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void saveData(String index, JsonElement value) {
        this.root.add(index, value);
        try {
            flush();
        }catch(IOException e){
            System.out.println("Saving data failed");
            e.printStackTrace();
        }
    }

    @Override
    public JsonElement getRoot() {
        return this.root;
    }

    @Override
    public void flush() throws IOException{
        String print = new GsonBuilder().serializeNulls().setPrettyPrinting().create()
                .toJson(root);
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(print);
        fileWriter.flush();
        fileWriter.close();
    }
    //From anywhere
    public static JsonObject convertFileToJSON (String fileName){
        // Read from File to String
        JsonObject jo = new JsonObject();
        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(fileName));
            jo = jsonElement.getAsJsonObject();
        } catch (IOException ignored) {}
        return jo;
    }
}
