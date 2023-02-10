package edu.wpi.cs3733.C23.teamA.controllers;

import static edu.wpi.cs3733.C23.teamA.hibernateDB.ADBSingletonClass.getAllRecords;
import static edu.wpi.cs3733.C23.teamA.hibernateDB.ADBSingletonClass.getSessionFactory;
import static edu.wpi.cs3733.C23.teamA.hibernateDB.ServiceRequestEntity.getServiceByEmployee;

import edu.wpi.cs3733.C23.teamA.enums.FormType;
import edu.wpi.cs3733.C23.teamA.enums.Status;
import edu.wpi.cs3733.C23.teamA.enums.UrgencyLevel;
import edu.wpi.cs3733.C23.teamA.hibernateDB.ServiceRequestEntity;
import edu.wpi.cs3733.C23.teamA.navigation.Navigation;
import edu.wpi.cs3733.C23.teamA.navigation.Screen;
import edu.wpi.cs3733.C23.teamA.serviceRequests.*;
import edu.wpi.cs3733.C23.teamA.serviceRequests.IdNumberHolder;
import edu.wpi.cs3733.C23.teamA.serviceRequests.ServiceRequestTableRow;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ServiceRequestStatusController extends MenuController {

  @FXML private TableView<ServiceRequestEntity> serviceReqsTable;
  @FXML public TableColumn<ServiceRequestEntity, Integer> IDCol;
  @FXML public TableColumn<ServiceRequestEntity, String> formTypeCol;
  @FXML public TableColumn<ServiceRequestEntity, String> dateCol;
  @FXML public TableColumn<ServiceRequestEntity, String> statusCol;
  @FXML public TableColumn<ServiceRequestEntity, String> urgencyCol;
  @FXML public TableColumn<ServiceRequestEntity, String> employeeAssignedCol;

  // text boxes for editing
  @FXML public MFXComboBox<String> formTypeBox;
  @FXML public MFXTextField dateBox; // make a MFXDatePicker ?????? or not??
  @FXML public MFXComboBox<String> employeeBox;
  @FXML public MFXComboBox<String> statusBox;
  @FXML public Text IDBoxSaver;
  @FXML private MFXButton editForm;
  UrgencyLevel urgent;
  Status status;

  public static EditTheForm newEdit = new EditTheForm(0, "", false);

  private String hospitalID;
  private String job;

  @FXML
  public void switchToHomeScene(ActionEvent event) throws IOException {
    Navigation.navigate(Screen.HOME);
  }

  private ObservableList<ServiceRequestEntity> dbTableRowsModel =
      FXCollections.observableArrayList();

  @FXML
  public void initialize() throws SQLException {

    IdNumberHolder holder = IdNumberHolder.getInstance();
    hospitalID = holder.getId();
    job = holder.getJob();

    // Assign permissions to differentiate between medical and non-medical staff
    if (job.equalsIgnoreCase("medical")) {
      statusBox.setDisable(true);
      employeeBox.setDisable(true);
      formTypeBox.setDisable(true);
      dateBox.setDisable(true);
      urgencyBox.setDisable(false);

    } else {
      statusBox.setDisable(false);
      employeeBox.setDisable(false);
      formTypeBox.setDisable(true);
      dateBox.setDisable(true);
      urgencyBox.setDisable(true);
    }

    IDCol.setCellValueFactory(new PropertyValueFactory<>("requestid"));
    formTypeCol.setCellValueFactory(
        param -> new SimpleStringProperty(param.getValue().getRequestType().requestType));
    dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
    statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    urgencyCol.setCellValueFactory(new PropertyValueFactory<>("urgency"));
    employeeAssignedCol.setCellValueFactory(new PropertyValueFactory<>("employeeAssigned"));

    Session session = getSessionFactory().openSession();
    List<ServiceRequestEntity> requests = new ArrayList<ServiceRequestEntity>();

    if (job.equalsIgnoreCase("medical")) {
      requests = getServiceByEmployee(hospitalID, session);
    } else {
      requests = getAllRecords(ServiceRequestEntity.class, session);
    }

    dbTableRowsModel.addAll(requests);

    serviceReqsTable.setItems(dbTableRowsModel);
    session.close();
  }

  @FXML
  public void rowClicked(MouseEvent event) {

    ServiceRequestEntity clickedServiceReqTableRow =
        serviceReqsTable.getSelectionModel().getSelectedItem();

    if (clickedServiceReqTableRow != null) {

      IDBoxSaver.setText(String.valueOf(clickedServiceReqTableRow.getRequestid()));
      formTypeBox.setText(String.valueOf(clickedServiceReqTableRow.getRequestType()));
      dateBox.setText(String.valueOf(clickedServiceReqTableRow.getDate()));
      statusBox.setText(String.valueOf(clickedServiceReqTableRow.getStatus()));
      urgencyBox.setText(String.valueOf(clickedServiceReqTableRow.getUrgency()));
      employeeBox.setText(String.valueOf(clickedServiceReqTableRow.getEmployeeAssigned()));
      if (job.equalsIgnoreCase("medical")) {
        editForm.setVisible(true);
      }
    }

    ObservableList<String> statuses = FXCollections.observableArrayList(Status.statusList());

    ObservableList<String> urgencies =
        FXCollections.observableArrayList(UrgencyLevel.urgencyList());

    ObservableList<String> formTypes = FXCollections.observableArrayList(FormType.typeList());

    ObservableList<String> employees =
        FXCollections.observableArrayList(
            "Izzy",
            "Isabella",
            "Andrei",
            "Harrison",
            "John",
            "Chris",
            "Steve",
            "Hazel",
            "Audrey",
            "Sarah");

    statusBox.setItems(statuses);
    urgencyBox.setItems(urgencies);
    formTypeBox.setItems(formTypes);
    employeeBox.setItems(employees);
  }

  @FXML
  public void submitEdit(ActionEvent event) throws IOException, SQLException, ParseException {

    if (!statusBox.getText().trim().isEmpty()
        && !dateBox.getText().trim().isEmpty()
        && !formTypeBox.getText().trim().isEmpty()
        && !urgencyBox.getText().trim().isEmpty()
        && !employeeBox.getText().trim().isEmpty()) {

      ObservableList<ServiceRequestEntity> currentTableData = serviceReqsTable.getItems();
      int currentRowId = Integer.parseInt(IDBoxSaver.getText());

      for (ServiceRequestEntity SRTable : currentTableData) {
        if (SRTable.getRequestid() == currentRowId) {
          SRTable.setRequestid(Integer.parseInt(IDBoxSaver.getText()));
          SRTable.setRequestType(ServiceRequestEntity.RequestType.valueOf(formTypeBox.getText()));
          SRTable.setDate(Timestamp.valueOf(dateBox.getText()));
          SRTable.setStatus(Status.valueOf(statusBox.getText()));
          SRTable.setUrgency(UrgencyLevel.value(urgencyBox.getText()));
          SRTable.setEmployeeAssigned(employeeBox.getText());

          serviceReqsTable.setItems(currentTableData);
          serviceReqsTable.refresh();
          Session session = getSessionFactory().openSession();
          Transaction tx = session.beginTransaction();
          ServiceRequestEntity billy = session.get(ServiceRequestEntity.class, currentRowId);

          if (statusBox != null && !statusBox.isDisabled()) {
            status = Status.valueOf(statusBox.getValue());
            billy.setStatus(status);
          }
          if (urgencyBox != null && !urgencyBox.isDisabled()) {
            urgent = UrgencyLevel.value(urgencyBox.getValue());
            billy.setUrgency(urgent);
          }
          billy.setEmployeeAssigned(employeeBox.getText());
          tx.commit();
          session.close();
          break;
        }
      }
    }
  }

  @FXML
  public void submitRequest(ActionEvent event) {}

  public void clearEdits(ActionEvent event) {
    IDBoxSaver.setText("");
    formTypeBox.clear();
    dateBox.clear();
    statusBox.clear();
    urgencyBox.clear();
    employeeBox.clear();
  }

  public void editForm(ActionEvent event) {
    ServiceRequestEntity clickedRow = serviceReqsTable.getSelectionModel().getSelectedItem();
    newEdit =
        new EditTheForm(clickedRow.getRequestid(), clickedRow.getRequestType().requestType, true);
    switch (clickedRow.getRequestType().requestType) {
      case "COMPUTER":
        Navigation.navigate(Screen.COMPUTER);
        break;
      case "SANITATION":
        Navigation.navigate(Screen.SANITATION);
        break;
      case "SECURITY":
        Navigation.navigate(Screen.SECURITY);
        break;
      default:
        Navigation.navigate(Screen.HOME);
        break;
    }
  }
}
