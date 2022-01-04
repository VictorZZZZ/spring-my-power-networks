package net.energo.grodno.pes.sms.entities;

import net.energo.grodno.pes.sms.entities.users.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="text_templates")
public class TextTemplate {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition="TEXT")
    private String template;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="created")
    @CreationTimestamp
    @DateTimeFormat(pattern="dd-MMM-YYYY HH:mm")
    private Date created;

    @Column(name="modified")
    @UpdateTimestamp
    @DateTimeFormat(pattern="dd-MMM-YYYY HH:mm")
    private Date modified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "TextTemplate{" +
                "id=" + id +
                ", template='" + template + '\'' +
                ", user=" + user +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}
