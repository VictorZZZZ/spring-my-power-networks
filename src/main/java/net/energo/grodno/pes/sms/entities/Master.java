package net.energo.grodno.pes.sms.entities;

import javax.persistence.*;

@Entity
@DiscriminatorValue("master")
public class Master extends Abonent {
    @Column(name="position",columnDefinition = "varchar default 'Мастер'")
    private String position;

    @ManyToOne
    @JoinColumn(name="res_id")
    private Res res;

    public Master() {
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Res getRes() {
        return res;
    }

    public void setRes(Res res) {
        this.res = res;
    }
}
