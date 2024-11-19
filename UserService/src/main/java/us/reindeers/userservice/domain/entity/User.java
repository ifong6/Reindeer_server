package us.reindeers.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "cognito_sub"))
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "cognito_sub", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID sub;

    @Column(name = "user_id", columnDefinition = "varchar(10)")
    private String userId;

    @Column(name = "username", nullable = false, columnDefinition = "varchar(50)")
    private String username;

    @Column(name = "email", nullable = false, columnDefinition = "varchar(50)")
    private String email;

    @Column(name = "avatar_url", columnDefinition = "varchar(255)")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleEnum role = RoleEnum.DEFAULT;

    @OneToOne(mappedBy = "user")
    private CareAgency careAgency;

    @OneToOne(mappedBy = "user")
    private Recipient recipient;

    @OneToOne(mappedBy = "user")
    private Donor donor;

    @Column(name = "account_created", updatable = false, nullable = false)
    private LocalDateTime accountCreated;

    @Column(name = "account_updated", nullable = false)
    private LocalDateTime accountUpdated;
}
