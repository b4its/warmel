/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Project.Pages.Receipt;

import Project.Connection.Connections;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import Project.Helper.CurrencyFormat;
import Project.Pages.Produk.ProdukMenu;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

import Project.Index;
import Project.Pages.Receipt.Detail.DetailPemasukan;
import Project.Pages.Receipt.Detail.DetailPengeluaran;
/**
 *
 * @author brsap
 */
public class KeuanganMenu extends javax.swing.JInternalFrame {
    CurrencyFormat formatIDCurrency = new CurrencyFormat();
    /**
     * Creates new form keuanganMenu
     */
    private Index halamanUtama;
    public static KeuanganMenu instance;

    
    public KeuanganMenu() {
        initComponents();
        instance = this; // ✅ set instance saat objek dibuat
        this.halamanUtama = Index.instance; // ✅ sekarang aman
        getPemasukanData();
        getPengeluaranData();

    }
        private void getPemasukanData()
    {
        // menampilkan data dari database
        try 
        {
            Connection conn = (Connection) Connections.ConnectionDB();
            java.sql.Statement stm = conn.createStatement();
            java.sql.ResultSet sql = stm.executeQuery("select idPenjualan, totalHarga, created_at from penjualan");
            
            // Membuat model tabel untuk menampilkan data
            DefaultTableModel model = new DefaultTableModel();

            // Menambahkan kolom baru untuk 'no'
            model.addColumn("No");
            model.addColumn("ID Pemasukan");
            model.addColumn("Kode Transaksi Masuk");
            model.addColumn("Total Pemasukan");
            
  

            // Menambahkan data ke dalam model
            int no = 1;  // Variabel untuk nomor urut
            while (sql.next()) {
                String idPenjualan = sql.getString("idPenjualan");
                String created_at = sql.getString("created_at");
                
                // Parse string menjadi LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(created_at, formatter);

                // Ambil komponen
                int minute = dateTime.getMinute();       // 55
                int hour = dateTime.getHour();           // 7
                int dayOfMonth = dateTime.getDayOfMonth(); // 26
                int month = dateTime.getMonthValue();    // 5

                // Gabungkan jadi satu string sesuai format Anda
                String codeCreatedAt = String.format("%02d%02d%02d%d", minute, hour, dayOfMonth, month);
                String codePemasukan = "IN"+codeCreatedAt+"-"+idPenjualan;
                String totalPemasukan = formatIDCurrency.currencyFormat(sql.getDouble("totalHarga"));
                
                //debugging
                    System.out.println("==========================");
                    System.out.println("Kode Pemasukan: " + codePemasukan);
                    System.out.println("Created At: " + created_at);
                    System.out.println("Total Pemasukan: " + totalPemasukan);
                model.addRow(new Object[] {
                    no++, // Menambahkan nomor urut
                     // Menambahkan nama kategori
                    idPenjualan,
                    codePemasukan,
                    totalPemasukan   // Menambahkan created_at
                });
            }

            // Menampilkan model ke dalam tabel
            pemasukanTable.setModel(model);
            
            //sembunyikan idPenjualan
                pemasukanTable.getColumnModel().getColumn(1).setMinWidth(0);
                pemasukanTable.getColumnModel().getColumn(1).setMaxWidth(0);
                pemasukanTable.getColumnModel().getColumn(1).setWidth(0);
            
        }
        catch (SQLException | HeadlessException e) 
        {
        }
    }

        private void getPengeluaranData()
    {
        // menampilkan data dari database
        try 
        {
            Connection conn = (Connection) Connections.ConnectionDB();
            java.sql.Statement stm = conn.createStatement();
            java.sql.ResultSet sql = stm.executeQuery("select idPembelian, idAgen, totalHarga, created_at from pembelian");
            
            // Membuat model tabel untuk menampilkan data
            DefaultTableModel model = new DefaultTableModel();

            // Menambahkan kolom baru untuk 'no'
            model.addColumn("No");
            model.addColumn("ID Pembelian");
            model.addColumn("Kode Transaksi Keluar");
            model.addColumn("Total Pengeluaran");
            
  

            // Menambahkan data ke dalam model
            int no = 1;  // Variabel untuk nomor urut
            while (sql.next()) {
                String idPembelian = sql.getString("idPembelian");
                String idAgen = sql.getString("idAgen");
                String created_at = sql.getString("created_at");
                
                // Parse string menjadi LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(created_at, formatter);

                // Ambil komponen
                int minute = dateTime.getMinute();       // 55
                int hour = dateTime.getHour();           // 7
                int dayOfMonth = dateTime.getDayOfMonth(); // 26
                int month = dateTime.getMonthValue();    // 5

                // Gabungkan jadi satu string sesuai format Anda
                String codeCreatedAt = String.format("%02d%02d%02d%d", minute, hour, dayOfMonth, month);
                String codePengeluaran = "OUT"+codeCreatedAt+"-"+idPembelian+idAgen;
                String totalPengeluaran = formatIDCurrency.currencyFormat(sql.getDouble("totalHarga"));
                
                //debugging
                    System.out.println("==========================");
                    System.out.println("Kode Pengeluaran: " + codePengeluaran);
                    System.out.println("Created At: " + created_at);
                    System.out.println("Total Pengeluaran: " + totalPengeluaran);
                model.addRow(new Object[] {
                    no++, // Menambahkan nomor urut
                    idPembelian,
                    codePengeluaran,
                    totalPengeluaran   // Menambahkan created_at
                });
            }

            // Menampilkan model ke dalam tabel
            pengeluaranTable.setModel(model);
            //sembunyikan idPenjualan
                pengeluaranTable.getColumnModel().getColumn(1).setMinWidth(0);
                pengeluaranTable.getColumnModel().getColumn(1).setMaxWidth(0);
                pengeluaranTable.getColumnModel().getColumn(1).setWidth(0);
            
        }
        catch (SQLException | HeadlessException e) 
        {
        }
    }


