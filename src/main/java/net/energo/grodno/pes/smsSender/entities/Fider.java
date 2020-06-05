package net.energo.grodno.pes.smsSender.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="fider")
public class Fider {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="name")
    private String name;
    @Column(name="dbf_id")
    private String dbf_id;

    @ManyToOne
    @JoinColumn(name="tp_id")
    private Tp tp;

    @OneToMany(mappedBy = "fider")
    private List<Abonent> abonents;

    public Fider() {
    }

    public Fider(String name, String dbf_id, Tp tp) {
        this.name = name;
        this.dbf_id = dbf_id;
        this.tp = tp;
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

    public void addAbonent(Abonent abonent){
        if(this.getAbonents()==null){
            List<Abonent> abonentList=new ArrayList<>();
            abonentList.add(abonent);
            this.setAbonents(abonentList);
        } else{
            List<Abonent> abonentList = this.getAbonents();
            abonentList.add(abonent);
            this.setAbonents(abonentList);
        }

    }

    @Override
    public String toString() {
        return "Fider{" +
                "name='" + name + '\'' +
                ", dbf_id='" + dbf_id + '\'' +
                '}';
    }
}
