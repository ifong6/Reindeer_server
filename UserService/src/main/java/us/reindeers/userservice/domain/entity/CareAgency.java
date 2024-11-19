package us.reindeers.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "care_agency")
public class CareAgency {
    @Id
    @Column(name = "cognito_sub", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID sub;

    @MapsId
    @OneToOne
    @JoinColumn(name = "cognito_sub")
    private User user;

    @Column(name = "address", columnDefinition = "varchar(255)")
    private String address;

    @Column(name = "phone", columnDefinition = "varchar(20)")
    private String phone;

    @Column(name = "website", columnDefinition = "varchar(100)")
    private String website;

    @Column(name = "descritpion", columnDefinition = "varchar(255)")
    private String description;

    @Column(name = "is_verified", columnDefinition = "bool")
    private boolean isVerified;

}
