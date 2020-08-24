package net.energo.grodno.pes.smsSender.entities;

import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Res;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="substation")
public class Substation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name")
    @NotBlank
    private String name;

    @Column(name="voltage")
    private String voltage;

    @ManyToOne
    @JoinColumn(name="res_id")
    private Res res;

    @OneToMany(mappedBy = "substation")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private List<Section> sections;

    public Substation() {
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

    public Res getRes() {
        return res;
    }

    public void setRes(Res res) {
        this.res = res;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    @Override
    public String toString() {
        return "Substation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", res=" + res.getName() +
                ", sections=" + sections.size() +
                '}';
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void info() {
        System.out.println("\t Substation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", res=" + res.getName() +
                '}');
        for (Section section: sections) {
            section.info();
        }
    }

    public List<Tp> getTps(){
        List<Tp> tpList = new ArrayList<>();
        for(Section section:this.getSections()){
            tpList.addAll(section.getTps());
        }
        return tpList;
    }
}
