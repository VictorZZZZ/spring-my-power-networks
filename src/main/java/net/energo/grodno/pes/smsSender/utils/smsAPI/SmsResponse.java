package net.energo.grodno.pes.smsSender.utils.smsAPI;

public class SmsResponse {
    private Integer smsId;
    private Integer smsCount;
    private Integer operator;
    private Integer errorCode;
    private Long recipient;

    public SmsResponse(Integer smsId, Integer smsCount, Integer operator, Integer errorCode, Long recipient) {
        this.smsId = smsId;
        this.smsCount = smsCount;
        this.operator = operator;
        this.errorCode = errorCode;
        this.recipient = recipient;
    }
}
