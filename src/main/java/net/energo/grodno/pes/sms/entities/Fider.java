package net.energo.grodno.pes.sms.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fider")
public class Fider {
    public static final int EMPTY_FIDER_ID = 0;
    public static final String EMPTY_FIDER_NAME = "Без номера";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "dbf_id", nullable = false, columnDefinition = "integer default 0")
    private int dbfId;
    @Column(name = "input_manually", nullable = false, columnDefinition = "boolean default false")
    private boolean inputManually;

    @ManyToOne
    @JoinColumn(name = "tp_id")
    private Tp tp;

    @OneToMany(mappedBy = "fider", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderBy("surname")
    private List<Abonent> abonents;

    public Fider() {
    }

    public Fider(String name, int dbfId, Tp tp, boolean inputManually) {
        this.name = name;
        this.dbfId = dbfId;
        this.tp = tp;
        this.inputManually = inputManually;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public void addAbonent(Abonent abonent) {
        if (this.getAbonents() == null) {
            List<Abonent> abonentList = new ArrayList<>();
            abonentList.add(abonent);
            this.setAbonents(abonentList);
        } else {
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
        return name + " - " + tp.getName();
    }

    public void info() {
        System.out.println("\t\t\t\t\t\t\tFider{" +
                "name='" + name + '\'' +
                ", dbfId='" + dbfId + '\'' +
                '}');
        for (Abonent abonent : abonents) {
            System.out.println("\t\t\t\t\t\t\t" + abonent.toShortString());
        }
    }
}
