package net.energo.grodno.pes.smsSender.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.bind.annotation.Mapping;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name="tp")
public class Tp {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name")
    @NotBlank
    private String name;
    @Column(name="dbf_id",nullable = false, columnDefinition="integer default 0")
    private int dbfId;

    @Column(name="input_manually",nullable = false, columnDefinition="boolean default false")
    private boolean inputManually;

    @ManyToOne
    @JoinColumn(name="res_id")
    private Res res;

    @OneToMany(mappedBy = "tp")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private List<Fider> fiders;

    public Tp() {
    }

    public Tp(String name, int dbfId, Res res,boolean inputManually) {
        this.name = name;
        this.dbfId = dbfId;
        this.res = res;
        this.inputManually = inputManually;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDbfId() {
        return dbfId;
    }

    public void setDbfId(int dbfId) {
        this.dbfId = dbfId;
    }

    public boolean isInputManually() {
        return inputManually;
    }

    public void setInputManually(boolean inputManually) {
        this.inputManually = inputManually;
    }

    public Res getRes() {
        return res;
    }

    public void setRes(Res res) {
        this.res = res;
    }

    public List<Fider> getFiders() {
        return fiders;
    }

    public void setFiders(List<Fider> fiders) {
        this.fiders = fiders;
    }

    @Override
    public String toString() {
        return "Tp{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dbfId='" + dbfId + '\'' +
                ", res=" + res +
                ", fiders=" + fiders +
                '}';
    }

    public String toShortString() {
        return "Tp{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dbfId='" + dbfId + '\'' +
                '}';
    }
}
