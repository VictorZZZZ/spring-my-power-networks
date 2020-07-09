package net.energo.grodno.pes.smsSender.utils.smsAPI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SmsAPI {
    private static final String smsUser = "Grodnoenergo";
    private static final String smsPassword = "r9359538";
    private static String smsFrom = "Elektroseti";
    private int smsValid = 86400;
    private static String checkBalanceURL = "https://userarea.sms-assistent.by/api/v1/credits/plain";
    private static String smsSendURL = "https://userarea.sms-assistent.by/api/v1/json";

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) throws InterruptedException, SmsSenderErrorException, IOException {
        //
    }

    //отправка СМС
    public static List<SmsResponse> sendSms(List<String> numbers,String message) throws IOException, InterruptedException, SmsSenderErrorException {
        //готовим JSON
        JSONObject jsonBody = prepareJSON(numbers, message);

        // add json header
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .uri(URI.create(smsSendURL))
                .setHeader("User-Agent", "ELEKTROSETI HttpClient Bot") // add request header
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Ответ:" + response.body());
        if(response.statusCode()==200) {
            //Если всё ОК
            return parseResponse(response.body());
        } else {
            //Если ошибка
            JSONObject respJson = new JSONObject(response.body());
            throw new SmsSenderErrorException(respJson.getInt("error"));
        }

    }

    private static List<SmsResponse> parseResponse(String response) throws SmsSenderErrorException {
        //String response = "{\"message\":{\"msg\":[{\"sms_id\":886055815,\"sms_count\":1,\"operator\":2,\"error_code\":0,\"recipient\":375297819778}]}}";
        //response = "{\"message\":{\"msg\":[{\"sms_id\":886055815,\"sms_count\":1,\"operator\":2,\"error_code\":0,\"recipient\":375297819778}]}}";
        //ответ приходит в виде:
        // {"message":{"msg":[{"sms_id":886146113,"sms_count":1,"operator":2,"error_code":0,"recipient":375297819778},
        //                      {"sms_id":886146114,"sms_count":1,"operator":2,"error_code":0,"recipient":375292589344}
        //                   ]
        //            }
        // }

        //или {"message":{"msg":[{"sms_id":886154330,"sms_count":0,"operator":0,"error_code":0,"recipient":29781977},{"sms_id":0,"sms_count":0,"operator":0,"error_code":-4,"recipient":29258934}]}}


        JSONObject respJson = new JSONObject(response);
        if(!respJson.isNull("error")) {
            throw new SmsSenderErrorException(respJson.getInt("error"));
        }

        JSONArray jsonArray = respJson.getJSONObject("message").getJSONArray("msg");
        List<SmsResponse> smsResponses=new ArrayList<>();
        for(int i = 0; i < jsonArray.length() ; i++){
            SmsResponse smsResp = new SmsResponse(
                    jsonArray.getJSONObject(i).getLong("sms_id"),
                    jsonArray.getJSONObject(i).getInt("sms_count"),
                    jsonArray.getJSONObject(i).getInt("operator"),
                    jsonArray.getJSONObject(i).getInt("error_code"),
                    ((Long)jsonArray.getJSONObject(i).getLong("recipient")).toString());
            smsResponses.add(smsResp);
        }

        return smsResponses;
    }



    //Проверка статуса СМС
    public static List<StatusResponse> checkStatuses(List<Long> strList) throws SmsSenderErrorException, IOException, InterruptedException {
        //готовим JSON
        JSONObject jsonBody = prepareJSON4Statuses(strList);

        // add json header
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .uri(URI.create(smsSendURL))
                .setHeader("User-Agent", "ELEKTROSETI HttpClient Bot") // add request header
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Ответ:" + response.body());
        if(response.statusCode()==200) {
            //Если всё ОК
            return parseStatusResponse(response.body());
        } else {
            //Если ошибка
            JSONObject respJson = new JSONObject(response.body());
            throw new SmsSenderErrorException(respJson.getInt("error"));
        }
    }

    private static List<StatusResponse> parseStatusResponse(String response) throws SmsSenderErrorException {
//        {"status":{"msg":[{"sms_id":886055815,"sms_count":"1","operator":"2","sms_status":"Delivered","recipient":"+375297819778"},
//                          {"sms_id":886055815,"sms_count":"1","operator":"2","sms_status":"Delivered","recipient":"+375297819778"},
//                          {"sms_id":886146113,"sms_count":"1","operator":"2","sms_status":"Delivered","recipient":"+375297819778"},
//                          {"sms_id":886146114,"sms_count":"1","operator":"2","sms_status":"Delivered","recipient":"+375292589344"}]}}

        JSONObject respJson = new JSONObject(response);
        if(!respJson.isNull("error")) {
            throw new SmsSenderErrorException(respJson.getInt("error"));
        }

        JSONArray jsonArray = respJson.getJSONObject("status").getJSONArray("msg");
        List<StatusResponse> statusResponses=new ArrayList<>();
        for(int i = 0; i < jsonArray.length() ; i++){
            StatusResponse smsResp = new StatusResponse(
                    jsonArray.getJSONObject(i).getLong("sms_id"),
                    jsonArray.getJSONObject(i).getInt("sms_count"),
                    jsonArray.getJSONObject(i).getInt("operator"),
                    jsonArray.getJSONObject(i).getString("sms_status"),
                    jsonArray.getJSONObject(i).getString("recipient")
            );
            statusResponses.add(smsResp);
        }

        return statusResponses;
    }

    private static JSONObject prepareJSON(List<String> numbers, String message) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("login",smsUser);
        jsonBody.put("password",smsPassword);
        jsonBody.put("command","sms_send");

        JSONObject jsonDefault = new JSONObject();
        jsonDefault.put("sender",smsFrom);
        jsonDefault.put("sms_text",message);

        JSONArray jsonMsg = new JSONArray();
        for (String number: numbers) {
            jsonMsg.put((new JSONObject()).put("recipient",number));
        }

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("default",jsonDefault);
        jsonMessage.put("msg",jsonMsg);
        jsonBody.put("message",jsonMessage);

        //System.out.println(jsonBody.toString());
        return jsonBody;
    }

    private static JSONObject prepareJSON4Statuses(List<Long> longList) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("login",smsUser);
        jsonBody.put("password",smsPassword);
        jsonBody.put("command","statuses");

        JSONArray smsIdArray = new JSONArray();
        for(Long smsId:longList){
            smsIdArray.put((new JSONObject()).put("sms_id",smsId.toString()));
        }

        JSONObject msg = new JSONObject();
        msg.put("msg",smsIdArray);

        jsonBody.put("status",msg);

        System.out.println(jsonBody.toString());

        return jsonBody;
    }

    public static String checkBalance() throws IOException, InterruptedException {
        //https://userarea.sms-assistent.by/api/v1/credits/plain?user=Grodnoenergo&password=r9359538
        String requestURL = checkBalanceURL+"?user="+smsUser+"&password="+smsPassword;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(requestURL))
                .setHeader("User-Agent", "Elektroseti v1.0 client") // add request header
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print response headers
        //HttpHeaders headers = response.headers();
        //headers.map().forEach((k, v) -> System.out.println(k + ":" + v));

        // print status code
        //System.out.println(response.statusCode());

        //System.out.println(response.statusCode());
        // print response body
        return response.body();

    }
}
