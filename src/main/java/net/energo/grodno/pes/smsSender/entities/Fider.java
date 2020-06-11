package net.energo.grodno.pes.smsSender.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @Column(name="dbf_id",nullable = false, columnDefinition="integer default 0")
    private int dbfId;
    @Column(name="input_manually",nullable = false, columnDefinition="boolean default false")
    private boolean inputManually;

    @ManyToOne
    @JoinColumn(name="tp_id")
    private Tp tp;

    @OneToMany(mappedBy = "fider",fetch = FetchType.EAGER)
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<Abonent> abonents;

    public Fider() {
    }

    public Fider(String name, int dbfId, Tp tp,boolean inputManually) {
        this.name = name;
        this.dbfId = dbfId;
        this.tp = tp;
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

    public boolean isInputManually() {
        return inputManually;
    }

    public void setInputManually(boolean inputManually) {
        this.inputManually = inputManually;
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
                ", dbfId='" + dbfId + '\'' +
                '}';
    }

    public String toShortString() {
        return name+" - "+tp.getName();
    }
}
