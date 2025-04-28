/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;


/**
 *
 * @author brsap
 */
public class Connections {
    private static Connection connectionDatabase;
    public static Connection ConnectionDB() throws SQLException {
        if (connectionDatabase == null || connectionDatabase.isClosed()) {
            try {
                // Tidak perlu lagi mendaftarkan driver secara manual
                String DB = "jdbc:mysql://localhost:3306/warmel"; // Nama database
                String user = "root"; // Nama pengguna database
                String pass = ""; // Password database

                // Membuka koneksi database
                connectionDatabase = DriverManager.getConnection(DB, user, pass);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Gagal koneksi: " + e.getMessage());
                throw e;  // Meneruskan exception jika koneksi gagal
            }
        }
        return connectionDatabase;
    }

    
    
    
}
