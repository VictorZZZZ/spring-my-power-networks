package net.energo.grodno.pes.smsSender.entities;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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

    @OneToMany(mappedBy = "res")
    private List<Tp> tps;

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
        return tps;
    }

    public void setTps(List<Tp> tps) {
        this.tps = tps;
    }
}
