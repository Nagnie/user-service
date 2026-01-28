package com.example.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vip_upgrade_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VIPUpgradeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer requestedMonths;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime requestDate = LocalDateTime.now();

    private LocalDateTime processedDate;

    @Column(name = "processed_by")
    private Long processedBy;

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
