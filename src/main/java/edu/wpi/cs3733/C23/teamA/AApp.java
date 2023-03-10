package edu.wpi.cs3733.C23.teamA;

import static edu.wpi.cs3733.C23.teamA.Database.API.ADBSingletonClass.getSessionFactory;

import edu.wpi.cs3733.C23.teamA.Database.API.FacadeRepository;
import edu.wpi.cs3733.C23.teamA.navigation.Navigation;
import edu.wpi.cs3733.C23.teamA.navigation.Screen;
import java.awt.*;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AApp extends Application {

  @Setter @Getter private static Stage primaryStage;
  @Setter @Getter private static BorderPane rootPane;

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    /* primaryStage is generally only used if one of your components require the stage to display */
    AApp.primaryStage = primaryStage;

    // the following is the ROOT: !!!!! NEVER EVER CHANGE THIS TO ANYTHING OTHER THAN
    // "views/Root.fxml" !!!!!
    final FXMLLoader loader = new FXMLLoader(AApp.class.getResource("views/Root.fxml"));
    final BorderPane root = loader.load();

    AApp.rootPane = root;

    // load images
    ImageLoader imgLoader = new ImageLoader();
    imgLoader.loadImages();
    FacadeRepository.getInstance(); // wake up database instances

    // add the icon and name to the application
    var icon =
        new Image("edu/wpi/cs3733/C23/teamA/assets/Brigham_and_Womens_Hospital_logo_iconSize.png");
    primaryStage.getIcons().add(icon);
    primaryStage.setTitle("CS3733-C23: Team A");

    final Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();
    primaryStage.setMaximized(true);
    Navigation.navigateHome(Screen.LOGIN);
    primaryStage.setMinWidth(615);
    primaryStage.setMinHeight(450);
  }

  @Override
  public void stop() {
    // ServiceRequestController.setStop(true);
    getSessionFactory().close();
    log.info("Shutting Down");
    System.exit(0);
  }
}
