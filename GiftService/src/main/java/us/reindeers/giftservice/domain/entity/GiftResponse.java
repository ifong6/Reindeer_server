package us.reindeers.giftservice.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gift_response")
@Data
public class GiftResponse {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "note_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID noteId;

    @Column(name = "donation_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID donationId;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "note_created")
    private LocalDateTime noteCreated;

    @Column(name = "note_updated")
    private LocalDateTime noteUpdated;

    @PreUpdate
    protected void onUpdate(){
        this.noteUpdated = LocalDateTime.now();
    }

    @PrePersist
    protected void onChange(){
        this.noteCreated = LocalDateTime.now();
        this.noteUpdated = LocalDateTime.now();
    }
}

