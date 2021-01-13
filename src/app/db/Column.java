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
public class Column {
    
    public enum ColumnType{
        INTEGER, DECIMAL, TEXT
    }
    
    public enum Modifier{
        PRIMARY, NOT_NULL
    }
    
    private final String name;
    private final ColumnType type;
    private final Modifier[] mods;
    
    public Column(String name, ColumnType type, Modifier... mods){
        this.name = name;
        this.type = type;
        this.mods = mods;
    }
    
    public String getLine(){
        StringBuilder sb = new StringBuilder();
        sb.append(getName(true)).append(" ");
        sb.append(type.toString());
        
        if(isPrimary()) sb.append(" ").append("PRIMARY KEY");
        if(isNotNull()) sb.append(" ").append("NOT NULL");
        
        return sb.toString();
    }
    
    public boolean isPrimary(){
        for(Modifier mod : mods) if(mod.equals(Modifier.PRIMARY)) return true;
        return false;
    }
    
    public boolean isNotNull(){
        for(Modifier mod : mods) if(mod.equals(Modifier.NOT_NULL)) return true;
        return false;
    }
    
    public String getName(boolean wrapped){
        if(wrapped) return String.format("\'%s\'", name);
        else return name;
    }
    
    public boolean isText(){
        return type.equals(ColumnType.TEXT);
    }
    
    public boolean isNumeric(){
        return !isText();
    }
    
    public boolean isDecimal(){
        return type.equals(ColumnType.DECIMAL);
    }    
    
    public static Column getRowId(){
        Column col = new Column("row_id", Column.ColumnType.INTEGER, Modifier.PRIMARY);
        return col;
    }
}
