package controller;

import database.Database;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Movimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardController {

    @FXML private Label labelSaldo;
    @FXML private LineChart<String, Number> lineChartSaldo;
    @FXML private PieChart pieChartGastos;

    @FXML private TableView<Movimiento> tablaUltimos;
    @FXML private TableColumn<Movimiento, String> colFecha;
    @FXML private TableColumn<Movimiento, String> colCategoria;
    @FXML private TableColumn<Movimiento, String> colTipo;
    @FXML private TableColumn<Movimiento, String> colCantidad;

    public void initialize() {
        labelSaldo.setText(String.format("Saldo actual: %.2f€", obtenerSaldoActual()));
        cargarGraficoSaldo();
        cargarGraficoGastos();
        cargarUltimosMovimientos();
        aplicarEstilosTablaUltimos();

        tablaUltimos.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Movimiento item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    setStyle(getIndex() % 2 == 0 ? "-fx-background-color: #e8e8e8;" : "-fx-background-color: white;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void aplicarEstilosTablaUltimos() {
        colTipo.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String tipo, boolean empty) {
                super.updateItem(tipo, empty);
                if (empty || tipo == null) {
                    setGraphic(null);
                } else {
                    Label icono = new Label("●");
                    icono.setStyle("-fx-font-size: 14px; -fx-padding: 0 5 0 0;");
                    icono.setStyle(icono.getStyle() + (tipo.equalsIgnoreCase("Ingreso") ? "-fx-text-fill: #28a745;" : "-fx-text-fill: #dc3545;"));
                    setGraphic(new HBox(5, icono, new Label(tipo)));
                }
            }
        });
    }

    private void cargarGraficoSaldo() {
        lineChartSaldo.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Saldo mensual");

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT strftime('%Y-%m', fecha) AS mes, " +
                             "SUM(CASE WHEN tipo = 'Ingreso' THEN cantidad ELSE -cantidad END) AS saldo " +
                             "FROM movimientos GROUP BY mes ORDER BY mes");
             ResultSet rs = stmt.executeQuery()) {

            double saldoAcumulado = 0;
            while (rs.next()) {
                String mes = rs.getString("mes");
                double cambio = rs.getDouble("saldo");
                saldoAcumulado += cambio;
                serie.getData().add(new XYChart.Data<>(mes, saldoAcumulado));
            }

            lineChartSaldo.getData().add(serie);

        } catch (Exception e) {
            System.out.println("❌ Error en gráfico: " + e.getMessage());
        }
    }

    private void cargarGraficoGastos() {
        pieChartGastos.getData().clear();

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT categoria, SUM(cantidad) AS total " +
                             "FROM movimientos WHERE tipo = 'Gasto' GROUP BY categoria");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String categoria = rs.getString("categoria");
                double total = rs.getDouble("total");
                PieChart.Data data = new PieChart.Data(categoria, total);
                pieChartGastos.getData().add(data);

                Tooltip tooltip = new Tooltip(String.format("%.2f€", total));
                Tooltip.install(data.getNode(), tooltip);
            }

        } catch (Exception e) {
            System.out.println("❌ Error al cargar gráfico de gastos: " + e.getMessage());
        }
    }

    private void cargarUltimosMovimientos() {
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM movimientos ORDER BY fecha DESC LIMIT 5");
             ResultSet rs = stmt.executeQuery()) {

            var movimientos = FXCollections.<Movimiento>observableArrayList();

            while (rs.next()) {
                Movimiento m = new Movimiento(
                        rs.getInt("id"),
                        rs.getString("tipo"),
                        rs.getDouble("cantidad"),
                        rs.getString("fecha"),
                        rs.getString("categoria"),
                        rs.getString("descripcion")
                );
                movimientos.add(m);
            }

            tablaUltimos.setItems(movimientos);

        } catch (Exception e) {
            System.out.println("❌ Error en últimos movimientos: " + e.getMessage());
        }
    }

    private double obtenerSaldoActual() {
        double ingresos = 0;
        double gastos = 0;

        try (Connection conn = Database.connect()) {
            PreparedStatement psIngreso = conn.prepareStatement("SELECT SUM(cantidad) FROM movimientos WHERE tipo = 'Ingreso'");
            ResultSet rsIngreso = psIngreso.executeQuery();
            if (rsIngreso.next()) ingresos = rsIngreso.getDouble(1);

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
            scene.getStylesheets().add(getClass().getResource("/CSS/estilos.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Añadir movimiento");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            labelSaldo.setText(String.format("Saldo actual: %.2f€", obtenerSaldoActual()));
            cargarUltimosMovimientos();
            cargarGraficoGastos();
            lineChartSaldo.getData().clear();
            cargarGraficoSaldo();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirTodosLosMovimientos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/todosMovimientos.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/CSS/estilos.css").toExternalForm());

            TodosMovimientosController controller = loader.getController();
            controller.setDashboardController(this); // <-- PASAMOS REFERENCIA

            Stage stage = new Stage();
            stage.setTitle("Todos los movimientos");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recargarDashboard() {
        labelSaldo.setText(String.format("Saldo actual: %.2f€", obtenerSaldoActual()));
        cargarGraficoSaldo();
        cargarGraficoGastos();
        cargarUltimosMovimientos();
    }

    @FXML
    private void exportarCSV() {
        String rutaArchivo = System.getProperty("user.home") + "/Desktop/movimientos.csv";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM movimientos");
             ResultSet rs = stmt.executeQuery();
             java.io.FileWriter writer = new java.io.FileWriter(rutaArchivo)) {

            writer.write("Tipo;Cantidad;Fecha;Categoria;Descripcion\n");
            while (rs.next()) {
                String linea = String.format("%s;%.2f;%s;%s;%s\n",
                        rs.getString("tipo"),
                        rs.getDouble("cantidad"),
                        rs.getString("fecha"),
                        rs.getString("categoria"),
                        rs.getString("descripcion").replace(";", " "));
                writer.write(linea);
            }

            mostrarAlerta("\u2705 Archivo CSV exportado en el Escritorio.");

        } catch (Exception e) {
            mostrarAlerta("\u274c Error al exportar CSV: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Gestor de Finanzas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void abrirPlanificadorPresupuestos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PlanificadorPresupuestos.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/CSS/estilos.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Planificador de Presupuestos");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

