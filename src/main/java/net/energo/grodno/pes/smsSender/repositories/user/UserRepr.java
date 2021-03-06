package net.energo.grodno.pes.smsSender.repositories.user;

import net.energo.grodno.pes.smsSender.entities.Res;
import net.energo.grodno.pes.smsSender.entities.users.Role;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class UserRepr {
    private Long id;

    @NotBlank
    @Unique
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String repeatPassword;

    private List<Role> roles;

    private Res res;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public Res getRes() {
        return res;
    }

    public void setRes(Res res) {
        this.res = res;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserRepr{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", repeatPassword='" + repeatPassword + '\'' +
                ", roles=" + roles +
                ", res=" + res +
                '}';
    }
}
