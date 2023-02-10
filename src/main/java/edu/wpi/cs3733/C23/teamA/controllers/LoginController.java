package edu.wpi.cs3733.C23.teamA.controllers;

import edu.wpi.cs3733.C23.teamA.Database.API.ADBSingletonClass;
import edu.wpi.cs3733.C23.teamA.Database.Entities.EmployeeEntity;
import edu.wpi.cs3733.C23.teamA.navigation.Navigation;
import edu.wpi.cs3733.C23.teamA.navigation.Screen;
import edu.wpi.cs3733.C23.teamA.serviceRequests.IdNumberHolder;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import org.hibernate.Session;

import static edu.wpi.cs3733.C23.teamA.Database.Implementation.EmployeeImpl.checkPass;

public class LoginController {

  @FXML MFXTextField usernameTextField;
  @FXML MFXPasswordField passwordTextField;
  @FXML Text incorrectNotification;
  EmployeeEntity employee = new EmployeeEntity();

  @FXML
  public void initialize() {
    incorrectNotification.setVisible(false);
  }

  @FXML
  public void logout(ActionEvent event) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Logout");
    alert.setHeaderText("Are you sure you want to leave the app?");

    if (alert.showAndWait().get() == ButtonType.OK) {
      System.out.println("You have successfully logged out!");
      Navigation.close(); // MAY NOT FUCKING WORK
    }
  }

  @FXML
  public void login(ActionEvent event) throws SQLException {

    Session session = ADBSingletonClass.getSessionFactory().openSession();
    // Transaction tx = session.beginTransaction();

    ArrayList<String> info =
        checkPass(usernameTextField.getText(), passwordTextField.getText());

    if (info.get(0).equals("")) {
      incorrectNotification.setVisible(true);
    } else {
      IdNumberHolder holder = IdNumberHolder.getInstance();
      holder.setId(info.get(0));
      holder.setUsername(usernameTextField.getText());
      holder.setPassword(passwordTextField.getText());
      holder.setJob(info.get(1));
      holder.setName(info.get(2));
      session.close();
      Navigation.navigate(Screen.HOME);
    }
    session.close();
  }
}
