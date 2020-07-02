package net.energo.grodno.pes.smsSender.utils.smsAPI;

public class SmsResponse {
    private Integer sms_id;
    private Integer sms_count;
    private Integer operator;
    private Integer error_code;
    private Long recipient;

    public SmsResponse(Integer sms_id, Integer sms_count, Integer operator, Integer error_code, Long recipient) {
        this.sms_id = sms_id;
        this.sms_count = sms_count;
        this.operator = operator;
        this.error_code = error_code;
        this.recipient = recipient;
    }
}
