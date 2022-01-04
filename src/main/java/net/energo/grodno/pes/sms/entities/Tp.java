package net.energo.grodno.pes.sms.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="tp")
@Getter
@Setter
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

    @Column(name="created")
    @CreationTimestamp
    @DateTimeFormat(pattern="dd-MMM-YYYY HH:mm")
    private Date created;

    public Tp() {
    }

    public Tp(String name, int dbfId, Part part,boolean inputManually, Integer resId) {
        this.name = name;
        this.dbfId = dbfId;
        this.part = part;
        this.inputManually = inputManually;
        this.resId = resId;
    }

    public Res getRes() {
        return part.getLine().getSection().getSubstation().getRes();
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
