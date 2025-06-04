/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Project.Pages.Produk;

import Project.Connection.Connections;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.SQLException;


import javax.swing.JOptionPane;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Project.Helper.CurrencyFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.proteanit.sql.DbUtils;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author brsap
 */
public class ProdukMenu extends javax.swing.JInternalFrame {

    /**
     * Creates new form 
     */
    public boolean dataBaru;
    public ProdukMenu() {
        
        initComponents();
        AutoCompleteDecorator.decorate(cbKategori);
        getKategori();
        getData();
        dataBaru = true;

     
    }
    
    private void getData() {
        CurrencyFormat formatIDCurrency = new CurrencyFormat();

        java.sql.Connection conn = null;
        java.sql.Statement stm = null;
        java.sql.ResultSet sql = null;

        try {
            conn = (Connection) Connections.ConnectionDB();

            // Memeriksa apakah koneksi berhasil
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Koneksi ke database gagal.");
                return;
            }

            // Menjalankan query
            stm = conn.createStatement();
            sql = stm.executeQuery(
                "SELECT p.idProduk, p.namaProduk, k.namaKategori, p.hargaBeli, p.hargaJual, p.stok, p.created_at, p.updated_at " +
                "FROM produk p " +
                "INNER JOIN kategori k ON p.idKategori = k.idKategori"
            );

            // Memeriksa apakah query mengembalikan hasil
            if (!sql.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "Tidak ada data yang ditemukan.");
                return;
            }

            // Membuat model tabel untuk menampilkan data
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("No");
            model.addColumn("Id Produk");
            model.addColumn("Nama Produk");
            model.addColumn("Kategori");
            model.addColumn("Harga Beli");
            model.addColumn("Harga Jual");
            model.addColumn("Stok");
            model.addColumn("Created");
            model.addColumn("Updated At");

            // Menambahkan data ke dalam model
            int no = 1;  // Variabel untuk nomor urut

            // Menambahkan data dari ResultSet ke model tabel
            while (sql.next()) {
                model.addRow(new Object[]{
                    no++, // Menambahkan nomor urut
                    sql.getString("idProduk"), // Menambahkan nama produk
                    sql.getString("namaProduk"), // Menambahkan nama produk
                    sql.getString("namaKategori"), // Menambahkan kategori
                    formatIDCurrency.formatCurrency(sql.getDouble("hargaBeli")), // Menambahkan harga beli
                    formatIDCurrency.formatCurrency(sql.getDouble("hargaJual")), // Menambahkan harga jual
                    sql.getInt("stok"), // Menambahkan stok
                    sql.getString("created_at"), // Menambahkan created_at
                    sql.getString("updated_at")  // Menambahkan updated_at
                });
            }

            // Menampilkan model ke dalam tabel
            produkTable.setModel(model);
            
