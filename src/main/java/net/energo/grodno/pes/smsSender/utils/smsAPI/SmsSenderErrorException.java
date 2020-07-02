package net.energo.grodno.pes.smsSender.utils.smsAPI;

public class SmsSenderErrorException extends Exception {
    public SmsSenderErrorException(Integer error) {
        super("Ошибка на стороне SMS Assistent:"+error);
    }
}
