package facade;

import controller.CaseController;
import controller.CriminalController;
import controller.LoginController;
import model.Criminal;
import java.util.Collection;

public class CriminalManagementFacade implements CriminalManagement {

    private final LoginController loginController;
    private final CriminalController criminalController;
    private final CaseController caseController;

    public CriminalManagementFacade() {
        this(new LoginController(), new CriminalController(), new CaseController());
    }

    public CriminalManagementFacade(LoginController loginController, CriminalController criminalController, CaseController caseController) {
        this.loginController = loginController;
        this.criminalController = criminalController;
        this.caseController = caseController;
    }

    @Override
    public boolean login(String username, String password) {
        return loginController.login(username, password);
    }

    @Override
    public boolean addCriminal(int id, String name, int age, String crime) {
        return criminalController.addCriminal(id, name, age, crime);
    }

    @Override
    public Criminal searchCriminal(int id) {
        return criminalController.searchCriminal(id);
    }

    @Override
    public boolean updateCrime(int id, String crime) {
        return criminalController.updateCrime(id, crime);
    }

    @Override
    public boolean deleteCriminal(int id) {
        return criminalController.deleteCriminal(id);
    }

    @Override
    public Collection<Criminal> getAllCriminals() {
        return criminalController.getAllCriminals();
    }

    public boolean createCase(int caseId, int criminalId, String description) {
        return caseController.createCase(caseId, criminalId, description);
    }
}