/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.db;

/**
 *
 * @author Christian
 */
public class Decimal {
    
    public final static String CURRENCY = "Php";

    private final String s;
    
    public Decimal(int i){
        String is = i+"";
        if(is.equals("0")) s = "0.00";
        else if(is.length() == 1) s = "0.0"+is;
        else if(is.length() == 2) s = "0."+is;
        else s = is.substring(0, is.length()-2) + "." + is.substring(is.length()-2);
    }
    
    public Decimal(String s) {
        if(s.contains(".")) this.s = s;
        else this.s = s+".00";
    }
    
    public int getNumericalValue(){
        String v = s.replace(".", "");
        return Integer.parseInt(v);
    }

    @Override
    public String toString() {
        return s;
    }
    
    public String getCurrency(){
        
        String buffer = s.replace("-", "");
        boolean isNegative = s.contains("-");
        int o_len = s.length();
        if(isNegative) o_len--;
        
        int v_len = o_len - 3;
        int comms = (v_len - 1)/3;
        
        String ret="";
        for(int i = 0; i<buffer.length()-3; i++){
            for(int c = 1; c<=comms; c++){
                int index_n = ((v_len-4)%3) + 3*comms - 2;
                if(i == index_n) ret += ",";
            }
            ret += buffer.charAt(i);
        }
        ret += buffer.substring(buffer.length()-3);
        if(isNegative) ret = "-"+ret;
        
        return (CURRENCY.isEmpty()?"":CURRENCY+" ")+ret;
    }
    
    
}
