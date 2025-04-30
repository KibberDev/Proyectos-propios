package controller;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Movimiento;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.paint.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainController {

    @FXML
    private Label labelSaldo;

    @FXML
    public void initialize() {
        double saldo = obtenerSaldoActual();

        labelSaldo.setText(String.format("Saldo actual: %.2f€", saldo));

        colTipo.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTipo()));
        colCantidad.setCellValueFactory(
                data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getCantidad()).asObject());
        colFecha.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFecha()));
        colCategoria.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategoria()));
        colDescripcion.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescripcion()));

        cargarMovimientos();

        tablaMovimientos.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Movimiento movimiento, boolean empty) {
                super.updateItem(movimiento, empty);
                if (movimiento == null || empty) {
                    setStyle("");
                } else {
                    if (movimiento.getTipo().equalsIgnoreCase("Ingreso")) {
                        setStyle("-fx-background-color: #d4edda;"); // verde suave
                    } else if (movimiento.getTipo().equalsIgnoreCase("Gasto")) {
                        setStyle("-fx-background-color: #f8d7da;"); // rojo suave
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        FontIcon iconoPapelera = new FontIcon("fas-trash-alt");
        iconoPapelera.setIconSize(18);
        iconoPapelera.setIconColor(Color.WHITE);

        buttonEliminar.setGraphic(iconoPapelera);

        FontIcon iconoExportar = new FontIcon("fas-file-csv");
        iconoExportar.setIconSize(18);
        iconoExportar.setIconColor(Color.WHITE);
        botonExportar.setGraphic(iconoExportar);

    }


    private double obtenerSaldoActual() {
        double ingresos = 0;
        double gastos = 0;

        try (Connection conn = Database.connect()) {
            //Sumar ingresos
            PreparedStatement psIngreso = conn.prepareStatement(
                    "SELECT SUM(cantidad) FROM movimientos WHERE tipo = 'Ingreso'");
            ResultSet rsIngreso = psIngreso.executeQuery();
            if (rsIngreso.next()) ingresos = rsIngreso.getDouble(1);

            // Sumar gastos
            PreparedStatement psGasto = conn.prepareStatement("SELECT SUM(cantidad) FROM movimientos WHERE tipo = 'Gasto'");
            ResultSet rsGasto = psGasto.executeQuery();
            if (rsGasto.next()) gastos = rsGasto.getDouble(1);

        } catch (Exception e) {
            System.out.println("❌ Error al obtener saldo: " + e.getMessage());
        }
        return ingresos - gastos;
    }

    @FXML
    private void abrirFormularioMovimiento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/añadirMovimiento.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage(); //Creamos ventana nueva
            stage.setTitle("Añadir movimiento");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            double saldo = obtenerSaldoActual();
            labelSaldo.setText(String.format("Saldo actual: %.2f€", saldo));
            cargarMovimientos();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TableView<model.Movimiento> tablaMovimientos;

    @FXML
    private TableColumn<model.Movimiento, String> colTipo;

    @FXML
    private TableColumn<model.Movimiento, Double> colCantidad;

    @FXML
    private TableColumn<model.Movimiento, String> colFecha;

    @FXML
    private TableColumn<model.Movimiento, String> colCategoria;

    @FXML
    private TableColumn<model.Movimiento, String> colDescripcion;


    @FXML
    private void cargarMovimientos() {
        System.out.println("Cargando movimientos...");
        tablaMovimientos.getItems().clear();

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM movimientos ORDER BY fecha DESC")) {

            while (rs.next()) {
                Movimiento mov = new Movimiento(
                        rs.getString("tipo"),
                        rs.getDouble("cantidad"),
                        rs.getString("fecha"),
                        rs.getString("categoria"),
                        rs.getString("descripcion")
                );
                tablaMovimientos.getItems().add(mov);
            }

        } catch (Exception e) {
            System.out.println("Error al cargar movimiento: " +e.getMessage());
        }


    }

    @FXML
    private Button buttonEliminar;

    @FXML
    private void eliminarMovimiento() {
        Movimiento seleccionado = tablaMovimientos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Eliminar movimiento");
            alerta.setHeaderText(null);
            alerta.setContentText("Selecciona un movimiento para eliminar.");
            alerta.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que quieres eliminar este movimiento?");
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try (Connection conn = Database.connect();
                     PreparedStatement stmt = conn.prepareStatement(
                             "DELETE FROM movimientos WHERE tipo = ? AND cantidad = ? AND fecha = ? AND categoria = ? AND descripcion = ?")) {

                    stmt.setString(1, seleccionado.getTipo());
                    stmt.setDouble(2, seleccionado.getCantidad());
                    stmt.setString(3, seleccionado.getFecha());
                    stmt.setString(4, seleccionado.getCategoria());
                    stmt.setString(5, seleccionado.getDescripcion());

                    stmt.executeUpdate();

                    // Actualizar tabla y saldo
                    cargarMovimientos();
                    labelSaldo.setText(String.format("Saldo actual: %.2f€", obtenerSaldoActual()));

                } catch (Exception e) {
                    System.out.println("❌ Error al eliminar: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private ComboBox<String> comboCategoria;

    @FXML
    private void abrirGraficoGastos(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/graficoGastos.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Gastos por categoría");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button botonExportar;

    @FXML
    private void exportarCSV() {
        String userHome = System.getProperty("user.home");
        String rutaArchivo = userHome + "/Desktop/movimientos.csv";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM movimientos");
             ResultSet rs = stmt.executeQuery();
             java.io.FileWriter writer = new java.io.FileWriter(rutaArchivo)) {

            // Cabecera
            writer.write("Tipo;Cantidad;Fecha;Categoria;Descripcion\n");

            // Filas
            while (rs.next()) {
                String linea = String.format("%s;%.2f;%s;%s;%s\n",
                        rs.getString("tipo"),
                        rs.getDouble("cantidad"),
                        rs.getString("fecha"),
                        rs.getString("categoria"),
                        rs.getString("descripcion").replace(";", " "));
                writer.write(linea);
            }

            mostrarAlerta("✅ Archivo CSV exportado en el Escritorio.");

        } catch (Exception e) {
            mostrarAlerta("❌ Error al exportar CSV: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Exportar a CSV");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }


}



