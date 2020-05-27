package net.energo.grodno.pes.smsSender.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="fider")
public class Fider {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name="name")
    private String name;
    @Column(name="dbf_code")
    private String dbf_code;

    @ManyToOne
    @JoinColumn(name="tp_id")
    private Tp tp;

    @OneToMany(mappedBy = "fider")
    private List<Abonent> abonents;

    public Fider() {
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

    public Tp getTp() {
        return tp;
    }

    public void setTp(Tp tp) {
        this.tp = tp;
    }

    public List<Abonent> getAbonents() {
        return abonents;
    }

    public void setAbonents(List<Abonent> abonents) {
        this.abonents = abonents;
    }
}
