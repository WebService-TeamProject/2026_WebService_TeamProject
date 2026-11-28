package org.example.jpabook.backend.account;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "maeum_users")
public class Account {
    @Id
    private Long id;
    private String email;
    private String passwordHash;

    protected Account() {
    }

    public String email() {
        return email;
    }

    String passwordHash() {
        return passwordHash;
    }
}
