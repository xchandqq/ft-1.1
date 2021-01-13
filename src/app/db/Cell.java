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
public class Cell {
    private final Column col;
    private final String val;

    public Cell(Column col, String val) {
        this.col = col;
        this.val = val;
    }
    
    public int getIntegerValue(){
        return Integer.parseInt(val);
    }

    public Column getColumn() {
        return col;
    }

    public String getValue() {
        return val;
    }
    
    
}
