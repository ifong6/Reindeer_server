package us.reindeers.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "recipient")
public class Recipient {

    @Id
    @Column(name = "cognito_sub", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID sub;

    @MapsId
    @OneToOne
    @JoinColumn(name = "cognito_sub")
    private User user;

    @Column(name = "age", columnDefinition = "integer")
    private Integer age;

    @Column(name = "address", columnDefinition = "varchar(255)")
    private String address;

    @Column(name = "interest", columnDefinition = "varchar(100)")
    private String interest;

    @Column(name = "story", columnDefinition = "text")
    private String story;
}

