/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Project.Pages.Pembelian;

import Project.Connection.Connections;
import Project.Helper.CurrencyFormat;
import Project.Helper.Tax;
import Project.Index;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;


/**
 *
 * @author brsap
 */
public class PembelianMenu extends javax.swing.JInternalFrame {
    private Map<String, String> produkMap = new HashMap<>();
    private Map<String, String> hargaMap = new HashMap<>();
    private Map<String, String> stokMap = new HashMap<>();
    CurrencyFormat formatIDCurrency = new CurrencyFormat();
    ArrayList<Integer> listIdAgen = new ArrayList<>();
    ArrayList<Integer> listIdProduk = new ArrayList<>();
    Tax taxInformation = new Tax();
    
    private Index halamanUtama;
    /**
     * Creates new form Pembelian
     */
    public boolean dataBaru;
    public PembelianMenu() {
        this.halamanUtama = Index.instance; // ✅ sekarang aman
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
            model.addColumn("Jumlah");
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
                    sql.getInt("jumlah"), // Menambahkan kategori
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
                listIdAgen.clear(); // reset mapping id
                cbAgen.addItem("Silakan pilih agen...");

                // Menyimpan idProduk dalam map untuk memetakan namaProduk ke idProduk
                Map<String, String> AgenMap = new HashMap<>();

                // Menambahkan data ke dalam model
                while (queryProduk.next()) {
                    String idAgen = queryProduk.getString("idAgen");
                    String namaAgen = queryProduk.getString("namaAgen");
                    AgenMap.put(namaAgen, idAgen);  // Menyimpan mapping antara nama dan idProduk
                    cbAgen.addItem(namaAgen);
                    listIdAgen.add(Integer.parseInt(idAgen));  
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
                spinJProduk.setValue(1);
                txtJumlahP.setText("");
                txtSubHarga.setText("");
                txtVisualSHarga.setText("Rp 0.000");
                txtVisualTHarga.setText("Rp 0.000");
                txtStok.setText("");
                txtIdAgen.setText("");
                txtIdProduk.setText("");
                txtHarga.setText("");
                cbAgen.setSelectedIndex(0);
                cbProduk.setSelectedIndex(0);
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
                listIdProduk.add(Integer.parseInt(idProduk));
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

        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        cbAgen = new javax.swing.JComboBox<>();
        cbProduk = new javax.swing.JComboBox<>();
        spinJProduk = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnKembali = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        btnSubmit = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtVisualTHarga = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtVisualSHarga = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pembelianTable = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        txtStok = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        txtSubHarga = new javax.swing.JTextField();
        txtTotalHarga = new javax.swing.JTextField();
        txtIdProduk = new javax.swing.JTextField();
        txtIdAgen = new javax.swing.JTextField();
        txtIdPembelian = new javax.swing.JTextField();
        txtJumlahP = new javax.swing.JTextField();

        jPanel2.setBackground(new java.awt.Color(255, 204, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        cbAgen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Yeni" }));

        cbProduk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel5.setText("Agen");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addGap(54, 54, 54)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbProduk, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbAgen, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spinJProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel6))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(cbAgen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinJProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        btnKembali.setBackground(new java.awt.Color(245, 245, 245));
        btnKembali.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
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

        btnBersihkan.setBackground(new java.awt.Color(204, 255, 255));
        btnBersihkan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBersihkan.setText("Bersihkan");
        btnBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBersihkanActionPerformed(evt);
            }
        });

