package service;

import model.Role;
import java.util.*;

public class PermissionService {
    private Map<Role, Set<String>> permissions;

    public PermissionService() {
        initializePermissions();
    }

    private void initializePermissions() {
        permissions = new HashMap<>();

        // Admin: Full access
        permissions.put(Role.ADMIN, new HashSet<>(Arrays.asList(
                "ADD_CRIMINAL", "UPDATE_CRIMINAL", "DELETE_CRIMINAL", "SEARCH_CRIMINAL",
                "CREATE_CASE", "ASSIGN_CASE", "VIEW_CASE",
                "ADD_EVIDENCE", "VIEW_EVIDENCE",
                "STORE_BIOMETRIC", "VIEW_BIOMETRIC",
                "VIEW_REPORTS", "MANAGE_USERS", "VIEW_AUDIT"
        )));

        // Detective: Investigation access
        permissions.put(Role.DETECTIVE, new HashSet<>(Arrays.asList(
                "SEARCH_CRIMINAL", "VIEW_CRIMINAL",
                "CREATE_CASE", "ASSIGN_CASE", "VIEW_CASE", "UPDATE_CASE",
                "ADD_EVIDENCE", "VIEW_EVIDENCE",
                "STORE_BIOMETRIC", "VIEW_BIOMETRIC",
                "VIEW_REPORTS"
        )));

        // Officer: Limited access
        permissions.put(Role.OFFICER, new HashSet<>(Arrays.asList(
                "ADD_CRIMINAL", "SEARCH_CRIMINAL", "VIEW_CRIMINAL",
                "CREATE_CASE", "VIEW_CASE",
                "ADD_EVIDENCE", "VIEW_EVIDENCE",
                "STORE_BIOMETRIC", "VIEW_BIOMETRIC"
        )));

        // Analyst: View only
        permissions.put(Role.ANALYST, new HashSet<>(Arrays.asList(
                "SEARCH_CRIMINAL", "VIEW_CRIMINAL",
                "VIEW_CASE",
                "VIEW_EVIDENCE",
                "VIEW_BIOMETRIC",
                "VIEW_REPORTS"
        )));
    }

    public boolean hasPermission(Role role, String permission) {
        return permissions.getOrDefault(role, new HashSet<>()).contains(permission);
    }

    public Set<String> getPermissions(Role role) {
        return new HashSet<>(permissions.getOrDefault(role, new HashSet<>()));
    }
}
