package net.energo.grodno.pes.sms.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="line")
public class Line {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name="section_id")
    private Section section;

    @OneToMany(mappedBy = "line")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private List<Part> parts;

    public Line() {
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

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", section=" + section +
                ", parts=" + parts.size() +
                '}';
    }

    public void addPart(Part part) {
        parts.add(part);
    }

    public void info() {
        System.out.println("\t\t\tLine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", section=" + section +
                ", parts=" + parts.size() +
                '}');
        for (Part part:parts ) {
            part.info();
        }
    }

    public List<Tp> getTps(){
        List<Tp> tpList = new ArrayList<>();
            for(Part part:this.getParts()){
                tpList.addAll(part.getTps());
            }
        return tpList;
    }
}
