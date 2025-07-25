/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Project.Pages.Penjualan;

import Project.Pages.Penjualan.*;
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
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;


/**
 *
 * @author brsap
 */
public class PenjualanMenu extends javax.swing.JInternalFrame {
    private Map<String, String> produkMap = new HashMap<>();
    private Map<String, String> hargaMap = new HashMap<>();
    private Map<String, String> stokMap = new HashMap<>();
    Map<Integer, Map<String, Object>> dataProdukMap = new HashMap<>();
    int indexProduk = 0; // penanda produk ke-i di keranjang
    
    CurrencyFormat formatIDCurrency = new CurrencyFormat();
    Tax taxInformation = new Tax();
    
    private Index halamanUtama;
    /**
     * Creates new form Penjualan
     */
    public boolean dataBaru;
    public PenjualanMenu() {
        this.halamanUtama = Index.instance; // ✅ sekarang aman
        initComponents();
        AutoCompleteDecorator.decorate(cbProduk);

//        getAgen();
        getProduk();
        getData();
        dataBaru = true;
        
    }
    

    private void getData() {

        // Membuat model tabel untuk menampilkan data
        DefaultTableModel model = new DefaultTableModel();

        // Cek apakah dataProdukMap kosong
        if (dataProdukMap == null || dataProdukMap.isEmpty()) {
            model.addColumn("Informasi");
            model.addRow(new Object[]{"Saat ini tidak ada data yang tersedia, silahkan untuk melakukan penjualan produk"});
            penjualanTable.setModel(model);
            return;
        }

        // Jika ada data, tampilkan normal
        model.addColumn("No");
        model.addColumn("ID Produk");
        model.addColumn("Nama Produk");
        model.addColumn("Jumlah");
        model.addColumn("Stok");
        model.addColumn("Sub Total");

        model.setRowCount(0); // Kosongkan isi tabel terlebih dahulu
        double totalSubTotal = 0.00;
        int number = 1;

        for (Map.Entry<Integer, Map<String, Object>> entry : dataProdukMap.entrySet()) {
            int no = number++;
            Map<String, Object> item = entry.getValue();

            int idProduk = (int) item.get("idProduk");
            String namaProduk = (String) item.get("namaProduk");
            int jumlah = (int) item.get("jumlah");
            int stok = (int) item.get("stok");
            double subTotal = (double) item.get("subTotal");

            totalSubTotal += subTotal;

            model.addRow(new Object[]{
                no, idProduk, namaProduk, jumlah, stok,
                formatIDCurrency.currencyFormat(subTotal)
            });
        }

        txtTotalHargaa.setText(String.valueOf(totalSubTotal));
        txtVisualTotalHarga.setText("Rp " + formatIDCurrency.currencyFormat(totalSubTotal));

        // Menampilkan model ke dalam tabel
        penjualanTable.setModel(model);

        // Sembunyikan kolom ID Produk
        penjualanTable.getColumnModel().getColumn(1).setMinWidth(0);
        penjualanTable.getColumnModel().getColumn(1).setMaxWidth(0);
        penjualanTable.getColumnModel().getColumn(1).setWidth(0);
    }



    
    private void getProduk() {
        try (Connection conn = (Connection) Connections.ConnectionDB();
             java.sql.Statement stm = conn.createStatement();
             java.sql.ResultSet queryProduk = stm.executeQuery("SELECT idProduk, namaProduk, hargaJual, stok FROM produk");
                ) {
                spinJProduk.setValue(1);
                txtJumlahP.setText("");
                txtSubHarga.setText("");
                txtVisualSHarga.setText("Rp 0.000");
                txtVisualTHarga.setText("Rp 0.000");
                txtStok.setText("");
                txtIdProduk.setText("");
                txtHarga.setText("");

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
                String hargaJual = queryProduk.getString("hargaJual");
                String stok = queryProduk.getString("stok");

                produkMap.put(namaProduk, idProduk);
                hargaMap.put(namaProduk, hargaJual);
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
                        double hargaJual = Double.parseDouble(hargaMap.get(selectedProduk));
                        int jumlahBeli = (int) spinJProduk.getValue();
                        double subHarga = hargaJual * jumlahBeli;
                        double total = subHarga + taxInformation.getTax();
                        double totalBiasa = hargaJual + taxInformation.getTax();
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
                            txtVisualSHarga.setText("Rp 0.000");
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

        jPanel8 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        cbProduk = new javax.swing.JComboBox<>();
        spinJProduk = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        btnHapus = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();
        btnTambahkan = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        txtTotalHarga = new javax.swing.JTextField();
        txtSubHarga = new javax.swing.JTextField();
        txtTotalHargaa = new javax.swing.JTextField();
        txtIdTabel = new javax.swing.JTextField();
        txtIdProduk = new javax.swing.JTextField();
        txtStok = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        txtIdPenjualan = new javax.swing.JTextField();
        txtJumlahP = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtVisualTotalHarga = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtVisualSHarga = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtVisualTHarga = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        penjualanTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtNominalBayar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnKonfirmasi = new javax.swing.JButton();

        jPanel8.setBackground(new java.awt.Color(255, 204, 255));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbProduk, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spinJProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cbProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinJProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        btnHapus.setBackground(new java.awt.Color(255, 204, 204));
        btnHapus.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnKembali.setBackground(new java.awt.Color(246, 246, 246));
        btnKembali.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        btnKembali.setText("Kembali");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        btnTambahkan.setBackground(new java.awt.Color(204, 255, 204));
        btnTambahkan.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        btnTambahkan.setText("Tambahkan");
        btnTambahkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahkanActionPerformed(evt);
            }
        });

        btnBersihkan.setBackground(new java.awt.Color(204, 255, 255));
        btnBersihkan.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        btnBersihkan.setText("Bersihkan");
        btnBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBersihkanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnKembali, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(btnBersihkan, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTambahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBersihkan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTambahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 204, 255));

        txtTotalHarga.setVisible(false);

        txtSubHarga.setVisible(false);

        txtTotalHargaa.setVisible(false);

        txtIdTabel.setVisible(false);

        txtIdProduk.setVisible(false);

        txtStok.setVisible(false);

        txtHarga.setVisible(false);

        txtIdPenjualan.setVisible(false);
        txtIdPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdPenjualanActionPerformed(evt);
            }
        });

        txtJumlahP.setVisible(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtIdTabel, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(txtTotalHargaa, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSubHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(txtIdPenjualan, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(188, 188, 188)
                        .addComponent(txtJumlahP, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtJumlahP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdPenjualan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdTabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(txtSubHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalHargaa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)))
                .addGap(22, 22, 22))
        );

        jPanel9.setBackground(new java.awt.Color(255, 239, 255));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel7.setText("Sub Total");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        txtVisualTotalHarga.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtVisualTotalHarga.setText("Rp 0.000");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtVisualTotalHarga, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtVisualTotalHarga)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel9.setText("Grand Total");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel8.setText("Harga");

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

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel8))
                    .addComponent(jLabel7)
                    .addComponent(jLabel9))
                .addGap(29, 29, 29)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Dubai", 1, 24)); // NOI18N
        jLabel1.setText("Penjualan Produk");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        penjualanTable.setBackground(new java.awt.Color(255, 241, 255));
        penjualanTable.setFont(new java.awt.Font("Dubai", 0, 14)); // NOI18N
        penjualanTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Produk", "Jumlah", "Harga"
            }
        ));
        penjualanTable.setSelectionBackground(new java.awt.Color(255, 204, 255));
        penjualanTable.setSelectionForeground(new java.awt.Color(153, 0, 153));
        penjualanTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                penjualanTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(penjualanTable);

        jPanel2.setBackground(new java.awt.Color(255, 241, 255));

        txtNominalBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNominalBayarActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        jLabel2.setText("Nominal Bayar");

        btnKonfirmasi.setBackground(new java.awt.Color(204, 255, 204));
        btnKonfirmasi.setFont(new java.awt.Font("Dubai", 1, 14)); // NOI18N
        btnKonfirmasi.setText("Bayar");
        btnKonfirmasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKonfirmasiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnKonfirmasi, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(txtNominalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtNominalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnKonfirmasi, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(59, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(119, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(63, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(55, 55, 55)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
                try { // hapus data
                    // Ambil nilai dari kolom ID produk (misalnya kolom ke-1 = index 1)
                    String idProdukClicked = txtIdTabel.getText();
                    System.out.println("Clicked ID: " + idProdukClicked);

                    // Cari key dari dataProdukMap yang memiliki idProduk sama
                    Integer keyToRemove = null;
                    for (Map.Entry<Integer, Map<String, Object>> entry : dataProdukMap.entrySet()) {
                        if (entry.getValue().get("idProduk").toString().equals(idProdukClicked)) {
                            keyToRemove = entry.getKey();
                            break;
                        }
                    }

                    // Hapus jika ditemukan
                    if (keyToRemove != null) {
                        dataProdukMap.remove(keyToRemove);
                        System.out.println("Data dengan ID " + idProdukClicked + " berhasil dihapus.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Data tidak ditemukan di Map.");
                    }
                    

                } catch (HeadlessException e) {}
                txtIdTabel.setText("");
                getData();
                getProduk();
                if (halamanUtama != null) {
                    halamanUtama.getPengeluaran();
                }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBersihkanActionPerformed
        // TODO add your handling code here:
        txtIdPenjualan.setText("");
        txtIdTabel.setText("");
        txtNominalBayar.setText("");
        getData();
        getProduk();
        
    }//GEN-LAST:event_btnBersihkanActionPerformed

    private void btnTambahkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahkanActionPerformed
        // TODO add your handling code here:
           // Ambil nilai ID produk dari input
        int idProduk = Integer.parseInt(txtIdProduk.getText().trim());

        // Ambil nama produk dari combo box
        String nama_produk = (cbProduk.getSelectedItem() != null) ? cbProduk.getSelectedItem().toString() : "";

        // Ambil jumlah produk dari spinner
        int jumlah_produk = (int) spinJProduk.getValue();
        String StringJumlah = String.valueOf(spinJProduk.getValue());
        // Ambil harga dari input
        double harga = Double.parseDouble(txtHarga.getText().trim());

        // Ambil subtotal dari input
        double subTotal = Double.parseDouble(txtSubHarga.getText().trim());

        // Ambil total harga dari input
        double totalHarga = Double.parseDouble(txtTotalHarga.getText().trim());
        String keterangan = "telah berhasil membeli produk: " + nama_produk + "\njumlah: " + StringJumlah;

        // Cetak hasil input ke konsol (debug/log)
        System.out.println("idProduk     : " + idProduk);
        System.out.println("nama_produk  : " + nama_produk);
        System.out.println("jumlahProduk : " + jumlah_produk);
        System.out.println("harga        : " + harga);
        System.out.println("subTotal     : " + subTotal);
//        System.out.println("keterangan  : " + keterangan);
        // TODO add your handling code here:
        // Masukkan data ke Map
                   
        boolean produkDitemukan = false;
        int stok = Integer.parseInt(txtStok.getText().trim());

        for (Map.Entry<Integer, Map<String, Object>> entry : dataProdukMap.entrySet()) {
            Map<String, Object> existingItem = entry.getValue();
            if (existingItem.get("idProduk").equals(idProduk)) {
                // Jika idProduk ditemukan, update jumlah dan subtotal
                int jumlahLama = (int) existingItem.get("jumlah");
                int jumlahBaru = jumlahLama + jumlah_produk;

                double subTotalLama = (double) existingItem.get("subTotal");
                double subTotalBaru = subTotalLama + subTotal;
                existingItem.put("jumlah", jumlahBaru);
                existingItem.put("subTotal", subTotalBaru);
                
                produkDitemukan = true;
              //  getData();
                break;
            }
        }

        if (!produkDitemukan) {
            Map<String, Object> item = new HashMap<>();
            item.put("idProduk", idProduk);
            item.put("namaProduk", nama_produk);
            item.put("jumlah", jumlah_produk);
            item.put("stok", stok);
            item.put("subTotal", subTotal);

            dataProdukMap.put(indexProduk, item);
            indexProduk++;
        }

        
        getProduk();
        getData();


        
    }//GEN-LAST:event_btnTambahkanActionPerformed

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
        halamanUtama.setVisible(false);
        setVisible(false);
        halamanUtama.setVisible(true);
        halamanUtama.dashboardViews();
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void penjualanTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_penjualanTableMouseClicked
        dataBaru = false;
        System.out.println("DONE BANG");

        try {
            int row = penjualanTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Silakan pilih data dari tabel.");
                return;
            }

            String tableClicked = penjualanTable.getModel().getValueAt(row, 1).toString();
            txtIdTabel.setText(tableClicked);
            System.out.println("Clicked ID: " + tableClicked);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_penjualanTableMouseClicked

    private void txtIdPenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdPenjualanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdPenjualanActionPerformed

    private void btnKonfirmasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKonfirmasiActionPerformed
        // TODO add your handling code here:
        // Misalnya dataProdukMap bertipe Map atau HashMap
        if (dataProdukMap == null || dataProdukMap.isEmpty()) {
            JOptionPane.showMessageDialog(null, "anda belum menambahkan produk yang ingin di beli", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
                // Hapus karakter underscore (_) dari input
            String nominalText = txtNominalBayar.getText().replace("_", "").replace(",", "");
            String subTotalText = txtTotalHargaa.getText().replace("_", "").replace(",", "");

            double nominal = Double.parseDouble(nominalText);
            double subTotal = Double.parseDouble(subTotalText);
            double kembalian = nominal - subTotal;
            String queryPenjualan = "INSERT INTO penjualan (keterangan, totalHarga, created_at) VALUES (?, ?, NOW())";
            
            String queryDetailPenjualan = "INSERT INTO detail_penjualan (idPenjualan, idProduk, jumlah, subtotal) VALUES (?, ?, ?, ?)";
            //cek stok
            String cekStokQuery = "SELECT stok FROM produk WHERE idProduk = ?";
            String queryUpdateStok = "UPDATE produk SET stok = stok - ? WHERE idProduk = ?";
            try (
                Connection conn = Connections.ConnectionDB();
                PreparedStatement pstPenjualan = conn.prepareStatement(queryPenjualan, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement pstUpdateStok = conn.prepareStatement(queryUpdateStok);
                PreparedStatement cekProdukStok = conn.prepareStatement(cekStokQuery);
                PreparedStatement pstDetailPenjualan = conn.prepareStatement(queryDetailPenjualan);  

            ) {
            // Buat penampung nama-nama produk
            List<String> listNamaProduk = new ArrayList<>();
            List<Integer> listStokProduk = new ArrayList<>();
            List<Integer> listJumlahProduk = new ArrayList<>();
            List<String> listTotalHarga = new ArrayList<>();

            for (Map.Entry<Integer, Map<String, Object>> entry : dataProdukMap.entrySet()) {
                Map<String, Object> item = entry.getValue();

                int idProduk = (int) item.get("idProduk");
                String namaProduk = (String) item.get("namaProduk");
                int jumlah = (int) item.get("jumlah");
                int stok = (int) item.get("stok");
                
                double subTotals = (double) item.get("subTotal");

                // Tambahkan ke list nama produk
                listNamaProduk.add(namaProduk);
                listTotalHarga.add("Rp "+formatIDCurrency.currencyFormat(subTotals));
                listStokProduk.add(stok);
                listJumlahProduk.add(jumlah);

                System.out.println("=========================");
                System.out.println("ID Produk    : " + idProduk);
                System.out.println("Nama Produk  : " + namaProduk);
                System.out.println("Jumlah       : " + jumlah);
                System.out.println("Subtotal     : " + subTotals);

                // Kalau kamu masih perlu insert tiap item, bisa lakukan di sini (opsional)
            }

            // Setelah loop selesai, gabungkan nama produk dengan koma
            String semuaNamaProduk = String.join(", ", listNamaProduk);
            String semuaTotalHarga = String.join(", ", listTotalHarga);
            String keterangan = "";
            
            //kembalianMessages
            String kembalianMessages = (nominal == subTotal) ? "Tidak Ada Kembalian" : "Kembalian Anda\n" + formatIDCurrency.currencyFormat(kembalian);
            String kembalianHeadlineMessages = (nominal >= subTotal) ? "Info" : "Error";
            if (nominal >= subTotal ) {
                if (nominal == subTotal)
                {
                    // tidak ada kembalian
                    keterangan = "Telah berhasil menjual\nproduk: " + semuaNamaProduk+
                    "\nSub Total Harga per Produk: "+semuaTotalHarga+"\nTotal Harga Keseluruhan: " + formatIDCurrency.currencyFormat(subTotal)+
                      "\nNominal Bayar: " +formatIDCurrency.currencyFormat(nominal)+
                      "\nKembalian: Tidak Ada Kembalian";
                } else 
                {
                    // ada kembalian
                    keterangan = "Telah berhasil menjual produk: " + semuaNamaProduk+
                        "\nSub Total Harga per Produk: "+semuaTotalHarga+"|\nTotal Harga Keseluruhan: " + formatIDCurrency.currencyFormat(subTotal)+
                          "\nNominal Bayar: Rp " +formatIDCurrency.currencyFormat(nominal)+
                          "\n Kembalian: Rp "+formatIDCurrency.currencyFormat(kembalian)+"|";
                }
                System.out.println("Keterangan: " + keterangan);
                
                // cek status stok
                boolean cekStokStatus = true;

                for (int i = 0; i < listStokProduk.size(); i++) {
                    if (listStokProduk.get(i) <= listJumlahProduk.get(i)) {
                        cekStokStatus = false;
                        break; // langsung keluar dari loop jika ada yang tidak valid
                    }
                }
                
                // jika benar maka akan disimpan datanya
                if (cekStokStatus) {
                    // Hanya satu kali penyimpanan
                    pstPenjualan.setString(1, keterangan);
                    pstPenjualan.setDouble(2, subTotal);
                    pstPenjualan.executeUpdate();
                } else {
                    JOptionPane.showMessageDialog(null, "Jumlah Produk berlebihan, tidak bisa melanjutkan proses..", "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
                }
                    
                    
                try (ResultSet generatedKeys = pstPenjualan.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idPenjualan = generatedKeys.getInt(1);
                        System.out.println("ID Penjualan Baru: " + idPenjualan);
                        // insert ke detail_penjualan atau update stok, dll
                        for (Map.Entry<Integer, Map<String, Object>> entry : dataProdukMap.entrySet()) {
                            Map<String, Object> item = entry.getValue();
                            
                            int idProduk = (int) item.get("idProduk");
                            String namaProduk = (String) item.get("namaProduk");
                            int jumlah = (int) item.get("jumlah");
                            double subTotalProduk = (double) item.get("subTotal");
                            int stok = (int) item.get("stok");

                            System.out.println("=========================");
                            System.out.println("ID Produk    : " + idProduk);
                            System.out.println("Nama Produk  : " + namaProduk);
                            System.out.println("Jumlah       : " + jumlah);
                            System.out.println("Subtotal     : " + subTotalProduk);
                            
                            // Cek stok terlebih dahulu
                            cekProdukStok.setInt(1, idProduk);
                            ResultSet rsProdukStok = cekProdukStok.executeQuery();
                            
                            try{
                                if (rsProdukStok.next()) {
                                        if (stok >= jumlah) {
                                            // perbarui stok
                                            pstUpdateStok.setInt(1, jumlah);
                                            pstUpdateStok.setInt(2, idProduk);
                                            pstUpdateStok.executeUpdate();
                                            
                                            // menambahkan detail pembelian
                                            pstDetailPenjualan.setInt(1, idPenjualan);
                                            pstDetailPenjualan.setInt(2, idProduk);
                                            pstDetailPenjualan.setInt(3, jumlah);
                                            pstDetailPenjualan.setDouble(4, subTotalProduk);
                                            pstDetailPenjualan.executeUpdate();
                                            JOptionPane.showMessageDialog(null, "\nNama Produk: " + namaProduk + 
                                                    "|\nJumlah Produk: " + jumlah + 
   
                                                    "|\nTotal Harga: Rp " + formatIDCurrency.currencyFormat(subTotalProduk) +
                                                    "|\nTelah Berhasil"
                                                    , 
                                                    "Pembelian Produk", JOptionPane.INFORMATION_MESSAGE);

                                        } else {
                                            
                                            System.out.println("Stok tidak mencukupi!");
                                            JOptionPane.showMessageDialog(null, "Jumlah Produk berlebihan, tidak bisa melanjutkan proses..", "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
                                            // Bisa throw exception atau tampilkan pesan ke user
                                        }
                                    }
                            } catch (Exception e)
                            {
                                    e.printStackTrace();
                            }

                            
                            

                            // Kalau kamu masih perlu insert tiap item, bisa lakukan di sini (opsional)
                        }
                            JOptionPane.showMessageDialog(null, kembalianMessages, kembalianHeadlineMessages, JOptionPane.INFORMATION_MESSAGE);
                        dataProdukMap.clear();
                    } else {
                        System.out.println("Gagal mendapatkan ID penjualan.");
                    }
                }

            } else {
                JOptionPane.showMessageDialog(null, "Nominal bayar anda tidak mencukupi.", kembalianHeadlineMessages, JOptionPane.ERROR_MESSAGE);
            }
            // Contoh keterangan



                





            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
            }
            getData();
            
            


        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ada kesalahan pada nominal bayar anda", "Error", JOptionPane.ERROR_MESSAGE);
        }

        txtNominalBayar.setText("");
        halamanUtama.getPemasukan();

    }//GEN-LAST:event_btnKonfirmasiActionPerformed

    private void txtNominalBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNominalBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNominalBayarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnKonfirmasi;
    private javax.swing.JButton btnTambahkan;
    private javax.swing.JComboBox<String> cbProduk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable penjualanTable;
    private javax.swing.JSpinner spinJProduk;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtIdPenjualan;
    private javax.swing.JTextField txtIdProduk;
    private javax.swing.JTextField txtIdTabel;
    private javax.swing.JTextField txtJumlahP;
    private javax.swing.JTextField txtNominalBayar;
    private javax.swing.JTextField txtStok;
    private javax.swing.JTextField txtSubHarga;
    private javax.swing.JTextField txtTotalHarga;
    private javax.swing.JTextField txtTotalHargaa;
    private javax.swing.JLabel txtVisualSHarga;
    private javax.swing.JLabel txtVisualTHarga;
    private javax.swing.JLabel txtVisualTotalHarga;
    // End of variables declaration//GEN-END:variables
}
