package hu.kek;

import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.webkit.WebView;

import java.util.ArrayList;

import hu.kek.Adatok.AdatCimek;
import hu.kek.Adatok.Adatok;

/**
 * Created by Bence on 2017. 01. 13..
 */

public class Statikus {

    public static String szovegSzin = "";
    public static String mezoHintSzovegSzin = "";
    public static String linkSzin = "";

    public static void beallitasok() {
        setSzovegSzin();
        setMezoHintSzovegSzin();
        setLinkSzin();
    }
    private static void setSzovegSzin() {
        if(BeallitasokActivity.sotetTema) {
            szovegSzin = "#ffffff";
        } else {
            szovegSzin = "#000000";
        }
    }

    private static void setMezoHintSzovegSzin() {
        if(BeallitasokActivity.sotetTema) {
            mezoHintSzovegSzin = "#909090";//909090 a8a8a8
        } else {
            mezoHintSzovegSzin = "#9b9b9b";
        }
    }

    private static void setLinkSzin() {
        if(BeallitasokActivity.sotetTema) {
            linkSzin = "#fde9ac";
        } else {
            linkSzin = "#0000ee";
        }
    }

    public static SpannableString szovegFormaz (String szoveg) {
        SpannableString ss =  new SpannableString(szoveg);
        ss.setSpan(new RelativeSizeSpan(1.5f), 0, ss.length(), 0);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor(szovegSzin.toString())), 0, ss.length(), 0);
        return ss;
    }

    public static void temaBeallit(Activity ablak) {
        if(BeallitasokActivity.sotetTema) {
            ablak.setTheme(R.style.AppThemeDark); //Theme_Material_NoActionBar
        } else {
            ablak.setTheme(R.style.AppTheme);
        }
    }

    public static void webViewSzinBeallit(WebView webView) {
        if(BeallitasokActivity.feketeHatter) {
            webView.setBackgroundColor(Color.BLACK);
        } else {
            webView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public static String cimKeres (String keresendo) {
        return cimKeres(keresendo, false);
    }

    public static String cimKeres (String keresendo, boolean elsoTorol) {
        ArrayList<String> talaltCimek = new ArrayList<>();
        int cimHely = -1;
        boolean torol = false;
        for(int i = 0; i<Adatok.kapcsolo.length; ++i) {
            for (int j = 0; j< Adatok.kapcsolo[i].length; ++j) {
                if(Adatok.kapcsolo[i][j].equals(keresendo)) {
                    cimHely = i;
                    if(j == 0 && elsoTorol) {
                        torol = true;
                    }
                    break;
                }
            }
        }
        int aktualisCimTipus = 10;
        int elozoCimTipus = 10;
        for (int i = cimHely+1; i> 0; --i) {
            if(AdatCimek.getCim(i).substring(2, 3).equals("p")) {
                if(AdatCimek.getCim(i).contains("eloszo")) {
                    aktualisCimTipus = 7;
                } else if(AdatCimek.getCim(i).contains("kiemeltKetto")) {
                    if(AdatCimek.getCim(i).contains("hitvall")) {
                        aktualisCimTipus = 4;
                    } else if(AdatCimek.getCim(i).contains("parancsolat")) {
                        aktualisCimTipus = 3;
                    } else if(AdatCimek.getCim(i).contains("Záró")) {
                        aktualisCimTipus = 7;
                    } else {
                        aktualisCimTipus = 4;
                    }
                } else {
                    //összefoglalas, egyesével kell végig nézni
                    for (int j = 0; j < Adatok.osszefoglaloCimek.length; ++j) {
                        if(Adatok.osszefoglaloCimek[j][0] == i) {
                            aktualisCimTipus = Adatok.osszefoglaloCimek[j][1];
                            break;
                        }
                    }
                }
            } else {
                aktualisCimTipus = Integer.parseInt(AdatCimek.getCim(i).substring(2, 3));
            }
            if(aktualisCimTipus < elozoCimTipus) {
                elozoCimTipus = aktualisCimTipus;
                talaltCimek.add(AdatCimek.getCim(i));
            }
        }
        int ciklusSzam = 0;
        if(torol) {
            for (int j = 0; j < Adatok.sorrend.length; ++j) {
                if(Adatok.sorrend[j].equals(keresendo)) {
                    for (int z = 0; z < 5; ++z) {
                        if(j-z > 0 && Adatok.sorrend[j-z].contains("c")) {
                            ++ciklusSzam;
                        }
                    }
                    break;
                }
            }
        }
        String osszerakott = "";
        for (int i = talaltCimek.size(); i>ciklusSzam; --i) {
            osszerakott += talaltCimek.get(i-1).replaceAll("<a(.*)a>", "").toString();
        }
        osszerakott = "<span class=\"sokcim\">" + osszerakott + "<span>";
        return osszerakott;
    }


}
