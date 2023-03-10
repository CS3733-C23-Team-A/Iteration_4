package edu.wpi.cs3733.C23.teamA.controllers;

import edu.wpi.cs3733.C23.teamA.Database.API.FacadeRepository;
import edu.wpi.cs3733.C23.teamA.Database.Entities.EmployeeEntity;
import edu.wpi.cs3733.C23.teamA.enums.Job;
import edu.wpi.cs3733.C23.teamA.navigation.Navigation;
import edu.wpi.cs3733.C23.teamA.navigation.Screen;
import edu.wpi.cs3733.C23.teamA.serviceRequests.IdNumberHolder;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;

public class EmployeeController extends MenuController {

  @FXML private TableView<EmployeeEntity> employeeTable;
  @FXML public TableColumn<EmployeeEntity, Integer> IDCol;

  @FXML public TableColumn<EmployeeEntity, String> nameCol;
  @FXML public TableColumn<EmployeeEntity, String> employeeCol;
  @FXML public TableColumn<EmployeeEntity, String> usernameCol;
  @FXML public TableColumn<EmployeeEntity, String> passwordCol;
  @FXML public TableColumn<EmployeeEntity, String> jobCol;

  // text boxes for editing
  @FXML public MFXTextField IDNumBox;
  @FXML public Text IDBoxSaver;

  @FXML public MFXTextField nameBox;
  @FXML public MFXTextField usernameBox;
  @FXML public MFXTextField passwordBox;
  @FXML public MFXComboBox<String> jobBox;
  @FXML private MFXButton editButton;
  @FXML private MFXButton deleteButton;
  @FXML private MFXButton createEmployee;
  @FXML private Text reminder;
  @FXML private StackPane reminderPane;
  @FXML private MFXTextField fileNameField;
  private static PopOver popup;
  @FXML private MFXButton cancel;

  private String hospitalID;
  private String job;

  private ObservableList<EmployeeEntity> dbTableRowsModel = FXCollections.observableArrayList();
  List<EmployeeEntity> employeeData = new ArrayList<>();

  @FXML
  public void initialize() throws SQLException {

    IdNumberHolder holder = IdNumberHolder.getInstance();
    hospitalID = holder.getId();
    job = holder.getJob();

    employeeData = FacadeRepository.getInstance().getAllEmployee();

    if (nameCol != null) {
      IDCol.setCellValueFactory(new PropertyValueFactory<>("employeeid"));
      nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
      employeeCol.setCellValueFactory(new PropertyValueFactory<>("hospitalid"));
      usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
      passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
      jobCol.setCellValueFactory(new PropertyValueFactory<>("job"));

      dbTableRowsModel.addAll(employeeData);
      employeeTable.setItems(dbTableRowsModel);
    }
    ObservableList<String> jobs = FXCollections.observableArrayList(Job.jobList());
    jobBox.setItems(jobs);
    // Assign permissions to differentiate between medical and non-medical staff

  }

  @FXML
  public void rowClicked(MouseEvent event) {

    EmployeeEntity clickedEmployeeTableRow = employeeTable.getSelectionModel().getSelectedItem();

    if (clickedEmployeeTableRow != null) {
      IDBoxSaver.setText(String.valueOf(clickedEmployeeTableRow.getEmployeeid()));
      nameBox.setText(String.valueOf(clickedEmployeeTableRow.getName()));
      IDNumBox.setText(String.valueOf(clickedEmployeeTableRow.getHospitalid()));
      usernameBox.setText(String.valueOf(clickedEmployeeTableRow.getUsername()));
      passwordBox.setText(String.valueOf(clickedEmployeeTableRow.getPassword()));
      jobBox.setValue(String.valueOf(clickedEmployeeTableRow.getJob()));
    }
    editButton.setDisable(false);
    deleteButton.setDisable(false);
    createEmployee.setVisible(false);

    ObservableList<String> jobs = FXCollections.observableArrayList(Job.jobList());
    jobBox.setItems(jobs);
  }

  @FXML
  public void createEmployee(ActionEvent event) {
    EmployeeEntity theEmployee =
        new EmployeeEntity(
            IDNumBox.getText(),
            usernameBox.getText(),
            passwordBox.getText(),
            jobBox.getValue(),
            nameBox.getText());

    FacadeRepository.getInstance().addEmployee(theEmployee);
    reloadData();
  }

  @FXML
  public void delete(ActionEvent event) {
    int currentRowId = Integer.parseInt(IDBoxSaver.getText());
    FacadeRepository.getInstance().deleteEmployee(currentRowId);
    reloadData();
  }

  public void reloadData() {
    dbTableRowsModel.clear();
    try {
      employeeData = FacadeRepository.getInstance().getAllEmployee();
      dbTableRowsModel.addAll(employeeData);
      clearEdits();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @FXML
  public void submitEdit(ActionEvent event) throws IOException, SQLException, ParseException {

    if (!IDNumBox.getText().trim().isEmpty()
        && !nameBox.getText().trim().isEmpty()
        && !usernameBox.getText().trim().isEmpty()
        && !passwordBox.getText().trim().isEmpty()) {

      ObservableList<EmployeeEntity> currentTableData = employeeTable.getItems();

      int currentRowId = Integer.parseInt(IDBoxSaver.getText());

      for (EmployeeEntity employees : currentTableData) {
        if (employees.getEmployeeid() == (currentRowId)) {

          employees.setName(nameBox.getText());
          employees.setUsername(usernameBox.getText());
          employees.setPassword(passwordBox.getText());
          employees.setJob(Job.value(jobBox.getValue()).getJob());
          employees.setHospitalid(IDNumBox.getText());
          FacadeRepository.getInstance().updateEmployee(currentRowId, employees);
          employeeTable.setItems(currentTableData);
          reloadData();
          break;
        }
      }
    }
  }

  @FXML
  public void submitRequest(ActionEvent event) {}

  public void clearEdits() {
    nameBox.clear();
    IDNumBox.clear();
    usernameBox.clear();
    passwordBox.clear();
    jobBox.clear();

    createEmployee.setVisible(true);
    editButton.setDisable(true);
  }

  //  @FXML
  //  void clearForm(ActionEvent event) {
  //    fileNameField.clear();
  //  }

  @FXML
  public void switchToImport(ActionEvent event) throws IOException {
    Navigation.navigateHome(Screen.IMPORT_CSV);
  }

  public void switchToExport(ActionEvent event) throws IOException {
    Navigation.navigateHome(Screen.EXPORT_CSV);
  }
}
