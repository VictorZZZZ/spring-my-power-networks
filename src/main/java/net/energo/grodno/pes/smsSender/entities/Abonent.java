package net.energo.grodno.pes.smsSender.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="abonent")
public class Abonent {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name="surname")
    private String surname;
    @Column(name="name")
    private String name;
    @Column(name="middlename")
    private String middlename;
    @Column(name="home_phone")
    private String homePhone;
    @Column(name="first_phone")
    private String firstPhone;
    @Column(name="second_phone")
    private String secondPhone;
    @Column(name="opora")
    private String opora;
    @Column(name="notes")
    private String notes;
    @Column(name="created")
    private Date created;
    @Column(name="modified")
    private Date modified;

    @ManyToOne
    @JoinColumn(name="fider_id")
    private Fider fider;

    public Abonent() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getFirstPhone() {
        return firstPhone;
    }

    public void setFirstPhone(String firstPhone) {
        this.firstPhone = firstPhone;
    }

    public String getSecondPhone() {
        return secondPhone;
    }

    public void setSecondPhone(String secondPhone) {
        this.secondPhone = secondPhone;
    }

    public String getOpora() {
        return opora;
    }

    public void setOpora(String opora) {
        this.opora = opora;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Fider getFider() {
        return fider;
    }

    public void setFider(Fider fider) {
        this.fider = fider;
    }
}
