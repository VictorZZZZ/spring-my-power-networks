package net.energo.grodno.pes.sms.entities;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="res")
public class Res {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name="cached_abonents_count",nullable = false, columnDefinition="bigint default 0")
    private Long cachedAbonentsCount;

    @Column(name="modified")
    @UpdateTimestamp
    @DateTimeFormat(pattern="dd-MMM-YYYY HH:mm")
    private Date modified;

//    @OneToMany(mappedBy = "res",fetch = FetchType.EAGER)
//    @OnDelete(action= OnDeleteAction.CASCADE)
//    private List<Tp> tps;

    @OneToMany(mappedBy = "res")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private List<Substation> substations;

    public Res() {
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

    public List<Tp> getTps() {
        List<Tp> tps = new ArrayList<>();
        for (Substation substation: substations) {
            for (Section section:substation.getSections()) {
                for (Line line: section.getLines()) {
                    for (Part part:line.getParts()) {
                        tps.addAll(part.getTps());
                    }
                }
            }
        }

        return tps;
    }
//
//    public void setTps(List<Tp> tps) {
//        this.tps = tps;
//    }

    public Long getCachedAbonentsCount() {
        return cachedAbonentsCount;
    }

    public void setCachedAbonentsCount(Long cachedAbonentsCount) {
        this.cachedAbonentsCount = cachedAbonentsCount;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public List<Substation> getSubstations() {
        return substations;
    }

    public void setSubstations(List<Substation> substations) {
        this.substations = substations;
    }

    public void addSubstation(Substation substation) {
        this.substations.add(substation);
    }

    @Override
    public String toString() {
        return "Res{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cachedAbonentsCount=" + cachedAbonentsCount +
                ", modified=" + modified +
                ", substations=" + substations +
                '}';
    }

    public void info(){
        System.out.println("id=" + id +
                           ", name='" + name + "\'");
        if(substations != null) {
            for (Substation subst : substations) {
                subst.info();
            }
        }
    }
}
