package us.reindeers.userservice.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table( name = "id_pool")
@Data
public class IdPool {

    @Id
    @Column(nullable = false, columnDefinition = "varchar(10)")
    String id;
}
