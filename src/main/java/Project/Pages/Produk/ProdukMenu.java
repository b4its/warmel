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
import Project.Index;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
    private final Map<String, String> kategoriMap = new HashMap<>();
    private final Map<String, String> getDataKategoriMap = new HashMap<>();


    /**
     * Creates new form 
     */
    private Index halamanUtama;
    public boolean dataBaru;
    public ProdukMenu() {
        
        initComponents();
        this.halamanUtama = Index.instance; 
      
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
                "INNER JOIN kategori k ON p.idKategori = k.idKategori " +
                "ORDER BY p.created_at DESC"
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
                    sql.getInt("stok") // Menambahkan stok
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
        try (Connection conn = Connections.ConnectionDB();
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT idKategori, namaKategori FROM kategori")) {

            txtIdKategori.setText("");
            cbKategori.removeAllItems();
            cbKategori.addItem("Silakan pilih kategori...");

            kategoriMap.clear();
            getDataKategoriMap.clear();

            while (rs.next()) {
                String idKategori = rs.getString("idKategori");
                String namaKategori = rs.getString("namaKategori");

                kategoriMap.put(namaKategori, idKategori);
                getDataKategoriMap.put(idKategori, namaKategori);

                cbKategori.addItem(namaKategori);
            }

            cbKategori.addActionListener(new ActionListener() {
                private boolean firstSelection = true;

                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedKategori = (String) cbKategori.getSelectedItem();

                    if (selectedKategori != null && !selectedKategori.equals("Silakan pilih kategori...")) {
                        String idKategori = kategoriMap.get(selectedKategori);
                        txtIdKategori.setText(idKategori);
                        firstSelection = false;
                    } else if (!firstSelection) {
                        txtIdKategori.setText("0");
                    }
                }
            });

            getData(); // opsional sesuai logikamu

        } catch (SQLException | HeadlessException e) {
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

        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cbKategori = new javax.swing.JComboBox<>();
        spinStok = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        textNama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        textHBeli = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        textHJual = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnSubmit = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBersih = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        produkTable = new javax.swing.JTable();
        btnCari = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        txtIdKategori = new javax.swing.JTextField();
        txtIdProduk = new javax.swing.JTextField();

        jPanel5.setBackground(new java.awt.Color(255, 204, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("Daftar Produk");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        cbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(textNama)
                    .addComponent(textHBeli)
                    .addComponent(textHJual)
                    .addComponent(spinStok)
                    .addComponent(cbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(217, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(textNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(textHBeli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(textHJual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(spinStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        btnSubmit.setBackground(new java.awt.Color(204, 255, 204));
        btnSubmit.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSubmit.setText("Tambahkan/Update");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        btnKembali.setBackground(new java.awt.Color(250, 250, 250));
        btnKembali.setText("Kembali");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(255, 204, 204));
        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnBersih.setBackground(new java.awt.Color(204, 255, 255));
        btnBersih.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBersih.setText("Bersihkan");
        btnBersih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBersihActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnKembali, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnBersih, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBersih, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        produkTable.setBackground(new java.awt.Color(255, 234, 255));
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

        btnCari.setBackground(new java.awt.Color(255, 204, 255));
        btnCari.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        txtCari.setText("Cari Produk");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 725, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCari, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(txtCari))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        txtIdKategori.setVisible(false);

        txtIdProduk.setVisible(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtIdKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
        halamanUtama.setVisible(false);
        setVisible(false);
        halamanUtama.setVisible(true);
        halamanUtama.dashboardViews();
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
                sql = "UPDATE produk SET "
                    + "namaProduk='" + textNama.getText() + "', "
                    + "idKategori=" + idKategoriItems + ", "
                    + "hargaBeli=" + Double.parseDouble(inputHBeli) + ", "
                    + "hargaJual=" + Double.parseDouble(inputHJual) + ", "
                    + "stok=" + (int) spinStok.getValue() + ", "
                    + "updated_at=NOW() "
                    + "WHERE idProduk=" + Integer.parseInt(txtIdProduk.getText());
                pesan = "Produk telah berhasil diperbarui!";
            }

            
            stmt.executeUpdate(sql);
            JOptionPane.showMessageDialog(null, pesan);

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


getData();





        
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnBersihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihActionPerformed
        // TODO add your handling code here:
        textNama.setText("");
        textHBeli.setText("");
        textHJual.setText("");
        txtIdProduk.setText("");
        txtCari.setText("");
        dataBaru = true;
        
        getData();
     
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
                int stok = sql.getInt("stok");
                spinStok.setValue(stok);
                
                String idKategori = sql.getString("idKategori");
                String namaKategori = getDataKategoriMap.get(idKategori);
                txtIdKategori.setText(idKategori);
                if (namaKategori != null) {
                    cbKategori.setSelectedItem(namaKategori);
                } else {
                    cbKategori.setSelectedIndex(0); // fallback ke default jika tidak ditemukan
                }
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
            String keyword = txtCari.getText();

            String sql = "SELECT p.idProduk, p.namaProduk, k.namaKategori, p.hargaBeli, p.hargaJual, p.stok, p.created_at, p.updated_at " +
                         "FROM produk p INNER JOIN kategori k ON p.idKategori = k.idKategori " +
                         "WHERE p.namaProduk LIKE ? ORDER BY p.created_at DESC";

            Connection conn = (Connection) Connections.ConnectionDB();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan!");
            } else {
                CurrencyFormat formatIDCurrency = new CurrencyFormat();

                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("No");
                model.addColumn("Id Produk");
                model.addColumn("Nama Produk");
                model.addColumn("Kategori");
                model.addColumn("Harga Beli");
                model.addColumn("Harga Jual");
                model.addColumn("Stok");


                int no = 1;
                while (rs.next()) {
                    model.addRow(new Object[]{
                        no++,
                        rs.getString("idProduk"),
                        rs.getString("namaProduk"),
                        rs.getString("namaKategori"),
                        formatIDCurrency.formatCurrency(rs.getDouble("hargaBeli")),
                        formatIDCurrency.formatCurrency(rs.getDouble("hargaJual")),
                        rs.getInt("stok")
                    });
                }

                produkTable.setModel(model);

                // Sembunyikan kolom idProduk
                produkTable.getColumnModel().getColumn(1).setMinWidth(0);
                produkTable.getColumnModel().getColumn(1).setMaxWidth(0);
                produkTable.getColumnModel().getColumn(1).setWidth(0);
            }

            rs.close();
            pst.close();
            conn.close();

        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
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
