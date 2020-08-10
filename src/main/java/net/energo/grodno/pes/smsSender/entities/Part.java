package net.energo.grodno.pes.smsSender.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name="part")
public class Part {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name="line_id")
    private Line line;

    @OneToMany(mappedBy = "part")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private List<Tp> tps;

    public Part() {
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

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public List<Tp> getTps() {
        return tps;
    }

    public void setTps(List<Tp> tps) {
        this.tps = tps;
    }

    @Override
    public String toString() {
        return "Part{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", line=" + line +
                ", tps=" + tps.size() +
                '}';
    }

    public void addTp(Tp tp) {
        tps.add(tp);
    }

    public void info() {
        System.out.println("\t\t\t\tPart{" +
                            "id=" + id +
                            ", name='" + name + '\'' +
                            ", line=" + line +
                            ", tps=" + tps.size() +
                            "}");
        for (Tp tp: tps ) {
            tp.info();
        }
    }
}
