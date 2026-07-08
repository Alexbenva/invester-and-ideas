package com.venturebridge.service;

import com.venturebridge.entity.Notification;
import com.venturebridge.entity.User;

import java.util.List;

public interface NotificationService {

    Notification create(User user, String message);

    List<Notification> findForUser(User user);

    void markAsRead(Long notificationId, User user);

    long countUnread(User user);
}