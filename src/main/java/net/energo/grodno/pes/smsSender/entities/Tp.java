package net.energo.grodno.pes.smsSender.entities;

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
    @Column(name="dbf_id")
    private String dbf_id;

    @ManyToOne
    @JoinColumn(name="res_id")
    private Res res;

    @OneToMany(mappedBy = "tp")
    private List<Fider> fiders;

    public Tp() {
    }

    public Tp(String name, String dbf_id, Res res) {
        this.name = name;
        this.dbf_id = dbf_id;
        this.res = res;
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

    public String getDbf_id() {
        return dbf_id;
    }

    public void setDbf_id(String dbf_id) {
        this.dbf_id = dbf_id;
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
                ", dbf_id='" + dbf_id + '\'' +
                ", res=" + res +
                ", fiders=" + fiders +
                '}';
    }
}
