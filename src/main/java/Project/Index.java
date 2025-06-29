/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Project;

import Project.Connection.Connections;
import java.sql.Connection;
import Project.Helper.CurrencyFormat;
import Project.DashboardIndex;
import Project.Pages.Produk.*;
import Project.Pages.Agen.AgenMenu;
import Project.Pages.Pembelian.PembelianMenu;
import Project.Pages.Receipt.KeuanganMenu;
import Project.Pages.Penjualan.PenjualanMenu;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author brsap
 */
public class Index extends javax.swing.JFrame {
    public static Index instance; // instance globa
    private String queryTotalPengeluaran = "SELECT SUM(totalHarga) AS totalKeseluruhan FROM pembelian WHERE DATE(pembelian.created_at) = CURDATE()";
    private String queryTotalPemasukan = "SELECT SUM(totalHarga) AS totalKeseluruhan FROM penjualan WHERE DATE(penjualan.created_at) = CURDATE()";

    /**
     * Creates new form Produk
     */
    public Index() {
        initComponents();
        dashboardViews();
        addSubMenuKategori();
        instance = this; // tetapkan saat frame dibuat
        getPengeluaran();
        getPemasukan();
    }
    
    public void setCustomQueryTotalPengeluaran(String query) {
        this.queryTotalPengeluaran = query;
    }

    public void setCustomQueryTotalPemasukan(String query) {
        this.queryTotalPemasukan = query;
    }
    
    public void dashboardViews()
    {
        panelViews.removeAll();
        panelViews.repaint();
        DashboardIndex dashboard = new DashboardIndex(this);
        panelViews.add(dashboard);
        dashboard.show();
    }
    
