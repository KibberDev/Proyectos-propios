package controller;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class AñadirMovimientoController {

    @FXML
    private ComboBox<String> tipoComboBox;

    @FXML
    private TextField cantidadField;

    @FXML
    private DatePicker fechaPicker;

    @FXML
    private TextArea descripcionArea;

    @FXML
    private void guardarMovimiento(){
        String tipo = tipoComboBox.getValue();
        String cantidadTexto = cantidadField.getText();
        LocalDate fecha = fechaPicker.getValue();
        String categoria = comboCategoria.getValue();
        String descripcion = descripcionArea.getText();

        //Validación formulario completo
        if(tipo == null || cantidadTexto.isEmpty() || fecha == null || categoria.isEmpty() || descripcion.isEmpty()) {
            mostrarAlerta("Por favor rellena todos los campos del formulario.");
            return;
        }

        try{
            double cantidad = Double.parseDouble(cantidadTexto);
            Connection conn = Database.connect();
            String sql = "INSERT INTO movimientos (tipo, cantidad, fecha, categoria, descripcion) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tipo);
            stmt.setDouble(2, cantidad);
            stmt.setString(3, fecha.toString());
            stmt.setString(4, categoria);
            stmt.setString(5, descripcion != null ? descripcion : "");
            stmt.executeUpdate();

            //Cierra ventana tras guardar
            cerrarVentana();
        } catch (NumberFormatException e) {
            mostrarAlerta("La cantidad debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error al guardar el movimiento: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar(){
        cerrarVentana();
    }

    @FXML
    private void cerrarVentana(){
        Stage stage = (Stage) cantidadField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void mostrarAlerta(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("¡Advertencia!");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private ComboBox<String> comboCategoria;

    @FXML
    public void initialize() {
        comboCategoria.getItems().addAll(
                "Comida",
                "Transporte",
                "Ocio",
                "Salud",
                "Educación",
                "Hogar",
                "Seguros",
                "Juegos",
                "Salario",
                "Paga extra",
                "Premios",
                "Apuestas",
                "Ropa",
                "Créditos",
                "Otros"
        );
    }
}
