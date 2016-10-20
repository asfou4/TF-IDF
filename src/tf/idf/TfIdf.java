/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.idf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author MYFE
 */
public class TfIdf {

    static Connection kon;
    static Statement st;
    static ResultSet rs;

    public static void main(String[] args) throws IOException, SQLException {
        System.out.println("MENU TF-IDF");
        BufferedReader x = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Masukan angka 1 untuk TF dan 2 untuk IDF : ");
        int pilih = Integer.parseInt(x.readLine());
        String website = "";
//        System.out.println(ambil_dok());
        switch (pilih) {
            case 1:
                System.out.print("Masukan website : ");
                website = x.readLine();
                tf(website);
                break;
            case 2:
                //System.out.print("Masukan website : ");
                //website = x.readLine();
                idf();
                break;
        }

    }

    private static void tf(String url) throws IOException {
        url = "http://" + url;
        Document doc = Jsoup.connect(url).timeout(0).get();
        Elements text = doc.select("body");
        String teks = text.text();
        teks = teks.replaceAll("[(-+.^:,'|&?!)]", "").replaceAll("yang", "").replaceAll("dengan", "").replaceAll("dan", "").replaceAll("dari", "");
        String[] kata = teks.split(" ");
        int jumlah_kata = kata.length;

        //-----------------------------------
        String[] kata1 = new String[kata.length];
        for (int i = 0; i < kata.length; i++) {
            kata1[i] = kata[i];
        }

        Vector<String> index_kata = new <String>Vector();
        Vector<Integer> hitung_kata = new <Integer>Vector();
        for (int i = 0; i < kata1.length; i++) {
            int hitung = 0;
            for (int j = 1; j < kata.length; j++) {
                if (kata1[i].equals(kata[j])) {
                    hitung++;
                }
            }
            if (index_kata.size() == 0) {
                index_kata.add(kata1[i]);
                hitung_kata.add(hitung);
            } else {
                int ada = 0;
                for (int j = 0; j < index_kata.size(); j++) {
                    if (kata1[i].equals(index_kata.get(j))) {
                        ada++;
                    }
                }

                if (ada == 0) {
                    index_kata.add(kata1[i]);
                    hitung_kata.add(hitung);
                }
            }
        }

        //System.out.println(kata.length + "<>" + index_kata.size());
        System.out.println("Jumlah kata = " + jumlah_kata);
        Hashtable tabel_kata = new Hashtable();
        for (int i = 0; i < index_kata.size(); i++) {
            System.out.println(index_kata.get(i) + " = " + hitung_kata.get(i) + ">tf>" + ((double) hitung_kata.get(i) / jumlah_kata));
            tabel_kata.put(index_kata.get(i), hitung_kata.get(i));
        }

    }

    //======================================================================================
    public static void idf() throws SQLException {
        String doks = ambil_dok();
        String[] dok = doks.split("ai.ia");
        Vector<String> dokumen = new <String>Vector();
        for (int i = 0; i < dok.length; i++) {
            dokumen.add(dok[i]);
        }
        Vector<String> index = new <String>Vector();
        for (int i = 0; i < dokumen.size(); i++) {
            String[] kata = dokumen.get(i).split(" ");
            for (int j = 0; j < kata.length; j++) {
                if (kata[j].isEmpty()||kata[j].contains(" ")||kata[j].matches("\\S")) {
                }else{
                   //proses add index
                    int hitung = 0;
                    for (int k = 0; k < index.size(); k++) {
                        if (index.size() == 0) {
                            index.add(kata[j]);
                        }else{
                            if(!kata[j].equalsIgnoreCase(index.get(k))){
                                hitung++;
                            }
                        }
                    }
                    if (hitung == index.size()) {
                        index.add(kata[j]);
                    }
                    
                }
                
            }
        }
        int[][] hitung_kata = new int[index.size()][dokumen.size()];
        
        for (int i = 0; i < dokumen.size(); i++) {
            String[] kata = dokumen.get(i).split(" ");
            for (int j = 0; j < kata.length; j++) {
                for (int k = 0; k < index.size(); k++) {
                    if (kata[j].equalsIgnoreCase(index.get(k))) {
                        hitung_kata[k][i] = 1;
                    }
                }
            }
        }
        
        int[] total = new int[index.size()];
        for (int i = 0; i < hitung_kata.length; i++) {
            int hitung = 0;
            for (int j = 0; j < hitung_kata[i].length; j++) {
                hitung = hitung + hitung_kata[i][j];
                
            }
            total[i] = hitung;
        }
        
        for (int i = 0; i < index.size(); i++) {
            System.out.println(index.get(i));
        }
        
        for (int i = 0; i < hitung_kata.length; i++) {
            
            for (int j = 0; j < hitung_kata[i].length; j++) {
                
                System.out.print(hitung_kata[i][j]);
            }
            System.out.println("");
           
        }
        
        for (int i = 0; i < total.length; i++) {
            System.out.println((double)1/total[i]);
        }
        
        
        
        
    }

    private static void konek() throws SQLException {
        String dbHost = "jdbc:mysql://localhost:3306/uninform";
        String dbUser = "root";
        String dbPass = "";

        try {
            kon = (Connection) DriverManager.getConnection(dbHost, dbUser, dbPass);
            //System.out.println("SUKSES !!!");

        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    private static String ambil_dok() throws SQLException {
        int count = 0;
        String sql = "";
        String teks = "";
        String kalimat = "";
        try {
            konek();
            sql = "select konten from bfs group by konten desc limit 3;";
            st = kon.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                teks = rs.getString("konten");//cek apakah ada link serupa
                kalimat = kalimat + teks + "ai.ia";
            }
            
        } catch (SQLException e) {
            System.err.println(e);

        }
        
        return kalimat;
    }

}
