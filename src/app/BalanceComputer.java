/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.db.Decimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Christian
 */
public class BalanceComputer {
    
    public static Transaction[] sortTransactions(Transaction... transactions){
        List<Transaction> transactionList = Arrays.asList(transactions);
        transactionList.sort(new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                int date1 = o1.getDate();
                int date2 = o2.getDate();
                if(date1 >= date2) return 1;
                else return -1;
            }
        });
        return transactionList.toArray(new Transaction[transactionList.size()]);
    }
    
    public static Balance getCurrentBalance(int accId, Transaction... sortedTransactions){
        int balance = 0;
        int getLastDate = 0;
        boolean startBalance = false;
        for(Transaction transaction : sortedTransactions){            
            boolean transferToThis = transaction.getAccountIdTo() == accId;            
            if(transaction.isInitial()) startBalance = true;

            if(!startBalance) continue;
            if(transaction.isDebit() || (transaction.isTransfer()&&!transferToThis)) balance-=transaction.getAmount().getNumericalValue();
            else balance+=transaction.getAmount().getNumericalValue();
            getLastDate = transaction.getDate();
        }
        return new Balance(getLastDate, new Decimal(balance));
    }
    
    public static Balance getMonthChange(int accId, Transaction... sortedTransactions){
        int balance = 0;
        int getLastDate = 0;
        Calendar c = Calendar.getInstance();
        String dateFilter = String.format("%04d%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH));
        for(Transaction transaction : sortedTransactions){   
            String tDate = transaction.getDate()+"";
            if(!tDate.startsWith(dateFilter)) continue;
            
            boolean transferToThis = transaction.getAccountIdTo() == accId;   
            
            if(transaction.isDebit() || (transaction.isTransfer()&&!transferToThis)) balance-=transaction.getAmount().getNumericalValue();
            else balance+=transaction.getAmount().getNumericalValue();
            getLastDate = transaction.getDate();
        }
        return new Balance(getLastDate, new Decimal(balance));
    }
    
    public static Balance getYearChange(int accId, Transaction... sortedTransactions){
        int balance = 0;
        int getLastDate = 0;
        Calendar c = Calendar.getInstance();
        String dateFilter = String.format("%04d", c.get(Calendar.YEAR));
        for(Transaction transaction : sortedTransactions){   
            String tDate = transaction.getDate()+"";
            if(!tDate.startsWith(dateFilter)) continue;
            
            boolean transferToThis = transaction.getAccountIdTo() == accId;   
            
            if(transaction.isDebit() || (transaction.isTransfer()&&!transferToThis)) balance-=transaction.getAmount().getNumericalValue();
            else balance+=transaction.getAmount().getNumericalValue();
            getLastDate = transaction.getDate();
        }
        return new Balance(getLastDate, new Decimal(balance));
    }
    
    public static Balance getStartOfYearBalance(Transaction... sortedTransactions){
        int balance = 0;
        int getLastDate = 0;
        for(Transaction transaction : sortedTransactions){
            if(transaction.isCredit()) balance-=transaction.getAmount().getNumericalValue();
            else balance+=transaction.getAmount().getNumericalValue();
            getLastDate = transaction.getDate();
            
            if(transaction.isInitial()) break;
        }
        return new Balance(getLastDate, new Decimal(balance));
    }
    
    public static Balance[] getBalanceArray(int accountId, Transaction... sortedTransactions){
        Balance[] bs = new Balance[sortedTransactions.length];
        int initialTransactionIndex = 0;
        int amount = 0;
        
        //find initial
        for(int i = 0; i<sortedTransactions.length; i++){
            Transaction t = sortedTransactions[i];
            if(t.isInitial()){
                amount = t.getAmount().getNumericalValue();
                bs[i] = new Balance(t.getDate(), new Decimal(amount));
                initialTransactionIndex = i;
                break;
            }
        }
        
        //apply before inital balances
        for(int i = initialTransactionIndex-1; i>=0; i--){
            Transaction t = sortedTransactions[i];
            boolean transferToThis = (t.getAccountIdTo() == accountId);
            if(t.isCredit()  || transferToThis) amount -= t.getAmount().getNumericalValue();
            else amount += t.getAmount().getNumericalValue();
            bs[i] = new Balance(t.getDate(), new Decimal(amount));
        }
        
        amount = bs[initialTransactionIndex].amount.getNumericalValue();
        
        //apply after initial balances
        for(int i = initialTransactionIndex + 1; i<sortedTransactions.length; i++){
            Transaction t = sortedTransactions[i];
            boolean transferToThis = (t.getAccountIdTo() == accountId);
            if(t.isCredit() || transferToThis) amount += t.getAmount().getNumericalValue();
            else amount -= t.getAmount().getNumericalValue();
            bs[i] = new Balance(t.getDate(), new Decimal(amount));
        }
        
//        boolean beforeInitial = true;
//        
//        for(int i = 0; i<sortedTransactions.length; i++){
//            boolean deduct;
//            
//            if(sortedTransactions[i].isDebit() || sortedTransactions[i].isTransfer()) deduct = true;
//            else deduct = false;
//            
//            if(beforeInitial){
//                if(sortedTransactions[i].isCredit()) deduct = true;
//                else deduct = false;
//            }
//            
//            if(deduct) amount -= sortedTransactions[i].getAmount().getNumericalValue();
//            else amount += sortedTransactions[i].getAmount().getNumericalValue();
//            
//            bs[i] = new Balance(sortedTransactions[i].getDate(), new Decimal(amount));
//            
//            if(sortedTransactions[i].isInitial()) beforeInitial = false;
//            
//            if(sortedTransactions[i].isInitial() && !beforeInitial){
//                for(int x = 0; x<indicesBeforeInitial; x++){
//                    int pa = bs[x].amount.getNumericalValue();
//                    bs[x] = new Balance(sortedTransactions[i].getDate(), new Decimal(amount - pa));
//                }
//                amount = sortedTransactions[i].getAmount().getNumericalValue();
//                //bs[i] = new Balance(sortedTransactions[i].getDate(), new Decimal(amount));
//            }
//            
//            indicesBeforeInitial++;
////            if(sorTransactions[i].isInitial()){
////                for(int x = 0; x<indicesBeforeInitial; x++){
////                    bs[i].amount = new Decimal(bs[i].amount.getNumericalValue() + sorTransactions[i].getAmount().getNumericalValue());
////                }
////            }
////            else{
////                
////            }
////            indicesBeforeInitial++;
//        }
        
        return bs;
    }
}