            //sembunyikan idPembelian
            produkTable.getColumnModel().getColumn(1).setMinWidth(0);
            produkTable.getColumnModel().getColumn(1).setMaxWidth(0);
            produkTable.getColumnModel().getColumn(1).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error SQL: " + e.getMessage());
            e.printStackTrace(); // Untuk debugging
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Error Antarmuka: " + e.getMessage());
            e.printStackTrace(); // Untuk debugging
        } finally {
            // Menutup koneksi dan statement di dalam blok finally untuk memastikan selalu ditutup
            try {
                if (sql != null) sql.close();
                if (stm != null) stm.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error saat menutup koneksi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


        private void getKategori() {
            try (Connection conn = (Connection) Connections.ConnectionDB();
                 java.sql.Statement stm = conn.createStatement();
                 java.sql.ResultSet queryKategori = stm.executeQuery("select idKategori, namaKategori from kategori")) {

                cbKategori.removeAllItems();
                cbKategori.addItem("Silakan pilih kategori...");

                // Menyimpan idKategori dalam map untuk memetakan namaKategori ke idKategori
                Map<String, String> kategoriMap = new HashMap<>();

                // Menambahkan data ke dalam model
                while (queryKategori.next()) {
                    String idKategori = queryKategori.getString("idKategori");
                    String namaKategori = queryKategori.getString("namaKategori");
                    kategoriMap.put(namaKategori, idKategori);  // Menyimpan mapping antara nama dan idKategori
                    cbKategori.addItem(namaKategori);
                }

                // Menambahkan listener untuk combo box
                cbKategori.addActionListener(new ActionListener() {
                    private boolean firstSelection = true; // Flag untuk menangani pilihan pertama

                    public void actionPerformed(ActionEvent e) {
                        String selectedKategori = (String) cbKategori.getSelectedItem();

                        // Cek apakah kategori yang valid dipilih
                        if (selectedKategori != null && !selectedKategori.equals("Silakan pilih kategori...")) {
                            // Setel idKategori ke txtIdKategori
                            String idKategori = kategoriMap.get(selectedKategori);
                            txtIdKategori.setText(idKategori);
                            labelKategori.setText(selectedKategori);

                            // Hanya set pertama kali saat valid dipilih
                            if (firstSelection) {
                                firstSelection = false; // Tandai bahwa sudah memilih yang valid
                            }
                        } else {
                            // Jika memilih "Silakan pilih kategori...", tampilkan pesan hanya sekali
                            if (!firstSelection) {
//                                JOptionPane.showMessageDialog(null, "Silakan pilih item yang valid!");
                                txtIdKategori.setText("0"); // Reset teks di txtIdKategori
                                labelKategori.setText("anda belum memilih..");
                            }

                            // Jangan reset cbKategori jika sudah memilih yang valid sebelumnya
                            // biarkan pengguna memilih tanpa reset otomatis
                        }
                    }
                });

                // Mengambil data lebih lanjut jika diperlukan
                getData();

            } catch (SQLException | HeadlessException e) {
                // Tangani exception jika ada kesalahan
                e.printStackTrace();
            }
        }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollBar1 = new javax.swing.JScrollBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        textNama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        textHBeli = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        textHJual = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        spinStok = new javax.swing.JSpinner();
        cbKategori = new javax.swing.JComboBox<>();
        btnBersih = new javax.swing.JButton();
        btnSubmit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        produkTable = new javax.swing.JTable();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        txtIdProduk = new javax.swing.JTextField();
        txtIdKategori = new javax.swing.JTextField();
        labelKategori = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("Daftar Produk");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Nama Produk");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText("Kategori");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("Harga Beli");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Harga Jual");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Stok");

        cbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbKategoriActionPerformed(evt);
            }
        });

        btnBersih.setText("Bersihkan");
        btnBersih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBersihActionPerformed(evt);
            }
        });

        btnSubmit.setText("Tambahkan/Update");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnKembali.setText("Kembali");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        produkTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        produkTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                produkTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(produkTable);

        txtCari.setText("cari produk..");

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        labelKategori.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        labelKategori.setText("anda belum memilih..");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCari))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnBersih)
                                    .addComponent(btnHapus))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnKembali)
                                    .addComponent(btnSubmit)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(textNama)
                                    .addComponent(textHBeli, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                    .addComponent(textHJual, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                    .addComponent(spinStok)
                                    .addComponent(cbKategori, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(labelKategori))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtIdKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addComponent(jScrollBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(textNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelKategori))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(textHBeli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(textHJual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(spinStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnBersih)
                            .addComponent(btnSubmit))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnHapus)
                            .addComponent(btnKembali))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 449, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
        dispose();
        
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
//
//        // Mendapatkan kategori (panggil metode getKategori() jika perlu)
        String inputHBeli = textHBeli.getText();
        String inputHJual = textHJual.getText();
        String pesan;

        try (Connection conn = Connections.ConnectionDB()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Koneksi ke database gagal.");
                return;
            }

            java.sql.Statement stmt = conn.createStatement();
            String sql;
            // Validasi idKategori
            String idKategoriStr = txtIdKategori.getText();
            if (idKategoriStr == null || idKategoriStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Silakan pilih kategori terlebih dahulu!");
                return;
            }

            int idKategoriItems = Integer.parseInt(idKategoriStr);

            // Pastikan idKategori ada di tabel kategori
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM kategori WHERE idKategori = " + idKategoriItems);
            if (rs.next() && rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(null, "Kategori yang dipilih tidak valid!");
                return;
            }
            
            if (dataBaru) { // Proses simpan data baru
                sql = "INSERT INTO produk (namaProduk, idKategori, hargaBeli, hargaJual, stok, created_at, updated_at) "
                    + "VALUES ('" + textNama.getText() + "', "
                    + idKategoriItems + ", "
                    + Double.parseDouble(inputHBeli) + ", "
                    + Double.parseDouble(inputHJual) + ", "
                    + (int) spinStok.getValue() + ", NOW(), NOW())";
               pesan = "Produk telah berhasil ditambahkan!";
            } else { // Proses update data
                sql = "UPDATE produk SET namaProduk='" + textNama.getText() + "', updated_at=NOW() "
                    + "WHERE idProduk=" + Integer.parseInt(txtIdProduk.getText());
                pesan = "Produk telah berhasil diperbarui!";
            }
            
            stmt.executeUpdate(sql);
            JOptionPane.showMessageDialog(null, pesan);

            getData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan dalam koneksi atau eksekusi query: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Input tidak valid: " + e.getMessage());
            e.printStackTrace();
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Kesalahan pada GUI: " + e.getMessage());
            e.printStackTrace();
        }








        
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void cbKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbKategoriActionPerformed
      getKategori();
    }//GEN-LAST:event_cbKategoriActionPerformed

    private void btnBersihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihActionPerformed
        // TODO add your handling code here:
        textNama.setText("");
        textHBeli.setText("");
        textHJual.setText("");
    }//GEN-LAST:event_btnBersihActionPerformed

    private void produkTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_produkTableMouseClicked
        // TODO add your handling code here:
        dataBaru = false; // menampilkan data ke textboxt
        try {
            int row =produkTable.getSelectedRow();
            String clickedTable=(produkTable.getModel().getValueAt(row, 1).toString());
            txtIdProduk.setText(clickedTable);
            java.sql.Connection conn = (Connection) Connections.ConnectionDB();
            java.sql.Statement stm = conn.createStatement();
            java.sql.ResultSet sql = stm.executeQuery("select * from produk where idProduk='"+clickedTable+"'");
            if(sql.next()){
                String nama = sql.getString("namaProduk");
                textNama.setText(nama);
                String hBeli = sql.getString("hargaBeli");
                textHBeli.setText(hBeli);
                String hJual = sql.getString("hargaJual");
                textHJual.setText(hJual);
            }
        } catch (SQLException e) {}
    }//GEN-LAST:event_produkTableMouseClicked

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
                try { // hapus data
                    String sql ="delete from produk where idProduk='"+txtIdProduk.getText()+"'";
                    Connection conn = (Connection) Connections.ConnectionDB();
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    pst.execute();
                    JOptionPane.showMessageDialog(null, "Data telah berhasil dihapus..");

                    dataBaru=true;

                    // kosongkan data
                    textNama.setText("");
                    txtIdKategori.setText("");

                } catch (SQLException | HeadlessException e) {}

        getData();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        // TODO add your handling code here:
        try {
            // Ambil nilai pencarian dari txtCari
            String keyword = txtCari.getText();

            // SQL query untuk mencari data yang sesuai dengan keyword
            String sql = "SELECT * FROM produk WHERE namaProduk LIKE ?";

            // Buat koneksi ke database
            Connection conn = (Connection) Connections.ConnectionDB();

            // Gunakan PreparedStatement untuk menghindari SQL injection
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);

            // Menggunakan parameter LIKE dengan tanda '%' untuk mencari data yang mengandung keyword
            pst.setString(1, "%" + keyword + "%");

            // Eksekusi query
            java.sql.ResultSet rs = pst.executeQuery();

            // Periksa apakah hasil query mengembalikan data
            if (!rs.isBeforeFirst()) {
                // Jika tidak ada data
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan!");
            } else {
                // Jika ada data, tampilkan ke dalam kategoriProdukTable
                produkTable.setModel(DbUtils.resultSetToTableModel(rs));
            }

        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage());
        }
        
    }//GEN-LAST:event_btnCariActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersih;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JComboBox<String> cbKategori;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelKategori;
    private javax.swing.JTable produkTable;
    private javax.swing.JSpinner spinStok;
    private javax.swing.JTextField textHBeli;
    private javax.swing.JTextField textHJual;
    private javax.swing.JTextField textNama;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtIdKategori;
    private javax.swing.JTextField txtIdProduk;
    // End of variables declaration//GEN-END:variables
}
