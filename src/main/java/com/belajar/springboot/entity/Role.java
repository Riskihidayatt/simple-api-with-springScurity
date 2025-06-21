package com.belajar.springboot.entity;

import com.belajar.springboot.constant.RoleType;
import com.belajar.springboot.constant.TableNames;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Table(name = TableNames.ROLES)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RoleType name;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

}
