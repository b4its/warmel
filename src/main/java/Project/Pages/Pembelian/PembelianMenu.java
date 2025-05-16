/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Project.Pages.Pembelian;

import Project.Connection.Connections;
import Project.Helper.CurrencyFormat;
import Project.Helper.Tax;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static java.time.LocalDate.now;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author brsap
 */
public class PembelianMenu extends javax.swing.JInternalFrame {
    private Map<String, String> produkMap = new HashMap<>();
    private Map<String, String> hargaMap = new HashMap<>();
    private Map<String, String> stokMap = new HashMap<>();
    CurrencyFormat formatIDCurrency = new CurrencyFormat();
    Tax taxInformation = new Tax();
    /**
     * Creates new form Pembelian
     */
    public boolean dataBaru;
    public PembelianMenu() {

        initComponents();
        AutoCompleteDecorator.decorate(cbAgen);
        AutoCompleteDecorator.decorate(cbProduk);

        getAgen();
        getProduk();
        getData();
        dataBaru = true;
    }
    
        private void getData() {

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
                "SELECT \n" +
                "    pembelian.idPembelian,\n" +
                "    pembelian.created_at AS tanggal_pembelian,\n" +
                "    pembelian.keterangan,\n" +
                "    pembelian.totalHarga,\n" +
                "    agen.idAgen,\n" +
                "    agen.namaAgen,\n" +
                "    agen.alamat,\n" +
                "    detail_pembelian.idDetailPembelian,\n" +
                "    detail_pembelian.idProduk,\n" +
                "    detail_pembelian.jumlah,\n" +
                "    detail_pembelian.subtotal,\n" +
                "    produk.namaProduk,\n" +
                "    produk.stok\n" + // asumsikan kamu ingin stok produk juga
                "FROM pembelian\n" +
                "JOIN agen ON pembelian.idAgen = agen.idAgen\n" +
                "JOIN detail_pembelian ON pembelian.idPembelian = detail_pembelian.idPembelian\n" +
                "JOIN produk ON detail_pembelian.idProduk = produk.idProduk"
            );

