package com.mycompany.mysqlc;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class PrimaryController implements Initializable {

    private static final Logger LOG = Logger.getLogger(PrimaryController.class.getName());
    @FXML
    private TextField txtCod;
    @FXML
    private TextField txtDes;

    @FXML
    private TableView<Marca> Tabla;

    @FXML
    private TableColumn<Marca, Integer> tCodigo;

    @FXML
    private TableColumn<Marca, String> tDescripcion;

    Connection con = null;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.tCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        this.tDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_investigacion", "admin", "12345");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error al conectar a la base de datos", ex);
            Alert al = new Alert(AlertType.ERROR);
            al.setTitle("Error de conexion ");
            al.setHeaderText("No se puede conectar con la base de datos");
            al.showAndWait();
            System.exit(1);
        }
        this.cargarDatos();
        this.Tabla.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Marca> obs, Marca valorAnterior, Marca valorNuevo) -> {
            if (valorNuevo != null) {
                this.txtCod.setText(valorNuevo.getCodigo().toString());
                this.txtDes.setText(valorNuevo.getDescripcion());
            }
        });
    }

    private void cargarDatos() {
        this.Tabla.getItems().clear();
        try {
            String sql = "SELECT *FROM marca";
            Statement stm = this.con.createStatement();
            ResultSet resultado = stm.executeQuery(sql);
            while (resultado.next()) {
                Integer cod = resultado.getInt("idmarca");
                String desc = resultado.getString("descripcion");
                Marca m = new Marca(cod, desc);
                this.Tabla.getItems().add(m);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error al cargar datos de la BD", ex);
        }
    }
    
    

    @FXML
    private void OnActionRegistrar(ActionEvent event) {
        String id = this.txtCod.getText();
        String descripcion = this.txtDes.getText();
        if (descripcion.isEmpty()) {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Error al registrar");
            a.setHeaderText("Complete todos los campos");
            a.show();
        } else {

            try {
                String sql = "INSERT INTO marca(descripcion) VALUES (?)";
                PreparedStatement stm = con.prepareStatement(sql);
                stm.setString(1, descripcion);
                stm.execute();
                Alert al = new Alert(AlertType.INFORMATION);
                al.setTitle("Exito");
                al.setHeaderText("Marca guardada correctamente");
                al.show();
                this.txtDes.clear();
                this.cargarDatos();

            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error al conectar a la base de datos", ex);
                Alert al = new Alert(AlertType.ERROR);
                al.setTitle("Error");
                al.setHeaderText("No se puede Insertar registro en la base de datos ");
                al.setContentText(ex.toString());
                al.showAndWait();

            }
        }
    }

    @FXML
    private void OnActionEditar(ActionEvent event) throws SQLException {
        String id = this.txtCod.getText();
        String descripcion = this.txtDes.getText();
        String sql = "UPDATE marca SET descripcion = ? WHERE idmarca = ?";

        if (id.isEmpty() || descripcion.isEmpty()) {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Error al eliminar");
            a.setHeaderText("Complete todos los campos");
            a.show();
        } else {
            try {
                PreparedStatement stm = this.con.prepareStatement(sql);
                stm.setString(1, descripcion);
                stm.setInt(2, Integer.parseInt(id));
                stm.execute();

                Alert al = new Alert(AlertType.INFORMATION);
                al.setTitle("Exito");
                al.setHeaderText("Marca guardada correctamente");
                al.show();
                this.txtDes.clear();
                this.txtCod.clear();
                this.cargarDatos();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error al Editar");
                Alert al = new Alert(AlertType.ERROR);
                al.setTitle("Error");
                al.setHeaderText("No se puede editar registro en la base de datos");
                al.setContentText(ex.toString());
                al.showAndWait();
            }

        }
    }

    @FXML
    private void eliminar(ActionEvent event) {
        String strCodigo = this.txtCod.getText();
        String strDescripcion = this.txtDes.getText();
        if (strCodigo.isEmpty()) {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Error al cargar");
            a.setHeaderText("Ingrese un codigo");
            a.show();

        } else {
            Alert alConfirm = new Alert(AlertType.INFORMATION);
            alConfirm.setTitle("Confirmar");
            alConfirm.setHeaderText("Desea eliminar la marca ");
            alConfirm.setContentText(strCodigo + "-" + strDescripcion);
            Optional<ButtonType> accion = alConfirm.showAndWait();

            if (accion.get().equals(ButtonType.OK)) {
                try {
                    String sql = "DELETE FROM marca WHERE idmarca = ?";
                    PreparedStatement stm = con.prepareStatement(sql);
                    Integer cod = Integer.parseInt(strCodigo);
                    stm.setInt(1, cod);
                    stm.execute();
                    int cantidad = stm.getUpdateCount();
                    if (cantidad == 0) {
                        Alert a = new Alert(AlertType.ERROR);
                        a.setTitle("Error al eliminar");
                        a.setHeaderText("No existe marca con ese codigo " + strCodigo);
                        a.show();
                    } else {
                        Alert a = new Alert(AlertType.INFORMATION);
                        a.setTitle("Eliminado");
                        a.setHeaderText("Marca eliminada correctamente.");
                        a.show();
                        this.txtCod.clear();
                        this.txtDes.clear();
                        this.cargarDatos();
                    }
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, "Error al importar", ex);
                }

            }
        }
    }
}
