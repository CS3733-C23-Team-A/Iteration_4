package edu.wpi.cs3733.C23.teamA.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class DisplayServiceRequestsPopupController {
  @FXML private Text req1;
  @FXML private Text req2;
  @FXML private Text req3;
  @FXML private Text req4;
  @FXML private MFXButton closeButton;
  private Text[] labels = new Text[4];

  @FXML
  public void initialize() {
    labels[0] = req1;
    labels[1] = req2;
    labels[2] = req3;
    labels[3] = req4;
  }

  @FXML
  public void closePopup(ActionEvent event) {}

  void closePopup(String[] serviceRequests) {
    int size = serviceRequests.length;
    for (int i = 0; i < Math.min(size, 4); i++) {
      labels[i].setText(serviceRequests[i]);
    }
  }
}
