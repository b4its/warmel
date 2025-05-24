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
                model.addColumn("No");
                model.addColumn("ID Produk");
                model.addColumn("Nama Produk");
                model.addColumn("Jumlah");
                model.addColumn("Sub Total");
                
                model.setRowCount(0); // Kosongkan isi tabel terlebih dahulu
                double totalSubTotal = 0.00;
                for (Map.Entry<Integer, Map<String, Object>> entry : dataProdukMap.entrySet()) {
                    int no = entry.getKey() + 1;
                    
                    Map<String, Object> item = entry.getValue();
                    
                    int idProduk = (int) item.get("idProduk");
                    String namaProduk = (String) item.get("namaProduk");
                    int jumlah = (int) item.get("jumlah");
                    double subTotal = (double) item.get("subTotal");
                    totalSubTotal += subTotal;
                    model.addRow(new Object[]{no, idProduk, namaProduk, jumlah, formatIDCurrency.currencyFormat(subTotal)});
                }
                txtVisualTotalHarga.setText(formatIDCurrency.currencyFormat(totalSubTotal));
                // Menampilkan model ke dalam tabel
                penjualanTable.setModel(model);

                //sembunyikan idPenjualan
                penjualanTable.getColumnModel().getColumn(1).setMinWidth(0);
                penjualanTable.getColumnModel().getColumn(1).setMaxWidth(0);
                penjualanTable.getColumnModel().getColumn(1).setWidth(0);
          
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
        cbProduk = new javax.swing.JComboBox<>();
        spinJProduk = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        btnHapus = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();
        btnTambahkan = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        penjualanTable = new javax.swing.JTable();
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
        txtJumlahP = new javax.swing.JTextField();
        txtIdPenjualan = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        txtVisualTotalHarga = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel1.setText("Form Penjualan");

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

        btnTambahkan.setText("Tambahkan");
        btnTambahkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahkanActionPerformed(evt);
            }
        });

        btnBersihkan.setText("Bersihkan");
        btnBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBersihkanActionPerformed(evt);
            }
        });

        penjualanTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Produk", "Jumlah", "Harga"
            }
        ));
        penjualanTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                penjualanTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(penjualanTable);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel7.setText("Sub Total");

        txtIdAgen.setVisible(false);

        txtIdProduk.setVisible(false);

        txtHarga.setVisible(false);

        txtTotalHarga.setVisible(false);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel8.setText("Harga");

        txtSubHarga.setVisible(false);

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

        txtStok.setVisible(false);

        txtJumlahP.setVisible(false);

        txtIdPenjualan.setVisible(false);
        txtIdPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdPenjualanActionPerformed(evt);
            }
        });

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
        jLabel9.setText("Total Harga");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(txtIdAgen, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtIdProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(21, 21, 21)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel8)
                                            .addGap(18, 18, 18)
                                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(btnBersihkan, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(btnTambahkan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(100, 100, 100)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txtSubHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtIdPenjualan, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(188, 188, 188)
                                                .addComponent(txtJumlahP, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addGap(40, 40, 40)
                                                .addComponent(cbProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(spinJProduk, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(36, 36, 36)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(cbProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinJProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtJumlahP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdPenjualan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnBersihkan)
                            .addComponent(btnTambahkan))
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
                        .addGap(18, 18, 18)
                        .addComponent(txtSubHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(54, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
                try { // hapus data
                    String queryDeleteDetailPenjualan ="delete from detail_penjualan where idPenjualan='"+txtIdPenjualan.getText()+"'";
                    String queryDeletePenjualan ="delete from penjualan where idPenjualan='"+txtIdPenjualan.getText()+"'";
                    Connection conn = (Connection) Connections.ConnectionDB();
                    
                    int idProduk = Integer.parseInt(txtIdProduk.getText());
                    int stok = Integer.parseInt(txtStok.getText());
                    int jumlahProduk = Integer.parseInt(txtJumlahP.getText());
                    int totalStok = stok - jumlahProduk;

                    // proses hapus detail penjualan
                    java.sql.PreparedStatement pstDeleteDetailPenjualan = conn.prepareStatement(queryDeleteDetailPenjualan);
                    pstDeleteDetailPenjualan.execute();
                    
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
                    
                    // proses hapus penjualan
                    java.sql.PreparedStatement pstDeletePenjualan = conn.prepareStatement(queryDeletePenjualan);
                    pstDeletePenjualan.execute();
                    JOptionPane.showMessageDialog(null, "Data berhasil dihapus..");

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
        txtIdPenjualan.setText("");
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
        String keterangan = "telah berhasil membeli produk: " + nama_produk + "| jumlah: " + StringJumlah;

        // Cetak hasil input ke konsol (debug/log)
        System.out.println("idProduk     : " + idProduk);
        System.out.println("nama_produk  : " + nama_produk);
        System.out.println("jumlahProduk : " + jumlah_produk);
        System.out.println("harga        : " + harga);
        System.out.println("subTotal     : " + subTotal);
//        System.out.println("keterangan  : " + keterangan);
        // TODO add your handling code here:
                    // Masukkan ke Map
                    
            Map<String, Object> item = new HashMap<>();
            item.put("idProduk", idProduk);
            item.put("namaProduk", nama_produk);
            item.put("jumlah", jumlah_produk);
            item.put("subTotal", subTotal);

            dataProdukMap.put(indexProduk, item);
            indexProduk++;

            // Tampilkan ke tabel
//            DefaultTableModel model = (DefaultTableModel) penjualanTable.getModel();
//            int no = 1;
//            
//            model.addRow(new Object[] {
//                   no++,
//                    idProduk,
//                    nama_produk,
//                    jumlah_produk,
//                    formatIDCurrency.currencyFormat(subTotal)
//                });
//            model.addRow(new Object[]{idProduk, nama_produk, jumlah_produk, subTotal});

            
            
//        if (dataBaru == true) {  // prosess simpan atau edit
//            String queryPenjualan = "INSERT INTO penjualan (keterangan, totalHarga, created_at) VALUES (?, ?, NOW())";
//
//            try (
//                 Connection conn = Connections.ConnectionDB();
//                 PreparedStatement pstPenjualan = conn.prepareStatement(queryPenjualan, Statement.RETURN_GENERATED_KEYS);
//           
//                    ) {
//                int stokAwal = Integer.parseInt(txtStok.getText());
//                int totalStok = stokAwal - jumlah_produk;
//                System.out.println("Total Stok: "+totalStok);
//                if (totalStok > 0)
//                {
//                    pstPenjualan.setString(1, keterangan);
//                    pstPenjualan.setDouble(2, totalHarga);
//
//                    pstPenjualan.executeUpdate();
//
//                    try (ResultSet generatedKeys = pstPenjualan.getGeneratedKeys()) {
//                        if (generatedKeys.next()) {
//                            int idPenjualan = generatedKeys.getInt(1);
//                            String queryUpdateStok = "update produk set stok = ? where idProduk = ?";
//                                // update stok produk
//                                PreparedStatement pstUpdateStok = conn.prepareStatement(queryUpdateStok);
//                                pstUpdateStok.setInt(1, totalStok);
//                                pstUpdateStok.setInt(2, idProduk);
//                                pstUpdateStok.executeUpdate();
//                                
//                                // penambahan detail penjualan
//                            String queryDetailPenjualan = "INSERT INTO detail_penjualan (idPenjualan, idProduk, jumlah, subtotal) VALUES (?, ?, ?, ?)";
//                                PreparedStatement pstDetail_Penjualan = conn.prepareStatement(queryDetailPenjualan);
//                                pstDetail_Penjualan.setInt(1, idPenjualan);
//                                pstDetail_Penjualan.setInt(2, idProduk);
//                                pstDetail_Penjualan.setInt(3, jumlah_produk);
//                                pstDetail_Penjualan.setDouble(4, subTotal);
//                                pstDetail_Penjualan.executeUpdate();
//                            JOptionPane.showMessageDialog(null, "Pembelian anda telah berhasil");
//
//
//                        } else {
//                            JOptionPane.showMessageDialog(null, "Penjualan anda gagal");
//
//                            System.out.println("ID penjualan tidak tersedia.");
//                        }
//                    }
//            
//                }
//                
//            } catch (SQLException | NumberFormatException e) {
//                e.printStackTrace();
//            }
//
//        } 
//        
//        else {
//            
//            txtIdPenjualan.setText("");
//            JOptionPane.showMessageDialog(null, "Penjualan anda gagal, silahkan untuk bersihkan terlebih dahulu");
//        }
        
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
        dispose();
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
            System.out.println("Clicked ID: " + tableClicked);

            Connection conn = Connections.ConnectionDB();
            String query = "select p.idPenjualan, p.idAgen, dp.idProduk, dp.jumlah from penjualan p join detail_penjualan dp on p.idPenjualan = dp.idPenjualan where dp.idPenjualan = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, tableClicked);
            ResultSet sql = ps.executeQuery();

            if (sql.next()) {
                String idPenjualan = sql.getString("idPenjualan");
                int idAgen = sql.getInt("idAgen") -1;
                int idProduk = sql.getInt("idProduk") -1;
                int jumlah = sql.getInt("jumlah");
                txtIdPenjualan.setText(idPenjualan);

                // Validasi index
                if (idProduk < cbProduk.getItemCount()) {
                    cbProduk.setSelectedIndex(idProduk);
                }
                
//                if (idAgen < cbAgen.getItemCount()) {
//                    cbAgen.setSelectedIndex(idAgen);
//                }

                if (jumlah >= 1) {
                    txtJumlahP.setText(String.valueOf(jumlah));
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

    }//GEN-LAST:event_penjualanTableMouseClicked

    private void txtIdPenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdPenjualanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdPenjualanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnTambahkan;
    private javax.swing.JComboBox<String> cbProduk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable penjualanTable;
    private javax.swing.JSpinner spinJProduk;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtIdAgen;
    private javax.swing.JTextField txtIdPenjualan;
    private javax.swing.JTextField txtIdProduk;
    private javax.swing.JTextField txtJumlahP;
    private javax.swing.JTextField txtStok;
    private javax.swing.JTextField txtSubHarga;
    private javax.swing.JTextField txtTotalHarga;
    private javax.swing.JLabel txtVisualSHarga;
    private javax.swing.JLabel txtVisualTHarga;
    private javax.swing.JLabel txtVisualTotalHarga;
    // End of variables declaration//GEN-END:variables
}
