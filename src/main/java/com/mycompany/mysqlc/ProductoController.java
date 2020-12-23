/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author gabriela
 */
public class ProductoController implements Initializable {

    private static final Logger LOG = Logger.getLogger(PrimaryController.class.getName());
    @FXML
    private TextField txtCod;
    @FXML
    private TextField txtDes;
    @FXML
    private TableView<Producto> tabla;
    @FXML
    private TextField txtPre;
    @FXML
    private ComboBox<String> cmIva;
    @FXML
    private ComboBox<Marca> cmMarca;
    @FXML
    private TableColumn<Producto, Integer> tCodigo;
    @FXML
    private TableColumn<Producto, String> tDescripcion;
    @FXML
    private TableColumn<Producto, Integer> tPre;
    @FXML
    private TableColumn<Producto, Integer> tIva;
    @FXML
    private TableColumn<Producto, Integer> tCan;

    @FXML
    private TextField txtCan;
    private ConexionBD conex;
    @FXML
    private TableColumn<?, ?> tMarca;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.tCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        this.tDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        this.tPre.setCellValueFactory(new PropertyValueFactory<>("precio"));
        this.tCan.setCellValueFactory(new PropertyValueFactory<>("can"));
        this.tIva.setCellValueFactory(new PropertyValueFactory<>("iva"));
        this.tMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        //Establecer la forma en el que el combo va a mostrar los ítems en el menú
        this.cmMarca.setCellFactory((ListView<Marca> l) -> {

            return new ListCell<Marca>() {
                @Override
                protected void updateItem(Marca m, boolean empty) {
                    if (!empty) {
                        this.setText("(" + m.getCodigo() + ") " + m.getDescripcion());
                    } else {
                        this.setText("");
                    }
                    super.updateItem(m, empty);
                }
            };
        });

        //Establecer la forma en que el combo va a mostrar la marca seleccioinada
        this.cmMarca.setButtonCell(new ListCell<Marca>() {
            @Override
            protected void updateItem(Marca m, boolean empty) {
                if (!empty) {
                    this.setText("(" + m.getCodigo() + ") " + m.getDescripcion());
                } else {
                    this.setText("");
                }
                super.updateItem(m, empty);
            }
        }
        );

        //Cargar los posibles valores en el combo de IVA
        this.cmIva.getItems().add("10%");
        this.cmIva.getItems().add("5%");
        this.cmIva.getItems().add("Excento");
        this.cmIva.getSelectionModel().selectFirst();

        //Se crea la conexion a la base de datos con la clase creada para el efecto
        this.conex = new ConexionBD();
        //Invocamos al metodo que trae los registros de la tabla marca para cargar en el combo
        this.cargarMarcas();
        this.cargarDatos();

