package controller;

import database.Database;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.Movimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class TodosMovimientosController {

    @FXML private TableView<Movimiento> tablaMovimientos;
    @FXML private TableColumn<Movimiento, String> colTipoMov;
    @FXML private TableColumn<Movimiento, Double> colCantidadMov;
    @FXML private TableColumn<Movimiento, String> colFechaMov;
    @FXML private TableColumn<Movimiento, String> colCategoriaMov;
    @FXML private TableColumn<Movimiento, String> colDescripcionMov;

    private DashboardController dashboardController;

    public void initialize() {
        colTipoMov.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo()));
        colCantidadMov.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getCantidad()));
        colFechaMov.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFecha()));
        colCategoriaMov.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategoria()));
        colDescripcionMov.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescripcion()));

        // ✅ Añadir círculos de color en la columna tipo
        colTipoMov.setCellFactory(column -> new TableCell<>() {
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

        cargarTodos();

        tablaMovimientos.setRowFactory(tv -> new TableRow<>() {
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

    private void cargarTodos() {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM movimientos ORDER BY fecha DESC")) {

            var movimientos = FXCollections.<Movimiento>observableArrayList();

            while (rs.next()) {
                Movimiento mov = new Movimiento(
                        rs.getInt("id"),
                        rs.getString("tipo"),
                        rs.getDouble("cantidad"),
                        rs.getString("fecha"),
                        rs.getString("categoria"),
                        rs.getString("descripcion")
                );
                movimientos.add(mov);
            }

            tablaMovimientos.setItems(movimientos);

        } catch (Exception e) {
            System.out.println("❌ Error al cargar movimientos: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarMovimiento() {
        Movimiento seleccionado = tablaMovimientos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un movimiento para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar movimiento");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que quieres eliminar el movimiento con ID: " + seleccionado.getId() + "?");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try (Connection conn = Database.connect();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM movimientos WHERE id = ?")) {

                    stmt.setInt(1, seleccionado.getId());
                    int filas = stmt.executeUpdate();

                    if (filas > 0) {
                        mostrarAlerta("✅ Movimiento eliminado correctamente.");
                    } else {
                        mostrarAlerta("⚠ No se pudo eliminar el movimiento.");
                    }

                    cargarTodos();

                } catch (Exception e) {
                    mostrarAlerta("❌ Error al eliminar: " + e.getMessage());
                }
            }
        });
        if (dashboardController != null) {
            dashboardController.recargarDashboard();
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Gestor de Finanzas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }
}
