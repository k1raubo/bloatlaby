package pl.edu.pg.eti.kask.pzw.user.entity;

import pl.edu.pg.eti.kask.pzw.permit.entity.Permit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private UUID id;

    private String login;

    private LocalDate birthday;

    private Role role;

    private List<Permit> permits = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Permit> getPermits() {
        return permits;
    }

    public void setPermits(List<Permit> permits) {
        this.permits = permits;
    }

}
