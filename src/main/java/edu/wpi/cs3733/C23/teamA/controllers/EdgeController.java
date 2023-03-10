package edu.wpi.cs3733.C23.teamA.controllers;

import edu.wpi.cs3733.C23.teamA.Database.API.FacadeRepository;
import edu.wpi.cs3733.C23.teamA.Database.Entities.EdgeEntity;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class EdgeController extends MenuController {

  @FXML private TableView<EdgeEntity> dbTable;

  @FXML public TableColumn<EdgeEntity, String> edgeIDCol;
  @FXML public TableColumn<EdgeEntity, String> startNodeCol;
  @FXML public TableColumn<EdgeEntity, String> endNodeCol;

  @FXML public MFXButton refresh;
  private List<EdgeEntity> data;

  private ObservableList<EdgeEntity> dbTableRowsModel = FXCollections.observableArrayList();
  /** runs on switching to this scene */
  public void initialize() {
    reloadData();

    edgeIDCol.setCellValueFactory(new PropertyValueFactory<>("edgeid"));
    startNodeCol.setCellValueFactory(
        param -> new SimpleStringProperty(param.getValue().getNode1().getNodeid()));
    endNodeCol.setCellValueFactory(
        param -> new SimpleStringProperty(param.getValue().getNode2().getNodeid()));
    dbTable.setItems(dbTableRowsModel);

    editableColumns();
    dbTable.setEditable(true);
  }

  // Loads data from the database into the list
  public void reloadData() {
    dbTableRowsModel.clear();
    try {
      data = FacadeRepository.getInstance().getAllEdge();
      dbTableRowsModel.addAll(data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void editableColumns() {
    startNodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
    endNodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
    startNodeCol.setOnEditCommit(
        e -> {
          EdgeEntity n = e.getTableView().getItems().get(e.getTablePosition().getRow());
          try {
            n.setNode1(FacadeRepository.getInstance().getNode(e.getNewValue()));
            FacadeRepository.getInstance().updateEdge(n.getEdgeid(), n);

          } catch (Exception ex) {
            refresh.setText("Invalid Node: Refresh");
          }
        });
    endNodeCol.setOnEditCommit(
        e -> {
          EdgeEntity n = e.getTableView().getItems().get(e.getTablePosition().getRow());
          try {

            n.setNode2(FacadeRepository.getInstance().getNode(e.getNewValue()));
            FacadeRepository.getInstance().updateEdge(n.getEdgeid(), n);

          } catch (Exception ex) {
            refresh.setText("Invalid Node: Refresh");
          }
        });
  }
}
