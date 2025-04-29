package org.example;

import database.Database;
import javafx.fxml.FXML;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainController {

    @FXML
    private Label labelSaldo;

    @FXML
    public void initializa() {
        double saldo = obtenerSaldoActual();
        labelSaldo.setText(String.format("Saldo actual: %.2f€", saldo));
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
    private void abrirFormularioMovimiento(){
        try{
            Stage stage = new Stage(); //Creamos ventana nueva
            stage.setTitle("Añadir movimiento");

            // Aquí luego cargaremos el formulario FXML

            Scene scene = new Scene(new javafx.scene.layout.VBox(), 300, 200); // Temporalmente vacío
            stage.setScene(scene);

            stage.initModality(Modality.APPLICATION_MODAL); // Modal, bloquea la ventana principal (como al adjuntar un archivo)
            stage.showAndWait(); //Espera a que se cierre esta ventana
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
