package net.energo.grodno.pes.sms.utils.smsAPI;

import java.util.HashMap;

public class ErrorsTable {
    public static final HashMap<Integer,String> ERRORS= new HashMap<Integer,String>() {{
        put(0, "Отправлено");
        put(-1, "Недостаточно средств");
        put(-2, "Неправильный логин/пароль");
        put(-3, "Отсутствует текст сообщения");
        put(-4, "Некорректное значение номера получателя");
        put(-5, "Некорректное значение отправителя сообщения");
        put(-6, "Отсутствует логин");
        put(-7, "Отсутствует Пароль");
        put(-10, "Сервис временно недоступен");
        put(-11, "Некорректное значение ID сообщения");
        put(-12, "Другая ошибка");
        put(-13, "Заблокировано");
        put(-14, "Запрос не укладывается по времени на отправку СМС");
        put(-15, "Некорректное значение даты отправки рассылки");
    }};

    public static final HashMap<String,String> STATUS= new HashMap<String,String>() {{
        put("Queued","В очереди");
        put("Sent","Отправлено");
        put("Delivered","Доставлено");
        put("Expired","Срок жизни истёк. Не доставлено");
        put("Rejected","Не доставлено");
        put("Unknown","Статус Неизвестен");
        put("Failed","Не отправлено");
    }};
}