            // Memeriksa apakah query mengembalikan hasil
            if (!sql.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "Tidak ada data yang ditemukan.");
                return;
            }

            // Membuat model tabel untuk menampilkan data
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("No");
            model.addColumn("Id Pembelian");
            model.addColumn("Nama Agen");
            model.addColumn("Produk");
            model.addColumn("Stok");
            model.addColumn("Sub Total");
            model.addColumn("Total Keseluruhan");

            // Menambahkan data ke dalam model
            int no = 1;  // Variabel untuk nomor urut

            // Menambahkan data dari ResultSet ke model tabel
            while (sql.next()) {
                model.addRow(new Object[]{
                    no++, // Menambahkan nomor urut
                    sql.getInt("idPembelian"), // Menambahkan nama produk
                    sql.getString("namaAgen"), // Menambahkan nama produk
                    sql.getString("namaProduk"), // Menambahkan kategori
                    sql.getInt("stok"), // Menambahkan kategori
                    formatIDCurrency.currencyFormat(sql.getDouble("subtotal")), // Menambahkan kategori
                    formatIDCurrency.currencyFormat(sql.getDouble("totalHarga")),
                });
            }

            // Menampilkan model ke dalam tabel
            pembelianTable.setModel(model);
            
            //sembunyikan idPembelian
            pembelianTable.getColumnModel().getColumn(1).setMinWidth(0);
            pembelianTable.getColumnModel().getColumn(1).setMaxWidth(0);
            pembelianTable.getColumnModel().getColumn(1).setWidth(0);

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

    
    private void getAgen() {
            try (Connection conn = (Connection) Connections.ConnectionDB();
                 java.sql.Statement stmAgen = conn.createStatement();
                 java.sql.ResultSet queryProduk = stmAgen.executeQuery("select idAgen, namaAgen from agen")) {

                cbAgen.removeAllItems();
                cbAgen.addItem("Silakan pilih agen...");

                // Menyimpan idProduk dalam map untuk memetakan namaProduk ke idProduk
                Map<String, String> AgenMap = new HashMap<>();

                // Menambahkan data ke dalam model
                while (queryProduk.next()) {
                    String idAgen = queryProduk.getString("idAgen");
                    String namaAgen = queryProduk.getString("namaAgen");
                    AgenMap.put(namaAgen, idAgen);  // Menyimpan mapping antara nama dan idProduk
                    cbAgen.addItem(namaAgen);
                }

                // Menambahkan listener untuk combo box
                cbAgen.addActionListener(new ActionListener() {
                    private boolean firstSelection = true; // Flag untuk menangani pilihan pertama

                    public void actionPerformed(ActionEvent e) {
                        String selectedProduk = (String) cbAgen.getSelectedItem();

                        // Cek apakah kategori yang valid dipilih
                        if (selectedProduk != null && !selectedProduk.equals("Silakan pilih produk...")) {
                            // Setel idProduk ke txtIdProduk
                            String idAgen = AgenMap.get(selectedProduk);
                            txtIdAgen.setText(idAgen);


                            // Hanya set pertama kali saat valid dipilih
                            if (firstSelection) {
                                firstSelection = false; // Tandai bahwa sudah memilih yang valid
                            }
                        } else {
                            // Jika memilih "Silakan pilih kategori...", tampilkan pesan hanya sekali
                            if (!firstSelection) {
//                                JOptionPane.showMessageDialog(null, "Silakan pilih item yang valid!");
                                txtIdAgen.setText("0"); // Reset teks di txtIdProduk

                            }

                            // Jangan reset cbProduk jika sudah memilih yang valid sebelumnya
                            // biarkan pengguna memilih tanpa reset otomatis
                        }
                    }
                });

                // Mengambil data lebih lanjut jika diperlukan
                // getData();

            } catch (SQLException | HeadlessException e) {
                // Tangani exception jika ada kesalahan
                e.printStackTrace();
            }
        }


    
    private void getProduk() {
        try (Connection conn = (Connection) Connections.ConnectionDB();
             java.sql.Statement stm = conn.createStatement();
             java.sql.ResultSet queryProduk = stm.executeQuery("SELECT idProduk, namaProduk, hargaBeli, stok FROM produk");
                ) {

            cbProduk.removeAllItems();
            cbProduk.addItem("Silakan pilih produk...");

            // Map untuk menyimpan idProduk dan hargaBeli berdasarkan namaProduk
            produkMap.clear();
            hargaMap.clear();
            stokMap.clear();

            while (queryProduk.next()) {
                String idProduk = queryProduk.getString("idProduk");
                String namaProduk = queryProduk.getString("namaProduk");
                String hargaBeli = queryProduk.getString("hargaBeli");
                String stok = queryProduk.getString("stok");

                produkMap.put(namaProduk, idProduk);
                hargaMap.put(namaProduk, hargaBeli);
                stokMap.put(namaProduk, stok);
                cbProduk.addItem(namaProduk);
            }


            cbProduk.addActionListener(new ActionListener() {
                private boolean firstSelection = true;

                public void actionPerformed(ActionEvent e) {
                    String selectedProduk = (String) cbProduk.getSelectedItem();

                    if (selectedProduk != null && !selectedProduk.equals("Silakan pilih produk...")) {
                        String idProduk = produkMap.get(selectedProduk);
                        txtHarga.setText(hargaMap.get(selectedProduk));
                        txtStok.setText(stokMap.get(selectedProduk));
                        double hargaBeli = Double.parseDouble(hargaMap.get(selectedProduk));
                        int jumlahBeli = (int) spinJProduk.getValue();
                        double subHarga = hargaBeli * jumlahBeli;
                        double total = subHarga + taxInformation.getTax();
                        double totalBiasa = hargaBeli + taxInformation.getTax();
                        String stringTotal = formatIDCurrency.currencyFormat(subHarga);
                        String stringTotalBiasa = formatIDCurrency.currencyFormat(totalBiasa);
                        txtIdProduk.setText(idProduk);
                        if (jumlahBeli > 1)
                        {
                            txtVisualSHarga.setText("Rp "+stringTotal);
                            txtSubHarga.setText(String.valueOf(subHarga));
                            txtTotalHarga.setText(String.valueOf(total)); // Menampilkan hargaBeli ke field txtHarga
                            txtVisualTHarga.setText("Rp "+stringTotalBiasa); // Menampilkan hargaBeli ke field txtHarga
                        }else{
                            txtVisualSHarga.setText("Rp "+stringTotal);
                            txtSubHarga.setText(hargaMap.get(selectedProduk));
                            txtTotalHarga.setText(hargaMap.get(selectedProduk)); // Menampilkan hargaBeli ke field txtHarga
                            txtVisualTHarga.setText("Rp "+stringTotalBiasa); // Menampilkan hargaBeli ke field txtHarga
                        }

                        if (firstSelection) {
                            firstSelection = false;
                        }
                    } else {
                        if (!firstSelection) {
                            txtIdProduk.setText("0");
                            txtTotalHarga.setText(""); // Reset harga jika pilihan tidak valid
                            txtVisualSHarga.setText("");
                        }
                    }
                }
            });

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

        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cbAgen = new javax.swing.JComboBox<>();
        cbProduk = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        spinJProduk = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        btnHapus = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();
        btnSubmit = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        pembelianTable = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtIdAgen = new javax.swing.JTextField();
        txtIdProduk = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        txtTotalHarga = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtSubHarga = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        txtVisualSHarga = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtVisualTHarga = new javax.swing.JLabel();
        txtStok = new javax.swing.JTextField();

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel1.setText("Form Pembelian");

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

        cbAgen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Yeni" }));

        cbProduk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel5.setText("Agen");

        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        spinJProduk.setModel(model);
        spinJProduk.setOpaque(true);
        spinJProduk.setVerifyInputWhenFocusTarget(false);
        spinJProduk.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinJProdukStateChanged(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel6.setText("Produk");

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

        btnSubmit.setText("Tambahkan/Update");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        btnBersihkan.setText("Bersihkan");
        btnBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBersihkanActionPerformed(evt);
            }
        });

        pembelianTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Produk", "Jumlah", "Harga"
            }
        ));
        pembelianTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pembelianTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(pembelianTable);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel7.setText("Total Harga");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel8.setText("Sub Total");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        txtVisualSHarga.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtVisualSHarga.setText("Rp 0.000");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtVisualSHarga, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtVisualSHarga)
                .addGap(0, 27, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        txtVisualTHarga.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtVisualTHarga.setText("Rp 0.000");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtVisualTHarga, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtVisualTHarga)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnBersihkan, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(btnSubmit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel5))
                                        .addGap(54, 54, 54)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(cbProduk, 0, 147, Short.MAX_VALUE)
                                            .addComponent(cbAgen, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(spinJProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel8)
                                            .addGap(18, 18, 18)
                                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel7)
                                            .addGap(18, 18, 18)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtSubHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(txtIdAgen, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(jLabel6))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(cbAgen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(spinJProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnBersihkan)
                            .addComponent(btnSubmit))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnHapus)
                            .addComponent(btnKembali))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIdAgen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtSubHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihkanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBersihkanActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
        int idAgen = Integer.parseInt(txtIdAgen.getText());
        int idProduk = Integer.parseInt(txtIdProduk.getText());
        String nama_agen = cbAgen.getSelectedItem().toString();
        String nama_produk = cbProduk.getSelectedItem().toString();
        int jumlah_produk = (int) spinJProduk.getValue();
        double harga = Double.parseDouble(txtHarga.getText());
        double subTotal = Double.parseDouble(txtSubHarga.getText());
        double totalHarga = Double.parseDouble(txtTotalHarga.getText());
        String keterangan = "Telah berhasil memesan Produk: " + nama_produk + "| Jumlah Produk: "+ jumlah_produk + "| Harga Satuan: "+ harga
                +"| dari agen "+nama_agen;
