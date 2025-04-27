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
        if(connectionDatabase==null)
        {
            try {
                String DB="jdbc:mysql://localhost:3306/warmel"; // delta_db database
                String user="root"; // user database
                String pass=""; // password database
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                connectionDatabase = (Connection) DriverManager.getConnection(DB,user,pass);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,"gagal koneksi");
            }
        }
        return connectionDatabase;
    }
    
    
    
}
