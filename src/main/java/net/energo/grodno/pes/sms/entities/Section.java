package net.energo.grodno.pes.sms.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="section")
public class Section {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name")
    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name="substation_id")
    private Substation substation;

    @OneToMany(mappedBy = "section")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private List<Line> lines;

    public Section() {
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

    public Substation getSubstation() {
        return substation;
    }

    public void setSubstation(Substation substation) {
        this.substation = substation;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", substation=" + substation.getName() +
                ", lines=" + lines.size() +
                '}';
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public void info() {
        System.out.println("\t\t Section{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", substation=" + substation.getName()+"}");
        for (Line line:lines) {
            line.info();
        }
    }
    public List<Tp> getTps(){
        List<Tp> tpList = new ArrayList<>();
            for(Line line:this.getLines()){
                tpList.addAll(line.getTps());
            }
        return tpList;
    }
}
