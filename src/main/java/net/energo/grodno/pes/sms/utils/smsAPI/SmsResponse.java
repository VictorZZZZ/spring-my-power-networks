package net.energo.grodno.pes.sms.utils.smsAPI;

public class SmsResponse {
    private String smsId;
    private Integer smsCount;
    private Integer operator;
    private Integer errorCode;
    private String recipient;

    public SmsResponse(String smsId, Integer smsCount, Integer operator, Integer errorCode, String recipient) {
        this.smsId = smsId;
        this.smsCount = smsCount;
        this.operator = operator;
        this.errorCode = errorCode;
        this.recipient = recipient;
    }

    public String getSmsId() {
        return smsId;
    }

    public Integer getSmsCount() {
        return smsCount;
    }

    public Integer getOperator() {
        return operator;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getRecipientWithoutPrefix(){
        if(recipient.startsWith("375")) return recipient.substring(3);
        else return recipient;
    }
}
