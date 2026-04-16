package service;

import model.WantedNotice;
import java.util.*;
import java.util.stream.Collectors;

public class AlertService {
    private List<WantedNotice> wantedList = Collections.synchronizedList(new ArrayList<>());
    private List<AlertListener> listeners = Collections.synchronizedList(new ArrayList<>());
    private int wantedIdCounter = 1000;

    public interface AlertListener {
        void onAlert(String message, String type);
    }

    public void addListener(AlertListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AlertListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String message, String type) {
        for (AlertListener listener : listeners) {
            listener.onAlert(message, type);
        }
    }

    public boolean addWantedNotice(int criminalId, String reason, String severity) {
        WantedNotice notice = new WantedNotice(wantedIdCounter++, criminalId, reason, severity);
        wantedList.add(notice);
        notifyListeners("Criminal ID " + criminalId + " added to wanted list - Severity: " + severity, "WANTED_ADDED");
        return true;
    }

    public boolean removeWantedNotice(int criminalId) {
        boolean removed = wantedList.removeIf(w -> w.getCriminalId() == criminalId && w.isActive());
        if (removed) {
            notifyListeners("Criminal ID " + criminalId + " removed from wanted list", "WANTED_REMOVED");
        }
        return removed;
    }

    public WantedNotice getWantedNotice(int criminalId) {
        return wantedList.stream()
                .filter(w -> w.getCriminalId() == criminalId && w.isActive())
                .findFirst()
                .orElse(null);
    }

    public List<WantedNotice> getAllWantedNotices() {
        return new ArrayList<>(wantedList.stream()
                .filter(WantedNotice::isActive)
                .collect(Collectors.toList()));
    }

    public void triggerAlert(int criminalId, String message) {
        if (getWantedNotice(criminalId) != null) {
            notifyListeners("ALERT: Wanted criminal ID " + criminalId + " - " + message, "WANTED_ALERT");
        }
    }
}
