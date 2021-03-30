package net.energo.grodno.pes.smsSender.entities;

import lombok.Getter;
import lombok.Setter;
import net.energo.grodno.pes.smsSender.entities.users.User;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "message")
    @NotBlank
    private String message;

    /**
     * Цена одного смс
     */
    @Column(name = "price", precision = 5, scale = 4)
    private Double price;

    /**
     * Стоимость рассылки
     */
    @Column(name = "amount", precision = 5, scale = 4)
    private Double amount;

    /**
     * Количество смс в Рассылке
     */
    @Column(name = "sms_count")
    private Integer smsCount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="created")
    @CreationTimestamp
    @DateTimeFormat(pattern="dd-MMM-YYYY HH:mm")
    private Date created;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", smsCount=" + smsCount +
                ", user=" + user +
                ", created=" + created +
                '}';
    }
}
