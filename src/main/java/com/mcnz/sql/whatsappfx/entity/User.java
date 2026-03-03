
package com.mcnz.sql.whatsappfx.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Le nom D'utilisateur obligatoire")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Mot de passe obligatoire")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)

    @Builder.Default
    private Status status = Status.OFFLINE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    public enum Status {
        ONLINE,
        OFFLINE
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = Status.OFFLINE;
    }
}
