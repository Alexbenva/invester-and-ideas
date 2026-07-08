package com.venturebridge.repository;

import com.venturebridge.entity.Notification;
import com.venturebridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedDateDesc(User user);

    long countByUserAndReadStatusFalse(User user);
}