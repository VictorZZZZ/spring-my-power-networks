package net.energo.grodno.pes.sms.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("leader")
public class Lead extends Abonent {
    @Column(name="position")
    private String position;

    public Lead() {
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
