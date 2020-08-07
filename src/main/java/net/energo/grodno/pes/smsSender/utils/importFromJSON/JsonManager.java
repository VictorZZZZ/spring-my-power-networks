package net.energo.grodno.pes.smsSender.utils.importFromJSON;

import net.energo.grodno.pes.smsSender.utils.smsAPI.SmsResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JsonManager {
    private static final String jsonFile = "spravochniki.json";

    public static void main(String[] args) {
        try {
            readJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readJson() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line.replaceAll("\\s\\s","").replaceAll("\\[\\s\\}",""));//убираем пробелы из строки
        }

        //System.out.println(stringBuilder.toString());

        JSONArray jsonArray = new JSONArray(stringBuilder.toString());

        for(int i = 0; i < jsonArray.length() ; i++){
            //System.out.println(jsonArray.getJSONObject(i));
            JSONObject res = jsonArray.getJSONObject(i).getJSONObject("Res");
            System.out.println(res.getString("name"));
            JSONArray substations = res.getJSONArray("Substations");
            for (int j = 0; j < substations.length(); j++) {
                System.out.println("   "+ substations.getJSONObject(j).getString("name"));

            }

        }
    }
}
