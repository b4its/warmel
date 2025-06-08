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
import java.sql.ResultSet;
import java.sql.Statement;
import Project.Helper.Tax;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author brsap
 */
public class KeuanganMenu extends javax.swing.JInternalFrame {
    CurrencyFormat formatIDCurrency = new CurrencyFormat();
    Tax pajak = new Tax();
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
        getSaldoBersih();

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
        
        private void getSaldoBersih()
        {
            try (Connection conn = (Connection) Connections.ConnectionDB()) {
                CurrencyFormat formatIDCurrency = new CurrencyFormat();

                // Langkah 1: Ambil total record dan saldo bersih (subtotal)
                String sql = "SELECT COUNT(*) AS totalRecord, SUM(subtotal) AS saldoBersih FROM detail_penjualan";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                if (rs.next()) {
                    int totalRecord = rs.getInt("totalRecord");       // total baris/record
                    double saldoBersih = rs.getDouble("saldoBersih"); // jumlah semua subtotal

                    // Langkah 2: Hitung tax dan saldo akhir
                    double totalTax = totalRecord * pajak.getTax();
                    double saldoSetelahTax = saldoBersih - totalTax;

                    // Langkah 3: Tampilkan ke label
                    labelSaldoBersih.setText(
                        (saldoSetelahTax != 0 ? "Rp " + formatIDCurrency.currencyFormat(saldoSetelahTax) : "0.00")
                    );
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memuat data dari database.");
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
        jPanel3 = new javax.swing.JPanel();
        dateInput = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        btnTampilkan = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pemasukanTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        btnDetailPemasukan1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pengeluaranTable = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btnDetailPengeluaran = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtTablePemasukan = new javax.swing.JTextField();
        txtTablePengeluaran = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        labelSaldoBersih = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnKembali = new javax.swing.JButton();

        jPanel5.setBackground(new java.awt.Color(255, 204, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel2.setText("Pilih Tanggal");

        btnTampilkan.setBackground(new java.awt.Color(204, 255, 255));
        btnTampilkan.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        btnTampilkan.setText("Lihat Data");
        btnTampilkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilkanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addComponent(dateInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTampilkan, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTampilkan, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Dubai", 1, 24)); // NOI18N
        jLabel1.setText("Keuangan");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addContainerGap(117, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(41, 41, 41))
        );

        jPanel4.setBackground(new java.awt.Color(227, 255, 227));

        pemasukanTable.setBackground(new java.awt.Color(242, 255, 236));
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

        jLabel3.setFont(new java.awt.Font("Dubai", 1, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 102, 0));
        jLabel3.setText("Transaksi Masuk");

        btnDetailPemasukan1.setBackground(new java.awt.Color(204, 255, 153));
        btnDetailPemasukan1.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        btnDetailPemasukan1.setForeground(new java.awt.Color(51, 102, 0));
        btnDetailPemasukan1.setText("Detail Pemasukan");
        btnDetailPemasukan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailPemasukan1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnDetailPemasukan1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE))
                .addContainerGap(58, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDetailPemasukan1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        jPanel6.setBackground(new java.awt.Color(255, 222, 222));

        pengeluaranTable.setBackground(new java.awt.Color(255, 243, 243));
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

        jLabel4.setFont(new java.awt.Font("Dubai", 1, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 0, 51));
        jLabel4.setText("Transaksi Keluar");

        btnDetailPengeluaran.setBackground(new java.awt.Color(255, 204, 204));
        btnDetailPengeluaran.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        btnDetailPengeluaran.setForeground(new java.awt.Color(102, 0, 51));
        btnDetailPengeluaran.setText("Detail Pengeluaran");
        btnDetailPengeluaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailPengeluaranActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                    .addComponent(btnDetailPengeluaran, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDetailPengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 204, 255));

        txtTablePemasukan.setVisible(false);

        txtTablePengeluaran.setVisible(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTablePemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTablePengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtTablePemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTablePengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 204, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        labelSaldoBersih.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        labelSaldoBersih.setForeground(new java.awt.Color(0, 102, 0));
        labelSaldoBersih.setText("Rp 0.000");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel7.setText("Saldo Bersih:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(labelSaldoBersih, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(labelSaldoBersih, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        btnKembali.setBackground(new java.awt.Color(204, 255, 255));
        btnKembali.setFont(new java.awt.Font("Dubai", 1, 16)); // NOI18N
        btnKembali.setText("Kembali");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnKembali, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(68, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(255, 255, 255)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(82, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(243, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void btnTampilkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilkanActionPerformed
        // TODO add your handling code here:
            Date tanggalDipilih = dateInput.getDate();

            if (tanggalDipilih != null) {
                // Format tanggal ke string
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String tanggalFormatted = sdf.format(tanggalDipilih);
                JOptionPane.showMessageDialog(null, "Tanggal yang dipilih: " + tanggalFormatted);
            } else {
                JOptionPane.showMessageDialog(null, "Silakan pilih tanggal terlebih dahulu.");
            }
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
    private com.toedter.calendar.JDateChooser dateInput;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelSaldoBersih;
    private javax.swing.JTable pemasukanTable;
    private javax.swing.JTable pengeluaranTable;
    private javax.swing.JTextField txtTablePemasukan;
    private javax.swing.JTextField txtTablePengeluaran;
    // End of variables declaration//GEN-END:variables
}