//        System.out.println("idAgen      : " + idAgen);
//        System.out.println("nama_agen   : " + nama_agen);
//        System.out.println("idProduk    : " + idProduk);
//        System.out.println("nama_produk   : " + nama_produk);
//        System.out.println("jumlahProduk: " + jumlah_produk);
//        System.out.println("harga    : " + harga);
//        System.out.println("subTotal    : " + subTotal);
//        System.out.println("totalHarga  : " + totalHarga);
//        System.out.println("keterangan  : " + keterangan);
        // TODO add your handling code here:
        if (dataBaru == true) {  // prosess simpan atau edit
            String queryPembelian = "INSERT INTO pembelian (idAgen, keterangan, totalHarga, created_at) VALUES (?, ?, ?, NOW())";

            try (
                Connection conn = Connections.ConnectionDB();
                PreparedStatement pstPembelian = conn.prepareStatement(queryPembelian, Statement.RETURN_GENERATED_KEYS);
            ) {
                int stokAwal = Integer.parseInt(txtStok.getText());
                int totalStok = stokAwal + jumlah_produk;
                System.out.println("Total Stok: "+totalStok);
                if (totalStok > 0)
                {
                    pstPembelian.setInt(1, idAgen);
                    pstPembelian.setString(2, keterangan);
                    pstPembelian.setDouble(3, totalHarga);

                    pstPembelian.executeUpdate();

                    try (ResultSet generatedKeys = pstPembelian.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idPembelian = generatedKeys.getInt(1);
                            String queryUpdateStok = "update produk set stok = ? where idProduk = ?";
                                // update stok produk
                                PreparedStatement pstUpdateStok = conn.prepareStatement(queryUpdateStok);
                                pstUpdateStok.setInt(1, totalStok);
                                pstUpdateStok.setInt(2, idProduk);
                                pstUpdateStok.executeUpdate();
                                
                                // penambahan detail pembelian
                            String queryDetailPembelian = "INSERT INTO detail_pembelian (idPembelian, idProduk, jumlah, subtotal) VALUES (?, ?, ?, ?)";
                                PreparedStatement pstDetail_Pembelian = conn.prepareStatement(queryDetailPembelian);
                                pstDetail_Pembelian.setInt(1, idPembelian);
                                pstDetail_Pembelian.setInt(2, idProduk);
                                pstDetail_Pembelian.setInt(3, jumlah_produk);
                                pstDetail_Pembelian.setDouble(4, subTotal);
                                pstDetail_Pembelian.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Pembelian anda telah berhasil");
                        } else {
                            JOptionPane.showMessageDialog(null, "Pembelian anda gagal");

                            System.out.println("ID pembelian tidak tersedia.");
                        }
                    }
            
                }
                
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
            }

        } 
