package net.energo.grodno.pes.smsSender.utils.smsAPI;

public class StatusResponse {
    //sms_id":886055815,"sms_count":"1","operator":"2","sms_status":"Delivered","recipient":"+375297819778"

    private Long smsId;
    private Integer smsCount;
    private Integer operator;
    private String smsStatus;
    private String recipient;

    public StatusResponse(Long smsId, Integer smsCount, Integer operator, String smsStatus, String recipient) {
        this.smsId = smsId;
        this.smsCount = smsCount;
        this.operator = operator;
        this.smsStatus = smsStatus;
        this.recipient = recipient;
    }

    public Long getSmsId() {
        return smsId;
    }

    public Integer getSmsCount() {
        return smsCount;
    }

    public Integer getOperator() {
        return operator;
    }

    public String getSmsStatus() {
        return smsStatus;
    }

    public String getRecipient() {
        return recipient;
    }
}
