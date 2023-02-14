package edu.wpi.cs3733.C23.teamA.controllers;

import static edu.wpi.cs3733.C23.teamA.controllers.ServiceRequestStatusController.newEdit;

import edu.wpi.cs3733.C23.teamA.Database.Entities.PatientTransportRequestEntity;
import edu.wpi.cs3733.C23.teamA.Database.Implementation.PatientTransportimpl;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.sql.SQLException;
import javafx.fxml.FXML;

public class PatientTransportController extends ServiceRequestController {
  @FXML private MFXTextField pNameBox;
  @FXML private MFXComboBox<String> moveFromBox;

  @FXML
  public void initialize() throws SQLException {
    super.initialize();
    // If Edit past submissions is pressed. Open Service request with form fields filled out.
    if (newEdit.needEdits && newEdit.getRequestType().equals("PatientTransport")) {
      PatientTransportimpl patI = new PatientTransportimpl();
      PatientTransportRequestEntity editPatientRequest = patI.get(newEdit.getRequestID());
      nameBox.setText(editPatientRequest.getName());
      IDNum.setText(editPatientRequest.getEmployee().getEmployeeid());
      pNameBox.setText(editPatientRequest.getPatientName());
      moveFromBox.setText(editPatientRequest.getLocation().getLongname());
      urgencyBox.setText(editPatientRequest.getUrgency().getUrgency()); // Double check
      descBox.setText(editPatientRequest.getDescription());
      // patI.closeSession();
    }
    // Otherwise Initialize service requests as normal
  }
}
