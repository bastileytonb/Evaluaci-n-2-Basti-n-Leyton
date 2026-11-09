package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtSuperficie;
    @FXML private TextField txtHabitaciones;
    @FXML private TextField txtPiso;
    @FXML private CheckBox chkGastoComun;
    @FXML private CheckBox chkEstacionamiento;
    @FXML private CheckBox chkPatio;
    @FXML private VBox panelDepartamento;
    @FXML private VBox panelCasa;
    @FXML private Label lblMensaje;

    @FXML private TableView<Vivienda> tblViviendas;
    @FXML private TableColumn<Vivienda, String> colTipo;
    @FXML private TableColumn<Vivienda, String> colCodigo;
    @FXML private TableColumn<Vivienda, Double> colSuperficie;
    @FXML private TableColumn<Vivienda, Integer> colHabitaciones;
    @FXML private TableColumn<Vivienda, Double> colCosto;

    private final ViviendaRepository repositorio = new ViviendaRepository();

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll("Departamento", "Casa");
        cmbTipo.valueProperty().addListener((obs, anterior, actual) -> actualizarTipo());

        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colSuperficie.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        colHabitaciones.setCellValueFactory(new PropertyValueFactory<>("habitaciones"));
        colCosto.setCellValueFactory(c ->
            new ReadOnlyDoubleWrapper(c.getValue().calcularCostoArriendo()).asObject());
        tblViviendas.setItems(repositorio.getViviendas());

        cargarDatos();
    }

    private void actualizarTipo() {
        String tipo = cmbTipo.getValue();
        panelDepartamento.setDisable(!"Departamento".equals(tipo));
        panelCasa.setDisable(!"Casa".equals(tipo));
    }

    private void cargarDatos() {
        try {
            repositorio.cargar();
        } catch (IOException e) {
            lblMensaje.setText("Error al leer los archivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void registrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            lblMensaje.setText("Tipo: debe seleccionar un tipo de vivienda.");
            return;
        }

        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            lblMensaje.setText("Código: no puede estar vacío.");
            return;
        }

        double superficie;
        try {
            superficie = Double.parseDouble(txtSuperficie.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            lblMensaje.setText("Superficie: debe ser un número entre 20 y 500.");
            return;
        }
        if (!(superficie >= 20 && superficie <= 500)) {
            lblMensaje.setText("Superficie: debe ser un número entre 20 y 500.");
            return;
        }

        int habitaciones;
        try {
            habitaciones = Integer.parseInt(txtHabitaciones.getText().trim());
        } catch (NumberFormatException e) {
            lblMensaje.setText("Habitaciones: debe ser un número entero mayor que cero.");
            return;
        }
        if (habitaciones <= 0) {
            lblMensaje.setText("Habitaciones: debe ser un número entero mayor que cero.");
            return;
        }

        Vivienda vivienda;
        if ("Departamento".equals(tipo)) {
            int piso;
            try {
                piso = Integer.parseInt(txtPiso.getText().trim());
            } catch (NumberFormatException e) {
                lblMensaje.setText("Piso: debe ser un número entero mayor que cero.");
                return;
            }
            Departamento departamento = new Departamento(codigo, superficie, habitaciones, piso,
                    chkGastoComun.isSelected());
            if (chkEstacionamiento.isSelected()) {
                departamento.asignarEstacionamiento();
            }
            vivienda = departamento;
        } else {
            vivienda = new Casa(codigo, superficie, habitaciones, chkPatio.isSelected());
        }

        try {
            repositorio.agregar(vivienda);
        } catch (IOException e) {
            lblMensaje.setText("Error al guardar el archivo: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        lblMensaje.setText(vivienda.getCodigo() + " (" + vivienda.getTipo() + ") registrada correctamente.");
        limpiarFormulario();
    }

    @FXML
    private void eliminar() {
        Vivienda seleccionada = tblViviendas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING,
                    "Debe seleccionar una vivienda de la tabla para eliminarla.");
            alerta.showAndWait();
            return;
        }

        try {
            repositorio.eliminar(seleccionada);
        } catch (IOException e) {
            lblMensaje.setText("Error al guardar el archivo: " + e.getMessage());
            return;
        }

        lblMensaje.setText(seleccionada.getCodigo() + " (" + seleccionada.getTipo() + ") eliminada correctamente.");
    }

    private void limpiarFormulario() {
        txtCodigo.clear();
        txtSuperficie.clear();
        txtHabitaciones.clear();
        txtPiso.clear();
    }
}
