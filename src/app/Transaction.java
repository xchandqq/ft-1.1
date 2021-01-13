/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.db.Decimal;

/**
 *
 * @author Christian
 */
public class Transaction {
    
    public enum TransactionType{
        INITIAL, DEBIT, CREDIT, TRANSFER
    }
    
    private final int id;
    private final int from;
    private final int to;
    private final TransactionType type;
    private final String categ;
    private final Decimal amount;
    private final int date;
    private final String details;
    private final int flags;

    public Transaction(int id, int fromId, int toId, int type, String categ, int amount, int date, String details, int flags) {
        this.id = id;
        this.from = fromId;
        this.to = toId;
        this.type = intToType(type);
        this.categ = categ;
        this.amount = new Decimal(amount);
        this.date = date;
        this.details = details;
        this.flags = flags;
    }

    public Decimal getAmount() {
        return amount;
    }

    public String getCateg() {
        return categ;
    }

    public int getDate() {
        return date;
    }

    public String getDetails() {
        return details;
    }

    public int getFlags() {
        return flags;
    }

    public int getAccountIdFrom() {
        return from;
    }

    public int getId() {
        return id;
    }

    public int getAccountIdTo() {
        return to;
    }

    public TransactionType getType() {
        return type;
    }
    
    public boolean isDebit(){
        return type.equals(TransactionType.DEBIT);
    }
    
    public boolean isCredit(){
        return type.equals(TransactionType.CREDIT);
    }
    
    public boolean isTransfer(){
        return type.equals(TransactionType.TRANSFER);
    }
    
    public boolean isInitial(){
        return type.equals(TransactionType.INITIAL);
    }
    
    public static int getTypeInteger(TransactionType t){
        switch(t){
            case INITIAL:
                return 0;
            case DEBIT:
                return 1;
            case CREDIT:
                return 2;
            case TRANSFER:
                return 3;
        }
        return -1;
    }
    
    private TransactionType intToType(int i){
        switch(i){
            case 0:
                return TransactionType.INITIAL;
            case 1:
                return TransactionType.DEBIT;
            case 2:
                return TransactionType.CREDIT;
            case 3:
                return TransactionType.TRANSFER;
            default:
                return null;
        }
    }
    
    
    
}