        this.tabla.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Producto> obs, Producto valorAnterior, Producto valorNuevo) -> {
            if (valorNuevo != null) {
                this.txtCod.setText(valorNuevo.getCodigo().toString());
                this.txtDes.setText(valorNuevo.getDescripcion());
                this.txtCan.setText(valorNuevo.getCan().toString());
                this.txtPre.setText(valorNuevo.getPrecio().toString());

            }
        });
    }

    //Metodo que consulta a la base de datos y carga las marcas en el combo
    private void cargarMarcas() {
        try {
            String sql = "SELECT * FROM marca";
            Statement stm = this.conex.getConexion().createStatement();
            ResultSet resultado = stm.executeQuery(sql);
            while (resultado.next()) {
                this.cmMarca.getItems().add(new Marca(resultado.getInt("idmarca"), resultado.getString("descripcion")));
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al cargar Marcas", ex);
        }

    }

    private void cargarDatos() {
        this.tabla.getItems().clear();
        try {
            String sql = "SELECT * FROM producto";
            Statement stm = conex.getConexion().prepareStatement(sql);
            ResultSet resultado = stm.executeQuery(sql);
            while (resultado.next()) {
                Integer codigo = resultado.getInt("idproducto");
                String desc = resultado.getString("descripcion");
                Integer can = resultado.getInt("cantidad");
                Integer iva = resultado.getInt("iva");
                Integer pre = resultado.getInt("precio");
                String marca = resultado.getString("idmarca");
                Producto p = new Producto(codigo, desc, can, iva, pre, marca);
                this.tabla.getItems().add(p);
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error al cargar datos de la BD", ex);
        }
    }

    @FXML
    private void OnActionRegistrar(ActionEvent event) {
        String codigo = this.txtCod.getText();
        String descripcion = this.txtDes.getText();

        String tmpCan = this.txtCan.getText();
        Double can = Double.parseDouble(tmpCan);

        String tmpPre = this.txtPre.getText();
        Double pre = Double.parseDouble(tmpPre);
        Integer iva;
        Integer idmarca = cmMarca.getSelectionModel().getSelectedItem().getCodigo();

        if (cmIva.getSelectionModel().getSelectedItem().equals("10%")) {
            iva = 10;

        } else if (cmIva.getSelectionModel().getSelectedItem().equals("5%")) {
            iva = 5;

        } else {
            iva = 0;
        }
        if (descripcion.isEmpty() || tmpCan.isEmpty() || codigo.isEmpty() || tmpPre.isEmpty()) {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Error al registrar");
            a.setHeaderText("Complete todos los campos");
            a.show();
        } else {

            try {
                String sql = "INSERT INTO producto( descripcion, cantidad, iva,  precio, idmarca) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stm = conex.getConexion().prepareStatement(sql);
                stm.setString(1, descripcion);
                stm.setDouble(2, can);
                stm.setInt(3, iva);
                stm.setDouble(4, pre);
                stm.setInt(5, idmarca);

                stm.execute();
                Alert al = new Alert(AlertType.INFORMATION);
                al.setTitle("Exito");
                al.setHeaderText("Producto guardada correctamente");
                al.show();
                this.txtDes.clear();
                this.txtCod.clear();
                this.txtCan.clear();
                this.txtPre.clear();
                this.cargarDatos();

            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error al conectar a la base de datos", ex);
                Alert al = new Alert(AlertType.ERROR);
                al.setTitle("Error");
                al.setHeaderText("No se puede Insertar registro en la base de datos ");
                al.setContentText(ex.toString());
                al.showAndWait();

            }
            this.cargarDatos();
            this.cargarMarcas();
        }

    }

    @FXML
    private void OnActionEditar(ActionEvent event) throws SQLException {
        String id = this.txtCod.getText();
        String descripcion = this.txtDes.getText();
        String tmpCan = this.txtCan.getText();
        Double can = Double.parseDouble(tmpCan);

        String tmpPre = this.txtPre.getText();
        Double pre = Double.parseDouble(tmpPre);
        Integer iva;
        Integer idmarca = cmMarca.getSelectionModel().getSelectedItem().getCodigo();

        if (cmIva.getSelectionModel().getSelectedItem().equals("10%")) {
            iva = 10;

        } else if (cmIva.getSelectionModel().getSelectedItem().equals("5%")) {
            iva = 5;

        } else {
            iva = 0;
        }

        String sql = "UPDATE producto SET descripcion= ?, cantidad= ?, iva=?,  precio=?, idmarca=? WHERE idproducto = ? ";
        if (id.isEmpty() || descripcion.isEmpty() || tmpPre.isEmpty() || tmpCan.isEmpty()) {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Error al eliminar");
            a.setHeaderText("Complete todos los campos");
            a.show();
        } else {
            try {

                PreparedStatement stm = conex.getConexion().prepareStatement(sql);
                stm.setString(1, descripcion);
                stm.setDouble(2, can);
                stm.setInt(3, iva);
                stm.setDouble(4, pre);
                stm.setInt(5, idmarca);
                stm.setInt(6, Integer.parseInt(id));
                stm.execute();
                
                Alert al = new Alert(AlertType.INFORMATION);
                al.setTitle("Exito");
                al.setHeaderText("Producto guardada correctamente");
                al.show();
                this.txtDes.clear();
                this.txtCod.clear();
                this.txtCan.clear();
                this.txtPre.clear();
               
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error al Editar");
                Alert al = new Alert(AlertType.ERROR);
                al.setTitle("Error");
                al.setHeaderText("No se puede editar registro en la base de datos");
                al.setContentText(ex.toString());
                al.showAndWait();
            }
            this.cargarDatos();
            this.cargarMarcas();
        }
    }

    @FXML
    private void OnActionEliminar(ActionEvent event) {

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
                    String sql = "DELETE FROM producto WHERE idproducto = ?";
                    PreparedStatement stm = conex.getConexion().prepareStatement(sql);
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
                        this.txtCan.clear();
                        this.txtPre.clear();
                        this.cargarDatos();
                    }
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, "Error al importar", ex);
                }

            }
        }
    }

}
//UPDATE producto SET descripcion = ? WHERE idproducto = ?"
