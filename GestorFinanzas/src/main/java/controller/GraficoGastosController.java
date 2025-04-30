package controller;

import database.Database;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Tooltip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GraficoGastosController {

    @FXML
    private PieChart graficoGastos;

    @FXML
    public void initialize() {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT categoria, SUM(cantidad) as total FROM movimientos WHERE tipo = 'Gasto' GROUP BY categoria";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            double totalGastos = 0;
            Map<String, Double> datos = new HashMap<>();

            // 1. Calcular total de gastos
            while (rs.next()) {
                String categoria = rs.getString("categoria");
                double cantidad = rs.getDouble("total");
                totalGastos += cantidad;
                datos.put(categoria, cantidad);
            }

            // 2. Añadir cada categoría al gráfico con tooltip
            for (Map.Entry<String, Double> entry : datos.entrySet()) {
                String categoria = entry.getKey();
                double cantidad = entry.getValue();
                double porcentaje = (cantidad / totalGastos) * 100;

                String etiqueta = String.format("%s: %.2f€ (%.1f%%)", categoria, cantidad, porcentaje);

                Data data = new Data(etiqueta, cantidad);
                graficoGastos.getData().add(data);

                // Tooltip asociado
                Platform.runLater(() -> {
                    Tooltip tooltip = new Tooltip(etiqueta);
                    Tooltip.install(data.getNode(), tooltip);
                });
            }

        } catch (Exception e) {
            System.out.println("❌ Error al cargar gráfico: " + e.getMessage());
        }
    }
}









