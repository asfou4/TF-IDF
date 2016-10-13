/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tf.idf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;


import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
/**
 *
 * @author MYFE
 */
public class TfIdf {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("MENU TF-IDF");
        BufferedReader x = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Masukan angka 1 untuk TF dan 2 untuk IDF : ");
        int pilih = Integer.parseInt(x.readLine());
        String website = "";
        switch (pilih){
            case 1 :
                System.out.print("Masukan website : ");
                website = x.readLine();
                tf(website);
                break;
            case 2 :
                System.out.print("Masukan website : ");
                website = x.readLine();
        }
        
    }
    
    private static void tf(String url) throws IOException{
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
            System.out.println(index_kata.get(i) + " = " + hitung_kata.get(i) + "==/===>" + ((double) hitung_kata.get(i) / jumlah_kata));
            tabel_kata.put(index_kata.get(i),hitung_kata.get(i));
        }
        
    }
    
}
