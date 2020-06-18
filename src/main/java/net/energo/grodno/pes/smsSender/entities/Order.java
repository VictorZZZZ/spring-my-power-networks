package net.energo.grodno.pes.smsSender.entities;

import net.energo.grodno.pes.smsSender.entities.users.User;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "message")
    private String message;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="created")
    @CreationTimestamp
    @DateTimeFormat(pattern="dd-MMM-YYYY HH:mm")
    private Date created;

}