//        else {
//            try {
//                String sql = "update kategori SET namaKategori='"+textNama.getText()+"', updated_at= now() where idKategori='"+txtIdKategori.getText()+"'";
//                Connection conn = (Connection) Connections.ConnectionDB();
//                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
//                pst.execute();
//                JOptionPane.showMessageDialog(null, "Kategori telah berhasil diperbarui");
//            } catch (SQLException | HeadlessException e) {
//                JOptionPane.showMessageDialog(null, e);
//            }
//        }
        
        getData();

        
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void spinJProdukStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinJProdukStateChanged
        // TODO add your handling code here:
        try {
 

            
            // Ambil nilai dari txtHarga dan spinner
            double hargaBeli = Double.parseDouble(txtHarga.getText());
            int jumlahBeli = (int) spinJProduk.getValue();
            System.out.println("jumlah beli: " + jumlahBeli);
            // txtIdProduk.setText(title);
            // Hitung total dan tampilkan
            double subTotal = hargaBeli * jumlahBeli;
            double total = subTotal + taxInformation.getTax();
            double totalBiasa = hargaBeli + taxInformation.getTax();
            String stringTotal = formatIDCurrency.currencyFormat(total);
            String stringTotalBiasa = formatIDCurrency.currencyFormat(totalBiasa);
            String stringSub = formatIDCurrency.currencyFormat(subTotal);
            System.out.println(stringTotal);
            if(jumlahBeli > 1)
            {
                txtVisualSHarga.setText("Rp "+stringSub);
                txtSubHarga.setText(String.valueOf(subTotal));
                txtTotalHarga.setText(String.valueOf(total));
                txtVisualTHarga.setText("Rp "+String.valueOf(stringTotal));
            }else
            {
                txtVisualSHarga.setText("Rp "+stringSub);
                txtSubHarga.setText(String.valueOf(hargaBeli));
                txtTotalHarga.setText(String.valueOf(hargaBeli));
                txtVisualTHarga.setText("Rp "+String.valueOf(stringTotalBiasa));   
            }


        } catch (NumberFormatException ex) {
            txtTotalHarga.setText("0.0"); // fallback jika txtHarga belum valid
            txtSubHarga.setText("0.0");
            txtVisualSHarga.setText("0.000");
            txtVisualTHarga.setText("0.000");
        }
    }//GEN-LAST:event_spinJProdukStateChanged

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void pembelianTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pembelianTableMouseClicked
        dataBaru = false;
        System.out.println("DONE BANG");

        try {
            int row = pembelianTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Silakan pilih data dari tabel.");
                return;
            }

            String tableClicked = pembelianTable.getModel().getValueAt(row, 1).toString();
            System.out.println("Clicked ID: " + tableClicked);

            Connection conn = Connections.ConnectionDB();
            String query = "select p.idPembelian, p.idAgen, dp.idProduk, dp.jumlah from pembelian p join detail_pembelian dp on p.idPembelian = dp.idPembelian where dp.idPembelian = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, tableClicked);
            ResultSet sql = ps.executeQuery();

            if (sql.next()) {
                int idAgen = sql.getInt("idAgen")-1;
                int idProduk = sql.getInt("idProduk")-1;
                int jumlah = sql.getInt("jumlah");

                // Validasi index
                if (idProduk < cbProduk.getItemCount()) {
                    cbProduk.setSelectedIndex(idProduk);
                }
                
                if (idAgen < cbAgen.getItemCount()) {
                    cbAgen.setSelectedIndex(idAgen);
                }

                if (jumlah >= 1) {
                    spinJProduk.setValue(jumlah);
                }

                System.out.println("ID Produk: " + idProduk);
                System.out.println("ID Agen: " + idAgen);
                System.out.println("Jumlah: " + jumlah);
            } else {
                System.out.println("Tidak ada data ditemukan untuk ID tersebut.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_pembelianTableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JComboBox<String> cbAgen;
    private javax.swing.JComboBox<String> cbProduk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable pembelianTable;
    private javax.swing.JSpinner spinJProduk;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtIdAgen;
    private javax.swing.JTextField txtIdProduk;
    private javax.swing.JTextField txtStok;
    private javax.swing.JTextField txtSubHarga;
    private javax.swing.JTextField txtTotalHarga;
    private javax.swing.JLabel txtVisualSHarga;
    private javax.swing.JLabel txtVisualTHarga;
    // End of variables declaration//GEN-END:variables
}
