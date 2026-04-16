package service;

import model.Notification;
import java.util.*;
import java.util.stream.Collectors;

public class NotificationService {
    private List<Notification> notifications = Collections.synchronizedList(new ArrayList<>());
    private List<NotificationListener> listeners = Collections.synchronizedList(new ArrayList<>());
    private int notificationIdCounter = 1;

    public interface NotificationListener {
        void onNewNotification(Notification notification);
    }

    public void addListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Notification notification) {
        for (NotificationListener listener : listeners) {
            listener.onNewNotification(notification);
        }
    }

    public Notification createNotification(String title, String message, String type, String recipient) {
        Notification notif = new Notification(notificationIdCounter++, title, message, type, recipient);
        notifications.add(notif);
        notifyListeners(notif);
        return notif;
    }

    public List<Notification> getNotificationsForUser(String recipient) {
        return new ArrayList<>(notifications.stream()
                .filter(n -> n.getRecipient().equals(recipient))
                .collect(Collectors.toList()));
    }

    public List<Notification> getUnreadNotifications(String recipient) {
        return new ArrayList<>(notifications.stream()
                .filter(n -> n.getRecipient().equals(recipient) && !n.isRead())
                .collect(Collectors.toList()));
    }

    public void markAsRead(int notificationId) {
        notifications.stream()
                .filter(n -> n.getNotificationId() == notificationId)
                .findFirst()
                .ifPresent(Notification::markAsRead);
    }

    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications);
    }

    public int getUnreadCount(String recipient) {
        return (int) notifications.stream()
                .filter(n -> n.getRecipient().equals(recipient) && !n.isRead())
                .count();
    }
}
