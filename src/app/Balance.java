/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.db.Decimal;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author Christian
 */
public class Balance {
    
    public int date;
    public Decimal amount;

    public Balance(int date, Decimal amount) {
        this.date = date;
        this.amount = amount;
    }
    
    public String getStringDate(){
        return getStringDate(date);
    }
    
    public static String getStringDate(int date){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, date/10000);
        c.set(Calendar.MONTH, (date%10000)/100);
        c.set(Calendar.DAY_OF_MONTH, date%100);
        return String.format("%02d %s %04d", date%100, c.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, Locale.ENGLISH), date/10000);
    }
    
}
