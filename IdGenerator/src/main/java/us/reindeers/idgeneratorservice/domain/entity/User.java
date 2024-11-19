package us.reindeers.idgeneratorservice.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "varchar(10)")
    private String userId;
}