    public void getPengeluaran() {
        try (Connection conn = (Connection) Connections.ConnectionDB()) {
            CurrencyFormat formatIDCurrency = new CurrencyFormat();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryTotalPengeluaran); // pakai custom query

            if (rs.next()) {
                double total = rs.getDouble("totalKeseluruhan");
                labelPengeluaran.setText((total != 0 ? "Rp " + formatIDCurrency.currencyFormat(total) : "Rp 0.000"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data pengeluaran dari database.");
        }
    }

    public void getPemasukan() {
        try (Connection conn = (Connection) Connections.ConnectionDB()) {
            CurrencyFormat formatIDCurrency = new CurrencyFormat();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryTotalPemasukan); // pakai custom query

            if (rs.next()) {
                double total = rs.getDouble("totalKeseluruhan");
                labelPemasukan.setText((total != 0 ? "Rp " + formatIDCurrency.currencyFormat(total) : "Rp 0.000"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data pemasukan dari database.");
        }
    }

    
    public void addSubMenuKategori() {
        SwingUtilities.invokeLater(() -> {
            try (Connection conn = (Connection) Connections.ConnectionDB()) {
                String sql = "SELECT idKategori, namaKategori FROM kategori";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                menuKategori.removeAll();

                int index = 0;
                while (rs.next()) {
                    int idKategori = rs.getInt("idKategori");
                    String namaKategori = rs.getString("namaKategori");
                    int currentIndex = index; // perlu untuk lambda expression

                    JMenuItem item = new JMenuItem(namaKategori);
                    item.addActionListener(e -> {
                        // Panggil frame dan lempar data
                        panelViews.removeAll();
                        panelViews.repaint();

                        ProdukperKategori menu = new ProdukperKategori(idKategori, currentIndex, namaKategori);

                        panelViews.add(menu);
                        menu.setVisible(true);
                    });

                    menuKategori.add(item);
                    index++;
                }

                menuKategori.revalidate();
                menuKategori.repaint();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memuat kategori dari database.");
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        labelPemasukan = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        labelPengeluaran = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        panelViews = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        daftarAgenView = new javax.swing.JMenuItem();
        daftarPembelianView = new javax.swing.JMenuItem();
        penjualanView = new javax.swing.JMenuItem();
        daftarKeuanganView = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        menuKategori = new javax.swing.JMenu();
        daftarKategoriView = new javax.swing.JMenuItem();
        daftarProdukView = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 204, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(1581, 120));

        jPanel2.setBackground(new java.awt.Color(255, 245, 255));

        jLabel2.setFont(new java.awt.Font("Dubai", 1, 18)); // NOI18N
        jLabel2.setText("Warung Mama Amel");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(16, 16, 16))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel6)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));

        jLabel1.setFont(new java.awt.Font("Dubai", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 102, 0));
        jLabel1.setText("Pemasukan:");

        labelPemasukan.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        labelPemasukan.setForeground(new java.awt.Color(51, 102, 0));
        labelPemasukan.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelPemasukan))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 204, 204));

        jLabel4.setFont(new java.awt.Font("Dubai", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 0, 0));
        jLabel4.setText("Pengeluaran:");

        labelPengeluaran.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        labelPengeluaran.setForeground(new java.awt.Color(102, 0, 0));
        labelPengeluaran.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelPengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(labelPengeluaran))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(175, 175, 175)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(66, 66, 66)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(265, 265, 265))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        panelViews.setBackground(new java.awt.Color(204, 255, 204));

        javax.swing.GroupLayout panelViewsLayout = new javax.swing.GroupLayout(panelViews);
        panelViews.setLayout(panelViewsLayout);
        panelViewsLayout.setHorizontalGroup(
            panelViewsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1589, Short.MAX_VALUE)
        );
        panelViewsLayout.setVerticalGroup(
            panelViewsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelViews)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelViews)
        );

        jMenu2.setText("Menu");

        daftarAgenView.setText("Agen");
        daftarAgenView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                daftarAgenViewActionPerformed(evt);
            }
        });
        jMenu2.add(daftarAgenView);

        daftarPembelianView.setText("Pembelian");
        daftarPembelianView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                daftarPembelianViewActionPerformed(evt);
            }
        });
        jMenu2.add(daftarPembelianView);

        penjualanView.setText("Penjualan");
        penjualanView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                penjualanViewActionPerformed(evt);
            }
        });
        jMenu2.add(penjualanView);

        daftarKeuanganView.setText("Keuangan");
        daftarKeuanganView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                daftarKeuanganViewActionPerformed(evt);
            }
        });
        jMenu2.add(daftarKeuanganView);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Produk");

        menuKategori.setText("Kategori Produk");
        jMenu3.add(menuKategori);

        daftarKategoriView.setText("Daftar Kategori");
        daftarKategoriView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                daftarKategoriViewActionPerformed(evt);
            }
        });
        jMenu3.add(daftarKategoriView);

        daftarProdukView.setText("Daftar Produk");
        daftarProdukView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                daftarProdukViewActionPerformed(evt);
            }
        });
        jMenu3.add(daftarProdukView);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void daftarProdukViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_daftarProdukViewActionPerformed
        // TODO add your handling code here:
        panelViews.removeAll();
        panelViews.repaint();
        ProdukMenu menuProduk = new ProdukMenu();
        panelViews.add(menuProduk);
        menuProduk.show();
    }//GEN-LAST:event_daftarProdukViewActionPerformed

    private void daftarKategoriViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_daftarKategoriViewActionPerformed
        // TODO add your handling code here:
        panelViews.removeAll();
        panelViews.repaint();
        KategoriProdukMenu menuKategori = new KategoriProdukMenu();
        panelViews.add(menuKategori);
        menuKategori.show();

    }//GEN-LAST:event_daftarKategoriViewActionPerformed

    private void daftarAgenViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_daftarAgenViewActionPerformed
        // TODO add your handling code here:
        panelViews.removeAll();
        panelViews.repaint();
        AgenMenu menuAgen = new AgenMenu();
        panelViews.add(menuAgen);
        menuAgen.show();
    }//GEN-LAST:event_daftarAgenViewActionPerformed

    private void daftarKeuanganViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_daftarKeuanganViewActionPerformed
        // TODO add your handling code here:
        panelViews.removeAll();
        panelViews.repaint();
        KeuanganMenu laporanKeuangan = new KeuanganMenu();
        panelViews.add(laporanKeuangan);
        laporanKeuangan.show();
    

    }//GEN-LAST:event_daftarKeuanganViewActionPerformed

    private void daftarPembelianViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_daftarPembelianViewActionPerformed
        // TODO add your handling code here:
        panelViews.removeAll();
        panelViews.repaint();
        PembelianMenu pembelianMenu = new PembelianMenu();
        panelViews.add(pembelianMenu);
        pembelianMenu.show();
    }//GEN-LAST:event_daftarPembelianViewActionPerformed

    private void penjualanViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_penjualanViewActionPerformed
        // TODO add your handling code here:
        panelViews.removeAll();
        panelViews.repaint();
        PenjualanMenu penjualanMenu = new PenjualanMenu();
        panelViews.add(penjualanMenu);
        penjualanMenu.show();
    }//GEN-LAST:event_penjualanViewActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Index.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Index.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Index.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Index.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Index().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem daftarAgenView;
    private javax.swing.JMenuItem daftarKategoriView;
    private javax.swing.JMenuItem daftarKeuanganView;
    private javax.swing.JMenuItem daftarPembelianView;
    private javax.swing.JMenuItem daftarProdukView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel labelPemasukan;
    private javax.swing.JLabel labelPengeluaran;
    private javax.swing.JMenu menuKategori;
    public javax.swing.JDesktopPane panelViews;
    private javax.swing.JMenuItem penjualanView;
    // End of variables declaration//GEN-END:variables
}
