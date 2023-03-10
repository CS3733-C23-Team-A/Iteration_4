package edu.wpi.cs3733.C23.teamA.controllers;

import edu.wpi.cs3733.C23.teamA.Database.API.FacadeRepository;
import edu.wpi.cs3733.C23.teamA.Database.Entities.NodeEntity;
import edu.wpi.cs3733.C23.teamA.pathfinding.enums.Building;
import edu.wpi.cs3733.C23.teamA.pathfinding.enums.Floor;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

public class NodeController extends MenuController {

  @FXML private TableView<NodeEntity> dbTable;

  @FXML public TableColumn<NodeEntity, String> nodeCol;
  @FXML public TableColumn<NodeEntity, Integer> xCol;
  @FXML public TableColumn<NodeEntity, Integer> yCol;
  @FXML public TableColumn<NodeEntity, String> floorCol;
  @FXML public TableColumn<NodeEntity, String> buildingCol;
  @FXML public MFXTextField xBox;
  @FXML public MFXTextField yBox;

  @FXML public MFXComboBox floorBox;
  @FXML public MFXComboBox buildingBox;

  @FXML public MFXButton createNode;
  @FXML public MFXButton deleteButton;

  private NodeEntity selected = null;
  private List<NodeEntity> data;

  private ObservableList<NodeEntity> dbTableRowsModel = FXCollections.observableArrayList();

  /** runs on switching to this scene */
  public void initialize() {
    reloadData();

    nodeCol.setCellValueFactory(new PropertyValueFactory<>("nodeid"));
    xCol.setCellValueFactory(new PropertyValueFactory<>("xcoord"));
    yCol.setCellValueFactory(new PropertyValueFactory<>("ycoord"));
    floorCol.setCellValueFactory(new PropertyValueFactory<>("floor"));
    buildingCol.setCellValueFactory(new PropertyValueFactory<>("building"));
    dbTable.setItems(dbTableRowsModel);

    nodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
    xCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    yCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    floorCol.setCellFactory(TextFieldTableCell.forTableColumn());
    buildingCol.setCellFactory(TextFieldTableCell.forTableColumn());

    ObservableList<String> floors =
        FXCollections.observableArrayList(
            Arrays.stream(Floor.values()).map(floor -> floor.getTableString()).toList());
    ObservableList<String> buildings =
        FXCollections.observableArrayList(
            Arrays.stream(Building.values()).map(building -> building.getTableString()).toList());
    floorBox.setItems(floors);
    buildingBox.setItems(buildings);
  }

  public void rowClicked() {
    selected = dbTable.getSelectionModel().getSelectedItem();
    deleteButton.setDisable(false);
    if (selected != null) {
      xBox.setText(String.valueOf(selected.getXcoord()));
      yBox.setText(String.valueOf(selected.getYcoord()));
      floorBox.setText(selected.getFloor());
      buildingBox.setText(selected.getBuilding());
    }
  }

  public void delete() {
    if (selected != null) {
      FacadeRepository.getInstance().collapseNode(selected);
      FacadeRepository.getInstance().deleteNode(selected.getNodeid());
      reloadData();
    }
  }

  public void onSubmit() {
    String x = xBox.getText().trim();
    String y = yBox.getText().trim();
    String floor = floorBox.getText().trim();
    String building = buildingBox.getText().trim();
    if (!x.isEmpty() && !y.isEmpty() && !floor.isEmpty() && !building.isEmpty()) {
      if (selected != null) {
        selected.setXcoord(Integer.parseInt(x));
        selected.setYcoord(Integer.parseInt(y));
        selected.setFloor(floor);
        selected.setBuilding(building);
        FacadeRepository.getInstance().updateNode(selected.getNodeid(), selected);
      } else {
        NodeEntity newNode =
            new NodeEntity(
                floor + "X" + x + "Y" + y,
                Integer.parseInt(x),
                Integer.parseInt(y),
                floor,
                building);

        List<NodeEntity> check = MapEditorController.getAllNodes();
        boolean noDuplicates = true;
        for (int i = 0; i < check.size(); i++) {
          if (newNode.getNodeid().equals(check.get(i).getNodeid())) {
            noDuplicates = false;
          }
        }
        if (noDuplicates == true) {
          FacadeRepository.getInstance().addNode(newNode);
        } else {
          System.out.println("there is a duplicate");
        }
      }
    }
    reloadData();
  }

  public void clearEdits() {
    xBox.clear();
    yBox.clear();
    floorBox.clear();
    buildingBox.clear();
  }

  /** Clear and retrieve all table rows again with hibernate only use once at start */
  public void reloadData() {
    clearEdits();
    selected = null;
    deleteButton.setDisable(true);
    dbTableRowsModel.clear();
    try {
      data = FacadeRepository.getInstance().getAllNode();
      dbTableRowsModel.addAll(data);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
