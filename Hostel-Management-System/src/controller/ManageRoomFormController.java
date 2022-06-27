package controller;

import bo.BOFactory;
import bo.custom.ManageRoomBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import dto.RoomDTO;
import dto.StudentDTO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import view.tm.RoomTM;
import view.tm.StudentTM;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ManageRoomFormController {
    public AnchorPane ManageRoomFormContext;
    public Label lblDate;
    public Label lblTime;
    public JFXTextField txtRoomID;
    public JFXTextField txtKeyMoney;
    public JFXTextField txtQty;
    public JFXButton btnSave;
    public JFXComboBox<String> cmbRoomType;
    public JFXButton btnDelete;
    public TableView<RoomTM> tblRoom;
    public TableColumn colRoomID;
    public TableColumn colRoomType;
    public TableColumn colKeyMoney;
    public TableColumn colQty;


    ManageRoomBO manageRoomBO = (ManageRoomBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.MANAGEROOMS);

    public void initialize() {
        tblRoom.getColumns().get(0).setCellValueFactory(new PropertyValueFactory("roomId"));
        tblRoom.getColumns().get(1).setCellValueFactory(new PropertyValueFactory("type"));
        tblRoom.getColumns().get(2).setCellValueFactory(new PropertyValueFactory("keyMoney"));
        tblRoom.getColumns().get(3).setCellValueFactory(new PropertyValueFactory("qty"));

        loadRoomType();

        clearFields();
        tblRoom.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnDelete.setDisable(newValue==null);
            btnSave.setDisable(newValue==null);
            btnSave.setText(newValue !=null ? "Update":"Save");

            if (newValue != null){
                txtRoomID.setText(newValue.getRoomId());
                cmbRoomType.getSelectionModel().select(newValue.getType());
                txtKeyMoney.setText(newValue.getKeyMoney());
                txtQty.setText(String.valueOf(newValue.getQty()));


                txtRoomID.setDisable(false);
                cmbRoomType.setDisable(false);
                txtKeyMoney.setDisable(false);
                txtQty.setDisable(false);
            }

        });

        loadAllRoomDetails();
    }

    private void loadAllRoomDetails() {
        List<RoomDTO> allRooms = manageRoomBO.getAllRooms();
        for (RoomDTO roomDTO : allRooms) {
            tblRoom.getItems().add(new RoomTM(roomDTO.getRoomId(), roomDTO.getType(), roomDTO.getKeyMoney(), roomDTO.getQty()));
        }
    }


    private void clearFields() {
        txtRoomID.clear();
        cmbRoomType.getSelectionModel().clearSelection();
        txtKeyMoney.clear();
        txtQty.clear();

        txtRoomID.setDisable(true);
        cmbRoomType.setDisable(true);
        txtKeyMoney.setDisable(true);
        txtQty.setDisable(true);
    }

    public void loadRoomType(){
        cmbRoomType.getItems().add("Non-AC");
        cmbRoomType.getItems().add("Non-AC / Food");
        cmbRoomType.getItems().add("AC");
        cmbRoomType.getItems().add("AC / Food");
    }



    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (btnSave.getText().equalsIgnoreCase("Add")) {
            ObservableList<RoomTM> items = tblRoom.getItems();
            for (RoomTM item : items) {
                if (item.getRoomId().equalsIgnoreCase(cmbRoomType.getValue())) {
                    int qty = item.getQty() + Integer.parseInt(txtQty.getText());
                    if (manageRoomBO.updateQty(item.getRoomId(), qty)) {
                        new Alert(Alert.AlertType.CONFIRMATION, "Updated..!").show();
                        item.setQty(qty);
                        txtQty.setText(String.valueOf(item.getQty()));
                        txtQty.clear();
                    }
                }
            }
            tblRoom.refresh();
        } else if (btnSave.getText().equalsIgnoreCase("Save")) {
            if (manageRoomBO.saveRoom(new RoomDTO(txtRoomID.getText(), cmbRoomType.getValue(), txtKeyMoney.getText(), Integer.parseInt(txtQty.getText())))) {
                new Alert(Alert.AlertType.CONFIRMATION, "Saved..!").show();
                tblRoom.getItems().add(new RoomTM(txtRoomID.getText(), cmbRoomType.getValue(), txtKeyMoney.getText(), Integer.parseInt(txtQty.getText())));
                tblRoom.refresh();
                txtRoomID.clear();
                cmbRoomType.getSelectionModel().clearSelection();
                txtKeyMoney.clear();
                txtQty.clear();

                btnSave.setDisable(true);
                txtRoomID.setVisible(false);
                txtRoomID.setVisible(true);
                cmbRoomType.setDisable(true);
                txtKeyMoney.setDisable(true);
                txtQty.setDisable(true);
            }
        } else if (btnSave.getText().equalsIgnoreCase("Update")) {
            if (manageRoomBO.updateRoom(new RoomDTO(txtRoomID.getText(), cmbRoomType.getValue(), txtKeyMoney.getText(), Integer.parseInt(txtQty.getText())))) {
                new Alert(Alert.AlertType.CONFIRMATION, "Updated..!").show();
                RoomTM selectedItem = tblRoom.getSelectionModel().getSelectedItem();
                selectedItem.setKeyMoney(txtKeyMoney.getText());
                selectedItem.setQty(Integer.parseInt(txtQty.getText()));
                selectedItem.setType(cmbRoomType.getValue());
                tblRoom.refresh();
            }
        }

    }
    public void btnDeleteOnAction(ActionEvent actionEvent) {
//        Alert alert=new Alert(Alert.AlertType.WARNING,"Are You Sure..?", ButtonType.YES,ButtonType.NO);
//        Optional<ButtonType> buttonType = alert.showAndWait();
//        if (buttonType.get().equals(ButtonType.YES)){
//            if (manageRoomBO.deleteRoom(cmbRoomType.getValue())) {
//                new Alert(Alert.AlertType.CONFIRMATION, "Deleted..!!").show();
//                tblRoom.getItems().remove(new RoomTM(cmbRoomType.getValue(),cmbRoomType.getValue(),txtKeyMoney.getText(),Integer.parseInt(txtQty.getText())));
//            }
//        }

        String roomId = tblRoom.getSelectionModel().getSelectedItem().getRoomId();

        Alert alert=new Alert(Alert.AlertType.WARNING,"Are You Sure ?", ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get().equals(ButtonType.YES)){
            if (manageRoomBO.deleteRoom(roomId)) {
                new Alert(Alert.AlertType.CONFIRMATION,"Deleted..!").show();
                tblRoom.getItems().remove(tblRoom.getSelectionModel().getSelectedItem());
                tblRoom.getSelectionModel().clearSelection();
                clearFields();
            }
        }
    }


    public void textFields_Key_Released(KeyEvent keyEvent) {
    }


    public void btnGoBackOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage =(Stage) ManageRoomFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
        stage.centerOnScreen();
    }

    public void btnAddNewRoomOnAction(ActionEvent actionEvent) {
        txtRoomID.setDisable(false);
        cmbRoomType.setDisable(false);
        txtKeyMoney.setDisable(false);
        txtQty.setDisable(false);

        txtRoomID.clear();
        cmbRoomType.getSelectionModel().clearSelection();
        txtKeyMoney.clear();
        txtQty.clear();
        tblRoom.getSelectionModel().clearSelection();

        btnDelete.setDisable(true);
        btnSave.setDisable(false);
        btnSave.setText("Save");
        txtKeyMoney.requestFocus();



    }
}
