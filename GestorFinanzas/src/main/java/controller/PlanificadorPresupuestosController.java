package controller;

import database.Database;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Presupuesto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class PlanificadorPresupuestosController {

    @FXML private TextField campoMes;
    @FXML private TextField campoCategoria;
    @FXML private TextField campoPresupuesto;

    @FXML private TableView<Presupuesto> tablaPresupuestos;
    @FXML private TableColumn<Presupuesto, String> colMes;
    @FXML private TableColumn<Presupuesto, String> colCategoria;
    @FXML private TableColumn<Presupuesto, Double> colPresupuesto;

    public void initialize() {
        colMes.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMes()));
        colCategoria.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategoria()));
        colPresupuesto.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCantidad()));

        cargarPresupuestos();

        TableColumn<Presupuesto, Void> colEliminar = new TableColumn<>("Eliminar");
        colEliminar.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("🗑");

            {
                btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    Presupuesto presupuesto = getTableView().getItems().get(getIndex());
                    eliminarPresupuesto(presupuesto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tablaPresupuestos.getColumns().add(colEliminar);
    }

    private void cargarPresupuestos() {
        tablaPresupuestos.getItems().clear();

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM presupuestos ORDER BY mes DESC");
             ResultSet rs = stmt.executeQuery()) {

            List<Presupuesto> lista = FXCollections.observableArrayList();
            while (rs.next()) {
                lista.add(new Presupuesto(
                        rs.getString("mes"),
                        rs.getString("categoria"),
                        rs.getDouble("presupuesto")
                ));
            }

            tablaPresupuestos.setItems(FXCollections.observableArrayList(lista));

        } catch (Exception e) {
            System.out.println("❌ Error al cargar presupuestos: " + e.getMessage());
        }
    }

    @FXML
    private void guardarPresupuesto() {
        String mes = campoMes.getText().trim();
        String categoria = campoCategoria.getText().trim();
        String cantidadStr = campoPresupuesto.getText().trim();

        if (mes.isEmpty() || categoria.isEmpty() || cantidadStr.isEmpty()) {
            mostrarAlerta("Rellena todos los campos.");
            return;
        }

        try {
            double cantidad = Double.parseDouble(cantidadStr);
            try (Connection conn = Database.connect();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO presupuestos (mes, categoria, presupuesto) VALUES (?, ?, ?)");) {

                stmt.setString(1, mes);
                stmt.setString(2, categoria);
                stmt.setDouble(3, cantidad);
                stmt.executeUpdate();

                mostrarAlerta("✅ Presupuesto guardado correctamente.");
                campoMes.clear(); campoCategoria.clear(); campoPresupuesto.clear();
                cargarPresupuestos();

            } catch (Exception e) {
                mostrarAlerta("❌ Error al guardar: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Introduce una cantidad válida.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Planificador de Presupuestos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    private void eliminarPresupuesto(Presupuesto presupuesto) {
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM presupuestos WHERE mes = ? AND categoria = ?")) {
            stmt.setString(1, presupuesto.getMes());
            stmt.setString(2, presupuesto.getCategoria());
            int filas = stmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta("✅ Presupuesto eliminado.");
                cargarPresupuestos();
            } else {
                mostrarAlerta("❌ No se encontró el presupuesto.");
            }
        } catch (Exception e) {
            mostrarAlerta("❌ Error al eliminar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private VBox contenedorGrafico;

    @FXML
    private void mostrarGraficoComparacion() {
        contenedorGrafico.getChildren().clear();
        String mes = campoMes.getText().trim();

        if (mes.isEmpty()) {
            mostrarAlerta("Introduce un mes válido (ej: 2025-05)");
            return;
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Categoría");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Euros (€)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Comparativa Gasto vs Presupuesto - " + mes);
        barChart.setLegendVisible(true);
        barChart.setPrefHeight(350);

        XYChart.Series<String, Number> seriePresupuesto = new XYChart.Series<>();
        seriePresupuesto.setName("Presupuesto");

        XYChart.Series<String, Number> serieGasto = new XYChart.Series<>();
        serieGasto.setName("Gasto Real");

        try (Connection conn = Database.connect()) {
            PreparedStatement ps1 = conn.prepareStatement("SELECT categoria, presupuesto FROM presupuestos WHERE mes = ?");
            ps1.setString(1, mes);
            ResultSet rs1 = ps1.executeQuery();

            while (rs1.next()) {
                String categoria = rs1.getString("categoria");
                double presupuesto = rs1.getDouble("presupuesto");

                seriePresupuesto.getData().add(new XYChart.Data<>(categoria, presupuesto));

                PreparedStatement ps2 = conn.prepareStatement("SELECT SUM(cantidad) FROM movimientos WHERE tipo = 'Gasto' AND categoria = ? AND strftime('%Y-%m', fecha) = ?");
                ps2.setString(1, categoria);
                ps2.setString(2, mes);
                ResultSet rs2 = ps2.executeQuery();

                double gasto = rs2.next() ? rs2.getDouble(1) : 0.0;
                serieGasto.getData().add(new XYChart.Data<>(categoria, gasto));
            }

            barChart.getData().addAll(seriePresupuesto, serieGasto);
            contenedorGrafico.getChildren().add(barChart);

        } catch (Exception e) {
            mostrarAlerta("❌ Error al generar gráfico: " + e.getMessage());
        }
    }


}