        btnSubmit.setBackground(new java.awt.Color(204, 255, 204));
        btnSubmit.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSubmit.setText("Tambahkan");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnKembali, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnBersihkan, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBersihkan, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 236, 255));

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

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel7.setText("Total Harga");

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

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel8.setText("Sub Total");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel1.setText("Pembelian Produk");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        pembelianTable.setBackground(new java.awt.Color(255, 239, 255));
        pembelianTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Produk", "Jumlah", "Harga"
            }
        ));
        pembelianTable.setSelectionBackground(new java.awt.Color(255, 102, 204));
        pembelianTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pembelianTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(pembelianTable);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 673, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 204, 255));

        txtStok.setVisible(false);

        txtHarga.setVisible(false);

        txtSubHarga.setVisible(false);

        txtTotalHarga.setVisible(false);

        txtIdProduk.setVisible(false);

        txtIdAgen.setVisible(false);

        txtIdPembelian.setVisible(false);
        txtIdPembelian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdPembelianActionPerformed(evt);
            }
        });

        txtJumlahP.setVisible(false);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(txtIdAgen, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(txtIdPembelian, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJumlahP, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtSubHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdPembelian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJumlahP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdAgen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSubHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
                try { // hapus data
                    String queryDeleteDetailPembelian ="delete from detail_pembelian where idPembelian='"+txtIdPembelian.getText()+"'";
                    String queryDeletePembelian ="delete from pembelian where idPembelian='"+txtIdPembelian.getText()+"'";
                    Connection conn = (Connection) Connections.ConnectionDB();
                    
                    int idProduk = Integer.parseInt(txtIdProduk.getText());
                    int stok = Integer.parseInt(txtStok.getText());
                    int jumlahProduk = Integer.parseInt(txtJumlahP.getText());
                    int totalStok = stok - jumlahProduk;

                    // proses hapus detail pembelian
                    java.sql.PreparedStatement pstDeleteDetailPembelian = conn.prepareStatement(queryDeleteDetailPembelian);
                    pstDeleteDetailPembelian.execute();
                    
                    if (totalStok > 0)
                    {
                     String queryUpdateStok = "update produk set stok = ? where idProduk = ?";
                        // update stok produk
                        PreparedStatement pstUpdateStok = conn.prepareStatement(queryUpdateStok);
                        pstUpdateStok.setInt(1, totalStok);
                        pstUpdateStok.setInt(2, idProduk);
                        pstUpdateStok.executeUpdate();
                    }else{
                     String queryUpdateStok = "update produk set stok = ? where idProduk = ?";
                        // update stok produk
                        PreparedStatement pstUpdateStok = conn.prepareStatement(queryUpdateStok);
                        pstUpdateStok.setInt(1, 0);
                        pstUpdateStok.setInt(2, idProduk);
                        pstUpdateStok.executeUpdate();
                    }
                    
                    // proses hapus pembelian
                    java.sql.PreparedStatement pstDeletePembelian = conn.prepareStatement(queryDeletePembelian);
                    pstDeletePembelian.execute();
                    JOptionPane.showMessageDialog(null, "Data berhasil dihapus..");
                    txtVisualSHarga.setText("Rp 0.000");
                    txtVisualTHarga.setText("Rp 0.000");
                    dataBaru=true;

                    
                    

                } catch (SQLException | HeadlessException e) {}

                getData();
                getProduk();
                if (halamanUtama != null) {
                    halamanUtama.getPengeluaran();
                }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihkanActionPerformed
        // TODO add your handling code here:
        txtIdPembelian.setText("");
        getData();
        getProduk();
        
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
        
        else {
            
            txtIdPembelian.setText("");
            JOptionPane.showMessageDialog(null, "Pembelian anda gagal, silahkan untuk bersihkan terlebih dahulu");
        }
        
        getData();
        getProduk();
        halamanUtama.getPengeluaran();

        
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
                String idPembelian = sql.getString("idPembelian");
                int idAgen = sql.getInt("idAgen");
                int idProduk = sql.getInt("idProduk");
                int jumlah = sql.getInt("jumlah");
                txtIdPembelian.setText(idPembelian);
                int indexAgen = listIdAgen.indexOf(idAgen) + 1;
                int indexProduk = listIdProduk.indexOf(idProduk) + 1;

                if (indexAgen != -1) {
                    cbAgen.setSelectedIndex(indexAgen); // pilih item sesuai index
                }
                
                if (indexProduk != -1) {
                    cbProduk.setSelectedIndex(indexProduk); // pilih item sesuai index
                }

                // Validasi index
                if (idProduk < cbProduk.getItemCount()) {
                    cbProduk.setSelectedIndex(idProduk);
                }
                


                if (jumlah >= 1) {
                    txtJumlahP.setText(String.valueOf(jumlah));
                    spinJProduk.setValue(jumlah);
                }
                
                System.out.println("ID Produk: " + idProduk);
                System.out.println("ID Agen: " + idAgen);
                System.out.println("Jumlah: " + String.valueOf(jumlah));
            } else {
                System.out.println("Tidak ada data ditemukan untuk ID tersebut.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_pembelianTableMouseClicked

    private void txtIdPembelianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdPembelianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdPembelianActionPerformed


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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable pembelianTable;
    private javax.swing.JSpinner spinJProduk;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtIdAgen;
    private javax.swing.JTextField txtIdPembelian;
    private javax.swing.JTextField txtIdProduk;
    private javax.swing.JTextField txtJumlahP;
    private javax.swing.JTextField txtStok;
    private javax.swing.JTextField txtSubHarga;
    private javax.swing.JTextField txtTotalHarga;
    private javax.swing.JLabel txtVisualSHarga;
    private javax.swing.JLabel txtVisualTHarga;
    // End of variables declaration//GEN-END:variables
}
