package app;

import facade.CriminalManagement;
import facade.CriminalManagementFacade;

public final class AppContext {

    private static final AppContext INSTANCE = new AppContext();

    private final CriminalManagement criminalManagement = new CriminalManagementFacade();

    private AppContext() {
    }

    public static AppContext getInstance() {
        return INSTANCE;
    }

    public CriminalManagement getCriminalManagement() {
        return criminalManagement;
    }
}