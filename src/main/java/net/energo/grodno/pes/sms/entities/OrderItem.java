package net.energo.grodno.pes.sms.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name="order_items")
@NamedStoredProcedureQuery(
        name = "OrderItem.smsCount",
        procedureName = "sms_count",
        resultClasses = { Integer.class },
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, type = Timestamp.class, name = "date_from"),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = Timestamp.class, name = "date_to"),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class, name = "resid"),
                @StoredProcedureParameter(mode = ParameterMode.OUT, type = Integer.class, name = "report")
        }
)

public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "abonent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Abonent abonent;

    @Column(name = "sms_id")
    private String smsId;

    @Column(name = "sms_count")
    private Integer smsCount;

    @Column(name = "operator")
    private Integer operator;

    @Column(name = "error_code")
    private Integer errorCode;

    @Column(name = "sms_status")
    private String smsStatus;

    @Column(name = "recipient")
    private String recipient;

    public OrderItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Abonent getAbonent() {
        return abonent;
    }

    public void setAbonent(Abonent abonent) {
        this.abonent = abonent;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public Integer getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(Integer smsCount) {
        this.smsCount = smsCount;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(String smsStatus) {
        this.smsStatus = smsStatus;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", order=" + order +
                ", abonent=" + abonent +
                ", smsId=" + smsId +
                ", smsCount=" + smsCount +
                ", operator=" + operator +
                ", errorCode=" + errorCode +
                ", smsStatus='" + smsStatus + '\'' +
                ", recipient='" + recipient + '\'' +
                '}';
    }
}
