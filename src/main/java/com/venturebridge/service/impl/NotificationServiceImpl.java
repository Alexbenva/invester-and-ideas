package com.venturebridge.service.impl;

import com.venturebridge.entity.Notification;
import com.venturebridge.entity.User;
import com.venturebridge.exception.ResourceNotFoundException;
import com.venturebridge.repository.NotificationRepository;
import com.venturebridge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification create(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setReadStatus(false);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findForUser(User user) {
        return notificationRepository.findByUserOrderByCreatedDateDesc(user);
    }

    @Override
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Notification does not belong to current user");
        }
        notification.setReadStatus(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(User user) {
        return notificationRepository.countByUserAndReadStatusFalse(user);
    }
}