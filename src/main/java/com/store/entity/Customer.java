package com.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "sales")
@EqualsAndHashCode(exclude = "sales")
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sale> sales = new ArrayList<>();

    public Customer(String name, String lastName, String email) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
    }
}
