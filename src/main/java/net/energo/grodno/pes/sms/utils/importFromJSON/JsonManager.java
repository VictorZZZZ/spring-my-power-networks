package net.energo.grodno.pes.sms.utils.importFromJSON;

import net.energo.grodno.pes.sms.entities.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonManager {
    private static final String JSON_FILE = "spravochniki.json";

    public static void main(String[] args) {
        try {
            readJson();
            System.out.println("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Res> readJson() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(JSON_FILE));
        StringBuilder stringBuilder = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null) {
            stringBuilder.append(str.replaceAll("\\s\\s","").replaceAll("\\[\\s\\}",""));//убираем пробелы из строки
        }

        //System.out.println(stringBuilder.toString());

        JSONArray jsonArray = new JSONArray(stringBuilder.toString());
        List<Res> resList=new ArrayList<>();
        for(int i = 0; i < jsonArray.length() ; i++){
            //System.out.println(jsonArray.getJSONObject(i));
            JSONObject resJson = jsonArray.getJSONObject(i).getJSONObject("Res");
            if(!resJson.getString("name").equals("")) {
                Res res = new Res();
                res.setId(resJson.getInt("id"));
                res.setName(resJson.getString("name"));
                res.setSubstations(new ArrayList<Substation>());
                JSONArray substJson = resJson.getJSONArray("Substations");
                for (int j = 0; j < substJson.length(); j++) {
                    if(!substJson.getJSONObject(j).getString("name").equals("")) {
                        Substation substation = new Substation();
                        substation.setName(substJson.getJSONObject(j).getString("name"));
                        substation.setVoltage(substJson.getJSONObject(j).getString("voltage"));
                        substation.setRes(res);
                        substation.setSections(new ArrayList<Section>());
                        JSONArray sectionsJson = substJson.getJSONObject(j).getJSONArray("Sections");
                        for (int k = 0; k < sectionsJson.length(); k++) {
                            if (!sectionsJson.getJSONObject(k).getString("name").equals("")) {
                                Section section = new Section();
                                section.setName(sectionsJson.getJSONObject(k).getString("name"));
                                section.setSubstation(substation);
                                section.setLines(new ArrayList<>());
                                JSONArray linesJson = sectionsJson.getJSONObject(k).getJSONArray("Lines");
                                for (int l = 0; l < linesJson.length(); l++) {
                                    if (!linesJson.getJSONObject(l).getString("name").equals("")) {
                                        Line line = new Line();
                                        line.setName(linesJson.getJSONObject(l).getString("name"));
                                        line.setSection(section);
                                        line.setParts(new ArrayList<>());
                                        JSONArray partsJson = linesJson.getJSONObject(l).getJSONArray("Parts");
                                        for (int m = 0; m < partsJson.length(); m++) {
                                            if (!partsJson.getJSONObject(m).getString("name").equals("")) {
                                                Part part = new Part();
                                                part.setName(partsJson.getJSONObject(m).getString("name"));
                                                part.setTps(new ArrayList<>());
                                                part.setLine(line);
                                                JSONArray tpsJson = partsJson.getJSONObject(m).getJSONArray("Tp");
                                                for (int n = 0; n < tpsJson.length(); n++) {
                                                    System.out.println(tpsJson.getJSONObject(n).getString("name"));
                                                    if (!tpsJson.getJSONObject(n).getString("name").equals("")) {
                                                        Tp tp = new Tp();
                                                        tp.setName(tpsJson.getJSONObject(n).getString("name"));
                                                        tp.setPart(part);
                                                        Fider fider = new Fider(Fider.EMPTY_FIDER_NAME, Fider.EMPTY_FIDER_ID, tp, true);
                                                        tp.setFiders(new ArrayList<>());
                                                        tp.addFider(fider);
                                                        tp.setResId(res.getId());
                                                        fider.setAbonents(new ArrayList<>());
                                                        JSONArray abonentsJson = tpsJson.getJSONObject(n).getJSONArray("Abonents");
                                                        for (int o = 0; o < abonentsJson.length(); o++) {
                                                            Abonent abonent = new Abonent();
                                                            abonent.setSurname(abonentsJson.getJSONObject(o).getString("name"));
                                                            abonent.setName("");
                                                            abonent.setMiddlename("");
                                                            abonent.setHomePhone("");
                                                            abonent.setFirstPhone(abonentsJson.getJSONObject(o).getString("phone_1"));
                                                            abonent.setSecondPhone(abonentsJson.getJSONObject(o).getString("phone_2"));
                                                            abonent.setFider(fider);
                                                            abonent.setInputManually(true);
                                                            fider.addAbonent(abonent);
                                                        }
                                                        part.addTp(tp);
                                                    }
                                                }
                                                line.addPart(part);
                                            }
                                        }
                                        section.addLine(line);
                                    }
                                }
                                substation.addSection(section);
                            }
                        }
                        res.addSubstation(substation);
                    }
                }
               // res.info();
                resList.add(res);
            }
        }
        return resList;
    }
}
