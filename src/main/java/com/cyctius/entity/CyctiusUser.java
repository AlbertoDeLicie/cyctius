package com.cyctius.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cyctius_users", schema = "cyctius_db_schema")
public class CyctiusUser extends Auditable {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String userId;
    @Column(name = "issuer_id", nullable = false, unique = true)
    private String issuerId;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "mobile", nullable = false, unique = true)
    private String mobile;
    @Column(name = "ftp")
    private Long ftp;
    @Column(name = "max_hr")
    private Long maxHR;
    @Column(name = "rest_hr")
    private Long restHR;
}
