/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project.Helper;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author brsap
 */
public class CurrencyFormat {

    // Fungsi untuk mendapatkan format currency Indonesia
    public NumberFormat currencyFormat() {
        // Format angka lokal Indonesia (1.000,00)
        NumberFormat format = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        return format;  // Mengembalikan format
    }

    // Fungsi untuk memformat angka dengan format currency
    public String formatCurrency(double nominalAngka) {
        // Memformat angka menjadi format currency
        NumberFormat format = currencyFormat();
        return format.format(nominalAngka);  // Mengembalikan angka yang diformat
    }

    public static void main(String[] args) {
        //cara penggunaan
        CurrencyFormat cf = new CurrencyFormat();
        double angka = 1000000.50;  // Angka yang ingin diformat
        System.out.println("Formatted Currency: " + cf.formatCurrency(angka));  // Output: 1.000.000,50
    }
}