        public String getIdPemasukan(){
            String idPemasukan = txtTablePemasukan.getText();
            return idPemasukan;
        }
        
        public String getIdPengeluaran(){
            String idPengeluaran = txtTablePengeluaran.getText();
            return idPengeluaran;
        }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pengeluaranTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        btnKembali = new javax.swing.JButton();
        btnTampilkan = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pemasukanTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        labelSaldoBersih = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtTablePemasukan = new javax.swing.JTextField();
        txtTablePengeluaran = new javax.swing.JTextField();
        btnDetailPengeluaran = new javax.swing.JButton();
        btnDetailPemasukan1 = new javax.swing.JButton();

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setText("Transaksi Keluar");

        pengeluaranTable.setBackground(new java.awt.Color(255, 151, 151));
        pengeluaranTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Barang", "Harga Barang"
            }
        ));
        pengeluaranTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pengeluaranTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(pengeluaranTable);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel1.setText("Laporan Keuangan");

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel2.setText("Pilih Tanggal");

        jDateChooser1.setToolTipText("");
        jDateChooser1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        btnKembali.setBackground(new java.awt.Color(255, 204, 255));
        btnKembali.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnKembali.setText("Kembali");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        btnTampilkan.setBackground(new java.awt.Color(255, 204, 255));
        btnTampilkan.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnTampilkan.setText("Tampilkan");
        btnTampilkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilkanActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel3.setText("Transaksi Masuk");

        pemasukanTable.setBackground(new java.awt.Color(178, 255, 142));
        pemasukanTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Barang", "Harga Barang"
            }
        ));
        pemasukanTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pemasukanTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(pemasukanTable);

        jPanel1.setBackground(new java.awt.Color(255, 204, 255));

        labelSaldoBersih.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        labelSaldoBersih.setText("Rp 0.000");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setText("Saldo Bersih:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(labelSaldoBersih, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(labelSaldoBersih))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtTablePemasukan.setVisible(false);
        txtTablePemasukan.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        txtTablePengeluaran.setVisible(false);
        txtTablePengeluaran.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        btnDetailPengeluaran.setBackground(new java.awt.Color(255, 102, 102));
        btnDetailPengeluaran.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnDetailPengeluaran.setText("Detail Pengeluaran");
        btnDetailPengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailPengeluaranActionPerformed(evt);
            }
        });

        btnDetailPemasukan1.setBackground(new java.awt.Color(153, 255, 153));
        btnDetailPemasukan1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnDetailPemasukan1.setText("Detail Pemasukan");
        btnDetailPemasukan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailPemasukan1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                    .addComponent(btnDetailPemasukan1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane2)
                    .addComponent(btnDetailPengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 605, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(232, 232, 232))
            .addGroup(layout.createSequentialGroup()
                .addGap(175, 175, 175)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTampilkan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTablePemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtTablePengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addComponent(jLabel1))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(25, 25, 25)
                                    .addComponent(jLabel2)))
                            .addGap(0, 1205, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(btnKembali)))
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTablePemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTablePengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTampilkan)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDetailPemasukan1)
                    .addComponent(btnDetailPengeluaran))
                .addGap(22, 22, 22)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(33, 33, 33)
                    .addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 501, Short.MAX_VALUE)
                    .addComponent(btnKembali)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void btnTampilkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilkanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTampilkanActionPerformed

    private void pemasukanTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pemasukanTableMouseClicked
        // TODO add your handling code here:
        try {
            int row = pemasukanTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Silakan pilih data dari tabel.");
                return;
            }

            String tableClicked = pemasukanTable.getModel().getValueAt(row, 1).toString();
            txtTablePemasukan.setText(tableClicked);
            System.out.println("Clicked ID Pemasukan: " + tableClicked);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_pemasukanTableMouseClicked

    private void pengeluaranTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pengeluaranTableMouseClicked
        // TODO add your handling code here:
        try {
            int row = pengeluaranTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Silakan pilih data dari tabel.");
                return;
            }

            String tableClicked = pengeluaranTable.getModel().getValueAt(row, 1).toString();
            txtTablePengeluaran.setText(tableClicked);
            System.out.println("Clicked ID Pengeluaran: " + tableClicked);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_pengeluaranTableMouseClicked

    private void btnDetailPemasukan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetailPemasukan1ActionPerformed
        // TODO add your handling code here:
        DetailPemasukan frameTes = new DetailPemasukan();
        halamanUtama.panelViews.add(frameTes);
        frameTes.setVisible(true);
    }//GEN-LAST:event_btnDetailPemasukan1ActionPerformed

    private void btnDetailPengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetailPengeluaranActionPerformed
        // TODO add your handling code here:
        DetailPengeluaran frameTes = new DetailPengeluaran();
        halamanUtama.panelViews.add(frameTes);
        frameTes.setVisible(true);
    }//GEN-LAST:event_btnDetailPengeluaranActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDetailPemasukan1;
    private javax.swing.JButton btnDetailPengeluaran;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnTampilkan;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelSaldoBersih;
    private javax.swing.JTable pemasukanTable;
    private javax.swing.JTable pengeluaranTable;
    private javax.swing.JTextField txtTablePemasukan;
    private javax.swing.JTextField txtTablePengeluaran;
    // End of variables declaration//GEN-END:variables
}
