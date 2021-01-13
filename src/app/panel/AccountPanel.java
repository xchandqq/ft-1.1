/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.panel;

import app.Account;
import app.Balance;
import app.BalanceComputer;
import app.CustomComponents;
import app.Main;
import app.Theme;
import app.Transaction;
import app.TransactionRow;
import app.db.Decimal;
import comp.LinearPanel;
import comp.XButton;
import comp.XLabel;
import comp.XScrollPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 *
 * @author Christian
 */
public class AccountPanel extends LinearPanel{
    
    private final String userId;
    private final Component parent;
    private final Account account;
    
    private final XScrollPanel xsp;
    private final LinearPanel scrollPanel;
    private final AddTransactionPanel addTransactionPanel;
    
    private boolean emptyAccount = true;
    
    private final XLabel currentBalanceAmount;
    private final XLabel currentBalanceDate;
    private final XLabel changeInBalanceMonth;
    private final XLabel changeInBalanceYear;
    
    private TransactionRow[] rowPanels;
    private final XLabel contentInformationLabel;
    private final LinearPanel controlPanel;
    private final XButton[] controlButtons;
    private int pages;
    private int currentPage;
    private final int pageSize = 20;
    private final Map<Integer, String> accountNameMap = new HashMap();
    
    public AccountPanel(Component parent, String userId, int accountId) throws SQLException {
        super(Orientation.VERTICAL, new Point(16, 16));
        super.setOpaque(false);
        
        this.parent = parent;
        this.userId = userId;
        
        parent.addComponentListener(componentListener);
        
        account = Main.getInterface().getAccount(userId, accountId); 
        accountNameMap.put(accountId, account.getFullname());
        
        scrollPanel = new LinearPanel(Orientation.VERTICAL, new Point(0, 0));
        scrollPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        scrollPanel.setBackground(Theme.getPanelBackground());
        
        xsp = new XScrollPanel();      
        xsp.setBackground(Theme.getPanelBackground());
        xsp.add(scrollPanel);
        
        super.addComponent(CustomComponents.getBigTransparentButton(account.getFullname()));
        
        super.addComponentHorizontally(0, CustomComponents.getTransparentButton((account.isAsset()?"Asset":"Liability")+ " account"));
        if(account.isCurrent())  super.addComponentHorizontally(4, CustomComponents.getTransparentButton("Current"));        
        super.newRow(20);
        super.addSpace(16);
        
        XLabel label0 = CustomComponents.getFadedCaption("Current balance");
        label0.setSize(180, label0.getHeight());
        
        XLabel label1 = CustomComponents.getFadedCaption("Balance since");
        
        XLabel label2 = CustomComponents.getFadedCaption("Changes this month");
        label2.setSize(180, label0.getHeight());
        
        XLabel label3 = CustomComponents.getFadedCaption("Changes this year");
        
        currentBalanceAmount = CustomComponents.getHeadingLabel("-");
        currentBalanceAmount.setSize(180, currentBalanceAmount.getHeight());
        
        currentBalanceDate = CustomComponents.getHeadingLabel("-");
        currentBalanceDate.setSize(180, currentBalanceAmount.getHeight());
        
        changeInBalanceMonth = CustomComponents.getHeadingLabel("-");
        changeInBalanceMonth.setSize(180, changeInBalanceMonth.getHeight());
        
        changeInBalanceYear = CustomComponents.getHeadingLabel("-");
        changeInBalanceYear.setSize(180, changeInBalanceYear.getHeight());
        
        super.addComponentHorizontally(0, label0);
        super.addComponentHorizontally(0, label1);
        super.newRow(20);
        
        super.addComponentHorizontally(0, currentBalanceAmount);
        super.addComponentHorizontally(0, currentBalanceDate);
        super.newRow(20);
        super.addSpace(16);
                
        super.addComponentHorizontally(0, label2);
        super.addComponentHorizontally(0, label3); 
        super.newRow(20);
        
        super.addComponentHorizontally(0, changeInBalanceMonth);
        super.addComponentHorizontally(0, changeInBalanceYear);
        super.newRow(16);
        
        super.addComponent(16, CustomComponents.getFadedCaption("Account transactions"));
        super.addComponent(8, xsp);
        
        contentInformationLabel = CustomComponents.getFadedCaption("-");
        contentInformationLabel.setSize(240, contentInformationLabel.getPreferredSize().height);        
        super.addComponent(contentInformationLabel);        
        
        controlPanel = new LinearPanel(Orientation.HORIZONTAL, new Point(0,0));
        controlPanel.setBackground(Theme.getPanelBackground());
        controlButtons = new XButton[4];
        String[] s = new String[]{"<<", "Previous", "Next", ">>"};
        for(int i = 0; i<controlButtons.length; i++){
            controlButtons[i] = CustomComponents.getAuxiliaryButton(s[i]);
            controlButtons[i].setSize(60, 24);
            controlButtons[i].setFont(new Font("Segoe UI", Font.PLAIN, 11));
        }
        for(int i = 0; i<controlButtons.length; i++) controlPanel.addComponent(controlButtons[i]);
        controlPanel.setSize(controlPanel.getPreferredSize());
        super.addComponent(8, controlPanel);
        
        controlButtons[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPage(0);
            }
        });
        
        controlButtons[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPage(currentPage-1);
            }
        });
        
        controlButtons[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPage(currentPage+1);
            }
        });
        
        controlButtons[3].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPage(pages-1);
            }
        });
        
        
        addTransactionPanel = new AddTransactionPanel(userId, account, addTransactionListener);
        
        
        super.addComponent(addTransactionPanel);
        
        AccountPanel.this.updateAccountTransactions();
        
        addTransactionPanel.repaint();
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g); //To change body of generated methods, choose Tools | Templates.
        
        if(emptyAccount){
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(128, 128, 128, 192));
            g2d.fillRect(0, 0, getWidth() - addTransactionPanel.getWidth(), getHeight());
            
            Polygon polygon = new Polygon();
            polygon.addPoint(0, 0);
            polygon.addPoint(240, 0);
            polygon.addPoint(190, 16);
            polygon.addPoint(190, 92);
            polygon.addPoint(0, 92);
            polygon.translate(addTransactionPanel.getX() - 240, 200);
            
            int shadowLength = 10;
            for(int i = 0; i<shadowLength; i++){
                g2d.setColor(new Color(0,0,0,8));
                polygon.translate(-1, 1);
                g2d.fill(polygon);
            }
            polygon.translate(shadowLength, -shadowLength);
            
            g2d.setColor(Theme.getHightlightColor());
            g2d.fill(polygon);
            g2d.setColor(Theme.getLabelForeground());
            g2d.draw(polygon);
            
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            g2d.setColor(Theme.getLabelForeground());
            g2d.drawString("This account is incomplete", polygon.getBounds().x + 8, polygon.getBounds().y + 24);
            
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2d.drawString("To complete, update the current", polygon.getBounds().x + 8, polygon.getBounds().y + 48);
            g2d.drawString("balance as of your preferred date,", polygon.getBounds().x + 8, polygon.getBounds().y + 60);
            g2d.drawString("which is usually at the beginning", polygon.getBounds().x + 8, polygon.getBounds().y + 72);
            g2d.drawString("of the year.", polygon.getBounds().x + 8, polygon.getBounds().y + 84);
            
            g2d.dispose();
        }
    }

    @Override
    public boolean contains(int x, int y) {
        if(emptyAccount){
            return addTransactionPanel.getBounds().contains(x, y);
        }
        return super.contains(x, y); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void updateAccountTransactions(){
        scrollPanel.removeAll();
        scrollPanel.initialComponentX = 0;
        scrollPanel.initialComponentY = 0;
        scrollPanel.highestComponentH = 0;
        scrollPanel.highestComponentW = 0;
        
        LinearPanel accountRowHeader = CustomComponents.getAccountRowHeader();
        
        scrollPanel.addComponent(accountRowHeader);
        
        try {
            Transaction[] transactions = Main.getInterface().getAccountTransactions(userId, account.getAccountId());
            if(transactions.length == 0){
                XLabel lbl = CustomComponents.getSubheadingLabel("No transactions yet");
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
                lbl.setToPreferredSize();
                //lbl.setSize(totalRowWidth, 24);
                lbl.setHorizontalAlignment(JLabel.LEFT);
                scrollPanel.addComponent(lbl);
                scrollPanel.newRow(0);
                emptyAccount = true;
            }
            else{
                Transaction[] sortedTransactions = BalanceComputer.sortTransactions(transactions);
                Balance currentBalance = BalanceComputer.getCurrentBalance(account.getAccountId(), sortedTransactions);
                
                currentBalanceAmount.setText(currentBalance.amount.getCurrency());
                currentBalanceDate.setText(currentBalance.getStringDate());
                
                Decimal cibm = BalanceComputer.getMonthChange(account.getAccountId(), sortedTransactions).amount;
                changeInBalanceMonth.setText(cibm.getCurrency());
                if(cibm.getNumericalValue() > 0 && account.isAsset()) changeInBalanceMonth.setForeground(Theme.getApproveButtonBackground()[0]);
                else if(cibm.getNumericalValue() < 0 && account.isLiability()) changeInBalanceMonth.setForeground(Theme.getApproveButtonBackground()[0]);
                else if(cibm.getNumericalValue() > 0 && account.isLiability()) changeInBalanceMonth.setForeground(Theme.getDeclineButtonBackground()[0]);
                else if(cibm.getNumericalValue() < 0 && account.isAsset()) changeInBalanceMonth.setForeground(Theme.getDeclineButtonBackground()[0]);
                else changeInBalanceMonth.setForeground(Theme.getLabelForeground());
                
                Balance[] balances = BalanceComputer.getBalanceArray(account.getAccountId(), sortedTransactions);

                Decimal ciby = BalanceComputer.getYearChange(account.getAccountId(), sortedTransactions).amount;
                changeInBalanceYear.setText(ciby.getCurrency());
                if(ciby.getNumericalValue() > 0 && account.isAsset()) changeInBalanceYear.setForeground(Theme.getApproveButtonBackground()[0]);
                else if(ciby.getNumericalValue() < 0 && account.isLiability()) changeInBalanceYear.setForeground(Theme.getApproveButtonBackground()[0]);
                else if(ciby.getNumericalValue() > 0 && account.isLiability()) changeInBalanceYear.setForeground(Theme.getDeclineButtonBackground()[0]);
                else if(ciby.getNumericalValue() < 0 && account.isAsset()) changeInBalanceYear.setForeground(Theme.getDeclineButtonBackground()[0]);
                else changeInBalanceYear.setForeground(Theme.getLabelForeground());
                
                rowPanels = new TransactionRow[sortedTransactions.length]; 
                pages = (sortedTransactions.length/pageSize) + (sortedTransactions.length%pageSize>0?1:0);
                currentPage = 0;
                
                for(int i = 0; i<sortedTransactions.length; i++){
                    Transaction t = sortedTransactions[i];
                    
                    TransactionRow tr = new TransactionRow(account.getAccountId(), t, balances[i], transactionListener);
                    rowPanels[i] = tr;
                }
                
                showPage(currentPage);
                
                emptyAccount = false;
            }
            addTransactionPanel.setInitialControls(emptyAccount);
        } catch (SQLException ex) {
            Logger.getLogger(AccountPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private final TransactionRow.TransactionListener transactionListener = new TransactionRow.TransactionListener() {
        @Override
        public void deleteTransaction(Transaction t) {
            try {
                Main.getInterface().removeTransaction(userId, t);
                updateAccountTransactions();
            } catch (SQLException ex) {
                Logger.getLogger(AccountPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void refreshTransactions() {
            updateAccountTransactions();
        }
    };
    
    public void showPage(int page){
        currentPage = page;
        
        for(int i = 1; i<scrollPanel.getComponentCount();) scrollPanel.remove(i);
        scrollPanel.repaint();
        scrollPanel.initialComponentY = scrollPanel.getComponent(0).getHeight();        
        
        int lastIndex = rowPanels.length - page*pageSize - 1;
        int firstIndex = Math.max(lastIndex - pageSize + 1, 0);
        
        contentInformationLabel.setText(String.format("Showing %d - %d out of %d transactions", page*pageSize + 1, Math.min((page+1)*pageSize, rowPanels.length), rowPanels.length));
        
        String date = "";
        boolean newDate;
        
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                
        for(int i = lastIndex; i>=firstIndex; i--){            
            String cdate = Balance.getStringDate(rowPanels[i].getTransaction().getDate());
            
            if(date.isEmpty() || !cdate.equals(date)){
                if(cdate.contains(" "+currentYear)) cdate = cdate.substring(0, cdate.length()-5);
                XLabel dateLabel = CustomComponents.getTableRowDateLabel(cdate);
                scrollPanel.addSpace(16);
                scrollPanel.addComponentHorizontally(16, dateLabel);
                scrollPanel.newRow(0);
                date = cdate;
                newDate = true;
            }
            else newDate = false;
            
            if(rowPanels[i].isTransfer()){
                int toId = rowPanels[i].getTransaction().getAccountIdTo();
                if(!accountNameMap.containsKey(toId)){
                    try {
                        Account acc = Main.getInterface().getAccount(userId, toId);
                        accountNameMap.put(toId, acc.getFullname());
                    } catch (SQLException ex) {
                        Logger.getLogger(AccountPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                int frId = rowPanels[i].getTransaction().getAccountIdFrom();
                if(!accountNameMap.containsKey(frId)){
                    try {
                        Account acc = Main.getInterface().getAccount(userId, frId);
                        accountNameMap.put(frId, acc.getFullname());
                    } catch (SQLException ex) {
                        Logger.getLogger(AccountPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                rowPanels[i].updateTransferInformation(accountNameMap);
            }
            
            Border b = BorderFactory.createMatteBorder(newDate?1:0, 1, 1, 1, Color.GRAY);
            rowPanels[i].setBorder(b);
            
            scrollPanel.addComponent(rowPanels[i]);
            rowPanels[i].setBackground(new Color(128,128,128, i%2==(rowPanels.length%2)?92:160));
        }
        
        controlButtons[0].setEnabled((currentPage!=0 && pages>1));
        controlButtons[1].setEnabled((currentPage>0 && pages>1));
        controlButtons[2].setEnabled((currentPage<pages-1 && pages>1));
        controlButtons[3].setEnabled((currentPage!=pages-1 && pages>1));
        
        componentListener.componentResized(null);
    }
    
    public final ComponentListener componentListener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            setSize(parent.getWidth() - getX(), parent.getHeight());            
            addTransactionPanel.setLocation(getWidth() - addTransactionPanel.getWidth(), 0);
            addTransactionPanel.setSize(AddTransactionPanel.WIDTH_CONSTANT, getHeight());
            
            int scrollWidth = getWidth() - addTransactionPanel.getWidth() - 32;
            int scrollHeight = getHeight() - xsp.getY() - 8 - contentInformationLabel.getHeight() - 8 - controlPanel.getHeight() - 16;
            double widthRatio = (double)scrollWidth/760.0;   
            
            scrollPanel.setSize(Math.max(760, scrollWidth), scrollPanel.getPreferredSize().height);
            for(Component c : scrollPanel.getComponents()){
                if(c instanceof JPanel){
                    JPanel p = (JPanel) c;
                    int x = 0;
                    p.setSize(Math.max(760, scrollWidth), p.getHeight());

                    for(int i = 0; i<p.getComponentCount(); i++){
                        Component cc = p.getComponent(i);
                        cc.setLocation(x, cc.getY());
                        int w = (int) (TransactionRow.WIDTH_COLUMNS[i] * widthRatio);
                        w = Math.max(w, TransactionRow.WIDTH_COLUMNS[i]);
                        x += w;
                        cc.setSize(w, cc.getHeight());
                    }
                }
            }
            xsp.setSize(scrollWidth/*Math.min(scrollWidth, scrollPanel.getWidth())*/, Math.min(scrollHeight, scrollPanel.getHeight() + 12));
            contentInformationLabel.setLocation(contentInformationLabel.getX(), xsp.getY() + xsp.getHeight() + 8);
            controlPanel.setLocation(xsp.getX() + xsp.getWidth()/2 - controlPanel.getWidth()/2, contentInformationLabel.getY() + contentInformationLabel.getHeight() + 8);
            //controlPanel.setSize(scrollWidth, controlPanel.getHeight());
            
        }
    };
    
    private final AddTransactionListener addTransactionListener = new AddTransactionListener() {
        @Override
        public void add(Transaction t) {
            try {
                Main.getInterface().addTransaction(userId, t);
                updateAccountTransactions();
                repaint();
            } catch (SQLException ex) {
                Logger.getLogger(AccountPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    
    public interface AddTransactionListener{
        public void add(Transaction t);
    }
    
    
}
