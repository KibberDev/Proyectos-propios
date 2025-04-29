package controller;

import database.Database;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Movimiento;

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
}
