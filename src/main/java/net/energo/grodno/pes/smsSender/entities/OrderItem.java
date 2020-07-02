package net.energo.grodno.pes.smsSender.entities;

import javax.persistence.*;

@Entity
@Table(name="order_items")
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
    private Abonent abonent;

    @Column(name = "sms_id")
    private Integer smsId;

    @Column(name = "sms_count")
    private Integer smsCount;

    @Column(name = "operator")
    private Integer operator;

    @Column(name = "error_code")
    private Integer errorCode;

    @Column(name = "sms_status")
    private Integer smsStatus;

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
}
