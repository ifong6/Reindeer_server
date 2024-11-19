package us.reindeers.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "donor")
public class Donor {

    @Id
    @Column(name = "cognito_sub", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID sub;

    @MapsId
    @OneToOne
    @JoinColumn(name = "cognito_sub")
    private User user;

    @Column(name = "introduction", columnDefinition = "text")
    private String introduction;
}