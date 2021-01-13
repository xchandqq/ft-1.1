/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

/**
 *
 * @author Christian
 */
public class Account {
    
    public final static int FLAG_STAPLE = 1;
    public final static int FLAG_CURRENT = 2;    
    
    public enum AccountType{
        ASSET, LIABILITY
    }
    
    private int accoutnId;
    private final String fullname;
    private final String shortName;
    private final AccountType type;
    private int accountFlags = 0;
    private int order = 0;

    public Account(String fullname, String shortName, AccountType type, int flags) {
        this.fullname = fullname;
        this.shortName = shortName;
        this.type = type;
        this.accountFlags = flags;
    }
    
    public int getTypeInteger(){
        return type.equals(AccountType.ASSET)?1:0;
    }
    
    public boolean isAsset(){
        return getTypeInteger()==1;
    }
    
    public boolean isLiability(){
        return getTypeInteger()==0;
    }
    
    public void setOrder(int order){
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
    
    public void incrementOrder(){
        order++;
    }
    
    public void decrementOrder(){
        order--;
    }
    
    public void addFlag(int flag){
        this.accountFlags |= flag;
    }

    public int getAccountFlags() {
        return accountFlags;
    }
    
    public boolean isStaple(){
        return (accountFlags&FLAG_STAPLE)>0;
    }
    
    public boolean isCurrent(){
        return (accountFlags&FLAG_CURRENT)>0;
    }

    public void setAccoutnId(int accoutnId) {
        this.accoutnId = accoutnId;
    }

    public int getAccountId() {
        return accoutnId;
    }

    public String getFullname() {
        return fullname;
    }

    public String getShortName() {
        return shortName;
    }

    public AccountType getType() {
        return type;
    }    
}
