package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.JDBC;

public class Database {

    private static final String URL = "jdbc:sqlite:GestorFinanzas.db";

    public static Connection connect(){
        Connection conn = null;

        try{
            conn = DriverManager.getConnection(URL);
            System.out.println("✅ Conexión exitosa a la base de datos.");
            crearTablaSiNoExiste(conn);
        } catch (SQLException e){
            System.out.println("❌ Error al conectar a la base de datos: " + e.getMessage());
        }
        return conn;
    }

    private static void crearTablaSiNoExiste(Connection connection){
        String sql = "CREATE TABLE IF NOT EXISTS movimientos (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, \n"
                + "tipo TEXT NOT NULL, \n"
                + "cantidad REAL NOT NULL, \n"
                + "fecha TEXT NOT NULL, \n"
                + "categoria TEXT NOT NULL, \n"
                + "descripcion TEXT\n"
                + ");";

        try(Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Tabla 'movimientos' verificada/creada.");
        } catch (SQLException e) {
            System.out.println("❌ Error al crear/verificar la tabla: " + e.getMessage());
        }
    }
}
