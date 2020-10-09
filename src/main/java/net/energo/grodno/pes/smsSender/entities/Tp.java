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
    private Long id;

    @Column(name="name")
    @NotBlank
    private String name;

    @Column(name="dbf_id",nullable = false, columnDefinition="integer default 0")
    private int dbfId;

    @Column(name="input_manually",nullable = false, columnDefinition="boolean default false")
    private boolean inputManually;


    @Column(name="res_id")
    private Integer resId;
//    @ManyToOne
//    @JoinColumn(name="res_id")
//    private Res res;

    @ManyToOne
    @JoinColumn(name="part_id")
    private Part part;

    @OneToMany(mappedBy = "tp")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private List<Fider> fiders;

    public Tp() {
    }

    public Tp(String name, int dbfId, Part part,boolean inputManually, Integer resId) {
        this.name = name;
        this.dbfId = dbfId;
        this.part = part;
        this.inputManually = inputManually;
        this.resId = resId;
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

    public boolean isInputManually() {
        return inputManually;
    }

    public void setInputManually(boolean inputManually) {
        this.inputManually = inputManually;
    }

    public Res getRes() {
        return part.getLine().getSection().getSubstation().getRes();
    }

    public List<Fider> getFiders() {
        return fiders;
    }

    public void setFiders(List<Fider> fiders) {
        this.fiders = fiders;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    @Override
    public String toString() {
        return "Tp{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dbfId='" + dbfId + '\'' +
                ", res=" + resId +
                ", part=" + part +
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

    public void addFider(Fider fider) {
        fiders.add(fider);
    }

    public void info() {
        System.out.println("\t\t\t\t\tTp{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dbfId='" + dbfId + '\'' +
                "}");
        for (Fider fider:fiders) {
            fider.info();
        }
    }
}
