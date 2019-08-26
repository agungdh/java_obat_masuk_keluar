/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.test.Helpers;

import static java.lang.System.out;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author user
 */
public class ADHhelper {
    public static void d(String string, boolean pembatas) {
        if (!pembatas) {
            out.println(string);
        } else {
            d(string);
        }
    }
    
    public static Date getTanggalFromDB(String tanggalParam) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(tanggalParam);
    }

    public static String parseTanggal(Date tanggalParam) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(tanggalParam);
    }
    
    public static String tanggalIndo(String tanggalParam) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date tanggal = format.parse(tanggalParam);

        SimpleDateFormat parsedFormat = new SimpleDateFormat("dd-MM-YYYY");
        String parsedtanggal = parsedFormat.format(tanggal);
        
        return parsedtanggal;
    }
    
    public static String rupiah(int uang) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        
        return kursIndonesia.format(uang);
    }
    public static void d(String string) {
        out.println("===========================================");
        out.println(string);
        out.println("===========================================");
    }
    
    public static String bulan(int i) {
        switch(i) {
            case 1:
              return "Januari";
            case 2:
              return "Februari";
            case 3:
              return "Maret";
            case 4:
              return "April";
            case 5:
              return "Mei";
            case 6:
              return "Juni";
            case 7:
              return "Juli";
            case 8:
              return "Agustus";
            case 9:
              return "September";
            case 10:
              return "Oktober";
            case 11:
              return "November";
            case 12:
              return "Desember";
            default:
              return "ERROR !!!";
          }
    }
}
