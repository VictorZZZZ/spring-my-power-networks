package net.energo.grodno.pes.smsSender.entities;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.context.annotation.Primary;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name="abonent")
public class Abonent {
//    @Id
//    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;

    @Id
    @Column(name="account_number")
    private Long accountNumber;
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
    @Column(name="input_manually",nullable = false, columnDefinition="boolean default false")
    private boolean inputManually;

    @Column(name="created")
    @CreationTimestamp
    @DateTimeFormat(pattern="dd-MMM-YYYY HH:mm")
    private Date created;

    @Column(name="modified")
    @UpdateTimestamp
    @DateTimeFormat(pattern="dd-MMM-YYYY HH:mm")
    private Date modified;

    @ManyToOne
    @JoinColumn(name="fider_id")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private Fider fider;

    public Abonent() {
    }

    public Abonent(Long accountNumber, String surname, String name, String middlename, String homePhone, String firstPhone, String secondPhone, String opora, Fider fider, boolean inputManually) {
        this.accountNumber = accountNumber;
        this.surname = surname;
        this.name = name;
        this.middlename = middlename;
        this.homePhone = homePhone;
        this.firstPhone = firstPhone;
        this.secondPhone = secondPhone;
        this.opora = opora;
        this.fider = fider;
        this.inputManually=inputManually;
    }

//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }

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

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean getInputManually() {
        return inputManually;
    }

    public void setInputManually(boolean inputManually) {
        this.inputManually = inputManually;
    }
}
