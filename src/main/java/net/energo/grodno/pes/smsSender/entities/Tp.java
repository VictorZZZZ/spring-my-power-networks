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
    @Column(name="dbf_code")
    private String dbf_code;

    @ManyToOne
    @JoinColumn(name="res_id")
    private Res res;

    @OneToMany(mappedBy = "tp")
    private List<Fider> fiders;

    public Tp() {
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

    public String getDbf_code() {
        return dbf_code;
    }

    public void setDbf_code(String dbf_code) {
        this.dbf_code = dbf_code;
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
                ", dbf_code='" + dbf_code + '\'' +
                ", res=" + res +
                ", fiders=" + fiders +
                '}';
    }
}
