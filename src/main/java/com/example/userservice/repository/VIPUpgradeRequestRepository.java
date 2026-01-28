package com.example.userservice.repository;

import com.example.userservice.entity.VIPUpgradeRequest;
import com.example.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VIPUpgradeRequestRepository extends JpaRepository<VIPUpgradeRequest, Long> {

    List<VIPUpgradeRequest> findByStatus(VIPUpgradeRequest.RequestStatus status);

    List<VIPUpgradeRequest> findByUserOrderByRequestDateDesc(User user);

    Optional<VIPUpgradeRequest> findByUserAndStatus(User user, VIPUpgradeRequest.RequestStatus status);

    List<VIPUpgradeRequest> findAllByOrderByRequestDateDesc();
}
