package net.energo.grodno.pes.sms.utils.smsAPI;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Getter
public class SmsAPI {
    private static Logger logger = LoggerFactory.getLogger(SmsAPI.class);

    @Value("${smsApi.user}")
    private String smsUser;
    @Value("${smsApi.password}")
    private String smsPassword;
    @Value("${smsApi.from}")
    private String smsFrom;
    private int smsValid = 86400;
    private static final String CHECK_BALANCE_URL = "https://userarea.sms-assistent.by/api/v1/credits/plain";
    private static final String SMS_SEND_URL = "https://userarea.sms-assistent.by/api/v1/json";

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    //отправка СМС
    public List<SmsResponse> sendSms(List<String> numbers, String message) throws IOException, InterruptedException, SmsSenderErrorException {
        //готовим JSON
        JSONObject jsonBody = prepareJSON(numbers, message);

        // add json header
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .uri(URI.create(SMS_SEND_URL))
                .setHeader("User-Agent", "ELEKTROSETI HttpClient Bot") // add request header
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info("Адрес запроса {}", SMS_SEND_URL);

        logger.info("Статус:{},Ответ:{}", response.statusCode(), response.body());
        if (response.statusCode() == 200) {
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
        if (!respJson.isNull("error")) {
            throw new SmsSenderErrorException(respJson.getInt("error"));
        }

        JSONArray jsonArray = respJson.getJSONObject("message").getJSONArray("msg");
        List<SmsResponse> smsResponses = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            SmsResponse smsResp = new SmsResponse(
                    String.valueOf(jsonArray.getJSONObject(i).getLong("sms_id")),
                    jsonArray.getJSONObject(i).getInt("sms_count"),
                    jsonArray.getJSONObject(i).getInt("operator"),
                    jsonArray.getJSONObject(i).getInt("error_code"),
                    ((Long) jsonArray.getJSONObject(i).getLong("recipient")).toString());
            smsResponses.add(smsResp);
        }

        return smsResponses;
    }


    //Проверка статуса СМС
    public List<StatusResponse> checkStatuses(List<String> strList) throws SmsSenderErrorException, IOException, InterruptedException {
        //готовим JSON
        JSONObject jsonBody = prepareJSON4Statuses(strList);

        // add json header
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .uri(URI.create(SMS_SEND_URL))
                .setHeader("User-Agent", "ELEKTROSETI HttpClient Bot") // add request header
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        logger.info("Ответ: {}", response.body());
        if (response.statusCode() == 200) {
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
        if (!respJson.isNull("error")) {
            throw new SmsSenderErrorException(respJson.getInt("error"));
        }

        JSONArray jsonArray = respJson.getJSONObject("status").getJSONArray("msg");
        List<StatusResponse> statusResponses = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            StatusResponse smsResp = new StatusResponse(
                    String.valueOf(jsonArray.getJSONObject(i).getLong("sms_id")),
                    jsonArray.getJSONObject(i).getInt("sms_count"),
                    jsonArray.getJSONObject(i).getInt("operator"),
                    jsonArray.getJSONObject(i).getString("sms_status"),
                    jsonArray.getJSONObject(i).getString("recipient")
            );
            statusResponses.add(smsResp);
        }

        return statusResponses;
    }

    private JSONObject prepareJSON(List<String> numbers, String message) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("login", smsUser);
        jsonBody.put("password", smsPassword);
        jsonBody.put("command", "sms_send");

        JSONObject jsonDefault = new JSONObject();
        jsonDefault.put("sender", smsFrom);
        jsonDefault.put("sms_text", message);

        JSONArray jsonMsg = new JSONArray();
        List<String> nonDuplicatedNumbers = numbers.stream().distinct().collect(Collectors.toList());
        logger.warn("Из {} номеров выбрано {} все дублированные очищены.", numbers.size(),nonDuplicatedNumbers.size());
        for (String number : nonDuplicatedNumbers) {
            jsonMsg.put((new JSONObject()).put("recipient", number));
        }

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("default", jsonDefault);
        jsonMessage.put("msg", jsonMsg);
        jsonBody.put("message", jsonMessage);

        logger.info(jsonBody.toString());

        return jsonBody;
    }

    private JSONObject prepareJSON4Statuses(List<String> idList) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("login", smsUser);
        jsonBody.put("password", smsPassword);
        jsonBody.put("command", "statuses");

        JSONArray smsIdArray = new JSONArray();
        for (String smsId : idList) {
                smsIdArray.put((new JSONObject()).put("sms_id", smsId));
        }

        JSONObject msg = new JSONObject();
        msg.put("msg", smsIdArray);

        jsonBody.put("status", msg);

        logger.info(jsonBody.toString());


        return jsonBody;
    }

    public String checkBalance() throws IOException, InterruptedException {
        //https://userarea.sms-assistent.by/api/v1/credits/plain?user=Grodnoenergo&password=r9359538
        String requestURL = CHECK_BALANCE_URL + "?user=" + smsUser + "&password=" + smsPassword;
        logger.info("Request Balance:{}", requestURL);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(requestURL))
                .setHeader("User-Agent", "Elektroseti v1.0 client") // add request header
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info("Response.statusCode():{}", response.statusCode());
        logger.info("Response.body:{}", response.body());

        return response.body();

    }
}
