/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.db;

import app.Account;
import app.Balance;
import app.Transaction;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Christian
 */
public class DatabaseInterface {
    
    private final static String NAME_USER_TABLE = "users";
    
    private final static Column USER_FIRST_NAME = new Column("f_name", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column USER_MIDDLE_NAME = new Column("m_name", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column USER_LAST_NAME = new Column("l_name", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column USER_USERNAME = new Column("u_name", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column USER_PASSWORD = new Column("pw", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    
    private final static Column ACC_FULLNAME = new Column("acc_full", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column ACC_SHORTNAME = new Column("acc_short", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column ACC_TYPE = new Column("acc_type", Column.ColumnType.INTEGER, Column.Modifier.NOT_NULL);
    private final static Column ACC_FLAG = new Column("acc_flag", Column.ColumnType.INTEGER, Column.Modifier.NOT_NULL);
    private final static Column ACC_ORDER = new Column("acc_order", Column.ColumnType.INTEGER, Column.Modifier.NOT_NULL);
    
    private final static Column TRN_FROM_ID = new Column("trn_from", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column TRN_TO_ID = new Column("trn_to", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column TRN_TYPE = new Column("trn_type", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column TRN_CATEG = new Column("trn_categ", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column TRN_AMOUNT = new Column("trn_amount", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column TRN_DATE = new Column("trn_date", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column TRN_DETAILS = new Column("trn_details", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    private final static Column TRN_FLAG = new Column("trn_flag", Column.ColumnType.INTEGER, Column.Modifier.NOT_NULL);

    //private final static Column TRN_ = new Column("acc_full", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
    
    private final Connection connection;
    
    private DatabaseInterface(File dataFile) throws SQLException{
        connection = DriverManager.getConnection("jdbc:sqlite:"+dataFile.getPath());
    }
    
    public boolean hasUserTable() throws SQLException{
        return hasTable(NAME_USER_TABLE);
    }
    
    public void createUserTable() throws SQLException{
        createTable(NAME_USER_TABLE,
                Column.getRowId(), USER_FIRST_NAME,
                USER_MIDDLE_NAME, USER_LAST_NAME,
                USER_USERNAME, USER_PASSWORD);
    }
    
    public boolean hasUserCredentials(String username, String password) throws SQLException{
        return hasRow(NAME_USER_TABLE, true,
                new Cell(USER_USERNAME, username), new Cell(USER_PASSWORD, password));
    }
    
    public String getUserId(String username, String password) throws SQLException{
        return getRowValues(NAME_USER_TABLE, new Column[]{Column.getRowId()}, true, new Cell(USER_USERNAME, username), new Cell(USER_PASSWORD, password))[0][0].getValue();
    }
    
    public boolean usernameExists(String username) throws SQLException{
        return hasRow(NAME_USER_TABLE, true, 
                new Cell(USER_USERNAME, username));
    }
    
    public void addUserCredentials(String[] s) throws SQLException{
        insertRow(NAME_USER_TABLE,
                new Cell(Column.getRowId(), s[0]),
                new Cell(USER_LAST_NAME, s[1]),
                new Cell(USER_MIDDLE_NAME, s[2]),
                new Cell(USER_FIRST_NAME, s[3]),
                new Cell(USER_USERNAME, s[4]),
                new Cell(USER_PASSWORD, s[5]));
        
        createTable(getAccountsTablename(s[0]), 
                Column.getRowId(), ACC_FULLNAME, ACC_SHORTNAME,
                ACC_TYPE, ACC_FLAG, ACC_ORDER);
        
        createTable(getTransactionsTablename(s[0]), 
                Column.getRowId(), 
                TRN_FROM_ID, TRN_TO_ID,
                TRN_TYPE, TRN_CATEG, TRN_AMOUNT,
                TRN_DATE, TRN_DETAILS, TRN_FLAG);
    }
    
    public String getUserFirstname(String userId) throws SQLException{
        return getRowValues(NAME_USER_TABLE, new Column[]{USER_FIRST_NAME}, true, new Cell(Column.getRowId(), userId))[0][0].getValue();
    }
    
    public String getFullname(String userId) throws SQLException{
        Cell[][] cells = getRowValues(NAME_USER_TABLE, new Column[]{USER_FIRST_NAME, USER_MIDDLE_NAME, USER_LAST_NAME}, true, new Cell(Column.getRowId(), userId));
        
        String fname = cells[0][0].getValue();
        String mname = cells[0][1].getValue();
        String lname = cells[0][2].getValue();
        
        return String.format("%s%s%s", fname, mname.isEmpty()?" ":(" "+mname+" "), lname);
    }
    
    public void addAccount(String userId, Account account) throws SQLException{
        insertRow(getAccountsTablename(userId), 
                new Cell(ACC_FULLNAME, account.getFullname()),
                new Cell(ACC_SHORTNAME, account.getShortName()),
                new Cell(ACC_TYPE, account.getTypeInteger()+""),
                new Cell(ACC_ORDER, account.getOrder()+""),
                new Cell(ACC_FLAG, account.getAccountFlags()+""));
    }
    
    public void addAccountWithBalance(String userId, Account account, Balance balance) throws SQLException{
        addAccount(userId, account);
        int id = getRowValues(
                getAccountsTablename(userId),
                new Column[]{Column.getRowId()},
                true, new Cell(ACC_FULLNAME, account.getFullname()),
                new Cell(ACC_SHORTNAME, account.getShortName()))[0][0].getIntegerValue();
        addTransaction(userId, 
                new Transaction(-1, id, 0, 
                        Transaction.getTypeInteger(Transaction.TransactionType.INITIAL), 
                        "Balance", balance.amount.getNumericalValue(),
                        balance.date, "Starting Balance", 0)
                );
    }
    
    public Account[] getAccounts(String userId) throws SQLException{
        int count = getRowCount(getAccountsTablename(userId), true);
        Account[] accs = new Account[count];
        
        Cell[][] cellMap = getRowValues(getAccountsTablename(userId), new Column[]{
                        Column.getRowId(), ACC_FULLNAME,
                        ACC_SHORTNAME, ACC_TYPE,
                        ACC_ORDER, ACC_FLAG}, true);
        
        for(int i = 0; i<cellMap.length; i++){
            Cell[] cells = cellMap[i];
            
            accs[i] = new Account(
                    cells[1].getValue(), cells[2].getValue(), 
                    cells[3].getValue().equals("0")?Account.AccountType.LIABILITY:Account.AccountType.ASSET,
                    Integer.parseInt(cells[5].getValue()));
            accs[i].setOrder(Integer.parseInt(cells[4].getValue()));
            accs[i].setAccoutnId(Integer.parseInt(cells[0].getValue()));
        }
        
        return accs;
    }
    
    public Account getAccount(String userId, int accountId) throws SQLException{
        
        Cell[] cells = getRowValues(getAccountsTablename(userId), new Column[]{
                        Column.getRowId(), ACC_FULLNAME,
                        ACC_SHORTNAME, ACC_TYPE,
                        ACC_ORDER, ACC_FLAG}, true, new Cell(Column.getRowId(), accountId+""))[0];
        Account account = new Account(
                    cells[1].getValue(), cells[2].getValue(), 
                    cells[3].getValue().equals("0")?Account.AccountType.LIABILITY:Account.AccountType.ASSET,
                    Integer.parseInt(cells[5].getValue()));
        account.setOrder(Integer.parseInt(cells[4].getValue()));
        account.setAccoutnId(Integer.parseInt(cells[0].getValue()));
        
        return account;
    }
    
    public Transaction[] getAccountTransactions(String userId, int accountId) throws SQLException{
        Cell[][] cells = getRowValues(getTransactionsTablename(userId), new Column[]{
                        Column.getRowId(), TRN_FROM_ID,
                        TRN_TO_ID, TRN_TYPE, TRN_CATEG,
                        TRN_AMOUNT, TRN_DATE, TRN_DETAILS, TRN_FLAG
        }, false, new Cell(TRN_FROM_ID, accountId+""), new Cell(TRN_TO_ID, accountId+""));
        
        List<Transaction> transactions = new ArrayList();
        for(Cell[] trans : cells){
            int toId;
            if(trans[2].getValue().isEmpty()) toId = 0;
            else toId = trans[2].getIntegerValue();
            Transaction t = new Transaction(
                    trans[0].getIntegerValue(), trans[1].getIntegerValue(), toId, 
                    trans[3].getIntegerValue(), trans[4].getValue(), 
                    trans[5].getIntegerValue(), 
                    trans[6].getIntegerValue(), 
                    trans[7].getValue(), 
                    trans[8].getIntegerValue());
            transactions.add(t);
        }
        
        return transactions.toArray(new Transaction[transactions.size()]);
    }
    
    public void addTransaction(String userId, Transaction t) throws SQLException{
        insertRow(getTransactionsTablename(userId), 
                new Cell(TRN_FROM_ID, t.getAccountIdFrom()+""),
                new Cell(TRN_TO_ID, t.getAccountIdTo()==0?"":t.getAccountIdTo()+""),
                new Cell(TRN_TYPE, Transaction.getTypeInteger(t.getType())+""),
                new Cell(TRN_CATEG, t.getCateg()),
                new Cell(TRN_AMOUNT, t.getAmount().getNumericalValue()+""),
                new Cell(TRN_DATE, t.getDate()+""),
                new Cell(TRN_DETAILS, t.getDetails()),
                new Cell(TRN_FLAG, t.getFlags()+"")
                );
    }
    
    public void removeTransaction(String userId, Transaction t) throws SQLException{
        removeRows(getTransactionsTablename(userId), true, new Cell(Column.getRowId(), t.getId()+""));
    }
    
    public boolean accountExists(String userId, Account account) throws SQLException{
        int c = getRowCount(getAccountsTablename(userId), true, 
                new Cell(ACC_FULLNAME, account.getFullname()),
                new Cell(ACC_SHORTNAME, account.getShortName()));
        return c>0;
    }
    
//    private final static Column TRN_FROM_ID = new Column("trn_from", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
//    private final static Column TRN_TO_ID = new Column("trn_to", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
//    private final static Column TRN_TYPE = new Column("trn_type", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
//    private final static Column TRN_CATEG = new Column("trn_categ", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
//    private final static Column TRN_AMOUNT = new Column("trn_amount", Column.ColumnType.DECIMAL, Column.Modifier.NOT_NULL);
//    private final static Column TRN_DATE = new Column("trn_date", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
//    private final static Column TRN_DETAILS = new Column("trn_details", Column.ColumnType.TEXT, Column.Modifier.NOT_NULL);
//    private final static Column TRN_FLAG = new Column("trn_flag", Column.ColumnType.INTEGER, Column.Modifier.NOT_NULL);
    
    /*
    
    general functions
    
    */
//    public String[] getColumnValues(String table, Column targetColumn, boolean match, Cell... conditions) throws SQLException{
//        String query = String.format("SELECT %s FROM %s%s", targetColumn.getName(false), table, getConditionLine(conditions, match));
//        ResultSet rs;
//        rs = connection.createStatement().executeQuery(query);
//        int retVal = 0;
//        while(rs.next()){
//            retVal = rs.getInt("count(*)");
//        }
//        rs.close();
//        return retVal;
//    }
//    
    
    public void removeRows(String table, boolean match, Cell... conditions) throws SQLException{
        //DELETE from trns_20210109093529 where row_id = 2
        String query = String.format("DELETE from %s%s", table, getConditionLine(conditions, match));
        connection.createStatement().execute(query);
    }
    
    public int getRowCount(String table, boolean match, Cell... conditions) throws SQLException{
        String query = String.format("SELECT count(*) FROM %s%s", table, getConditionLine(conditions, match));
        ResultSet rs;
        rs = connection.createStatement().executeQuery(query);
        int retVal = 0;
        while(rs.next()){
            retVal = rs.getInt("count(*)");
        }
        rs.close();
        return retVal;
    }
    
    private void insertRow(String table, Cell... cells) throws SQLException{
        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", table, getColumnNames(cells), getValueLines(cells));
        connection.createStatement().execute(query);
    }
    
    private Cell[][] getRowValues(String table, Column[] targets, boolean match, Cell... conditions) throws SQLException{
        String names = getColumnNames(targets);
        String query = String.format("SELECT %s FROM %s%s", names, table, getConditionLine(conditions, match));
        List<Cell[]> cellMap = new ArrayList();
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            while(rs.next()){
                Cell[] cells = new Cell[targets.length];
                for(int i = 0; i<targets.length; i++){
                    cells[i] = new Cell(targets[i], rs.getString(targets[i].getName(false)));
                }
                cellMap.add(cells);
            }
        }
        return cellMap.toArray(new Cell[cellMap.size()][]);
    }
    
    private boolean hasRow(String table, boolean mustMatchAll, Cell... conditions) throws SQLException{
        String names = getColumnNames(conditions);
        String query = String.format("SELECT %s FROM %s%s", names, table, getConditionLine(conditions, mustMatchAll));
        boolean notEmpty;
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            notEmpty = false;
            while(rs.next()){
                notEmpty = true;
                break;
            }
        }
        return notEmpty;
    }
    
    private boolean hasTable(String table) throws SQLException{
        String query = String.format("SELECT name FROM sqlite_master WHERE type='table' AND name=\'%s\'", table);
        
        boolean tableExist;
        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            tableExist = false;
            while(rs.next()){
                if(rs.getString("name").equals(table)) tableExist = true;
            }
        }
        return tableExist;
    }
    
    private void createTable(String table, Column... cols) throws SQLException{
        String columnsLine = getColumnLines(cols);
        String query = String.format("CREATE TABLE IF NOT EXISTS %s (%s)", table, columnsLine);
        connection.createStatement().execute(query);
    }
    
    private String getConditionLine(Cell[] conditionCells, boolean match){
        int length = conditionCells.length;
        if(length == 0) return "";
        
        StringBuilder sb = new StringBuilder();
        sb.append(" WHERE ");
        for(int i = 0; i<length; i++){
            if(i != 0) sb.append(match?" AND ":" OR ");
            
            String val;
            if(conditionCells[i].getColumn().isText()) val = "\'"+conditionCells[i].getValue()+"\'";
            else val = conditionCells[i].getValue();
            sb.append(conditionCells[i].getColumn().getName(false)).append("=").append(val);
        }
        return sb.toString();
    }
    
    public String getValueLines(Cell... cells){
        int length = cells.length;
        if(length == 0) return "";
        
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<length; i++){
            if(i != 0) sb.append(",");            
            String val;
            if(cells[i].getColumn().isText()) val = "\'"+cells[i].getValue()+"\'";
            else val = cells[i].getValue();
            sb.append(val);
        }
        return sb.toString();
    }
    
    private String getColumnNames(Cell... cells){
        StringBuilder sb = new StringBuilder();
        for (Cell cell : cells) {
            sb.append(cell.getColumn().getName(false)).append(",");
        }
        sb.replace(sb.length()-1, sb.length(), "");
        return sb.toString();
    }
    
    private String getColumnNames(Column... cols){
        StringBuilder sb = new StringBuilder();
        for(Column col : cols){
            sb.append(col.getName(false)).append(",");
        }
        sb.replace(sb.length()-1, sb.length(), "");
        return sb.toString();
    }
    
    private String getColumnLines(Column... cols){
        StringBuilder sb = new StringBuilder();
        for(Column col : cols){
            sb.append(col.getLine()).append(",");
        }
        sb.replace(sb.length()-1, sb.length(), "");
        return sb.toString();
    }
    
    private String getAccountsTablename(String userId){
        return "accs_"+userId;
    }
    
    private String getTransactionsTablename(String userId){
        return "trns_"+userId;
    }
    
    public static DatabaseInterface connectInterface(File dataFile) throws SQLException{
        return new DatabaseInterface(dataFile);
    }
}
