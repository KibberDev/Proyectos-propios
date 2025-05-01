package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.JDBC;

public class Database {

    private static final String URL = "jdbc:sqlite:src/main/resources/db/GestorFinanzas.db";

    public static Connection connect(){
        Connection conn = null;

        try{
            conn = DriverManager.getConnection(URL);
            System.out.println("✅ Conexión exitosa a la base de datos.");
            System.out.println("Ruta absoluta de la DB: " + new java.io.File(URL.replace("jdbc:sqlite:", "")).getAbsolutePath());
            crearTablaSiNoExiste(conn);
        } catch (SQLException e){
            System.out.println("❌ Error al conectar a la base de datos: " + e.getMessage());
        }
        return conn;
    }

    private static void crearTablaSiNoExiste(Connection connection){
        String sqlMovimientos = "CREATE TABLE IF NOT EXISTS movimientos (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, \n"
                + "tipo TEXT NOT NULL, \n"
                + "cantidad REAL NOT NULL, \n"
                + "fecha TEXT NOT NULL, \n"
                + "categoria TEXT NOT NULL, \n"
                + "descripcion TEXT\n"
                + ");";

        String sqlPresupuestos = "CREATE TABLE IF NOT EXISTS presupuestos (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, \n"
                + "mes TEXT NOT NULL, \n"
                + "categoria TEXT NOT NULL, \n"
                + "presupuesto REAL NOT NULL\n"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlMovimientos);
            stmt.execute(sqlPresupuestos);
            System.out.println("✅ Tablas 'movimientos' y 'presupuestos' verificadas/creadas.");
        } catch (SQLException e) {
            System.out.println("❌ Error al crear/verificar las tablas: " + e.getMessage());
        }
    }
}
