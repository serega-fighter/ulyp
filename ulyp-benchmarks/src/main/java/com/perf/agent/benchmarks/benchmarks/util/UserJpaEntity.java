package com.perf.agent.benchmarks.benchmarks.util;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserJpaEntity {

    @Id
    @GeneratedValue
    private int id;
    private String firstname;
    private String lastname;

    public UserJpaEntity() {
    }

    public UserJpaEntity(final String firstname, final String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public UserJpaEntity(final User user) {
        this.id = user.getId();
        this.firstname = user.getFirstName();
        this.lastname = user.getLastName();
    }

    // getters

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public int getId() {
        return id;
    }

    // setters

    public void setId(int id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    // type conversion

    public User toUser() {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        return user;
    }
}
