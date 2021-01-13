/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.panel;

import app.Account;
import app.CustomComponents;
import app.Main;
import app.Theme;
import app.Transaction;
import app.db.Decimal;
import comp.DateChooser;
import comp.LinearPanel;
import comp.LinearPanel.Orientation;
import comp.NewComboBox;
import comp.XButton;
import comp.XScrollPanel;
import comp.XTextField;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;

/**
 *
 * @author Christian
 */
public class AddTransactionPanel extends XScrollPanel{
    
    public final static String[] INCOME_CATEGORIES = new String[]{
        "Salary", "Other Income", "Reimbursements", "Gifts", "Investments"
    };
    
    public final static String[] EXPENSE_CATEGORIES = new String[]{
        "Apparel", "Bills", "Education", "Food", "Gadgets", "Insurance", "Allowance", "Transportation", "Witheld Expenses", "Other Expenses", "Investments"
    };
            
    
    private final LinearPanel linearPanel;
    public final static int WIDTH_CONSTANT = 240;
    
    private final static int spacing = 8;
    public final static int COMP_WIDTH = WIDTH_CONSTANT - spacing*2;
    
    private final Account account;
    
    private final ButtonGroup buttonGroup;
    private final XButton[] typeButtons;
    
    private final XTextField amountField;
    private final XTextField detailsField;
    private final NewComboBox cb_categ;;
    private final NewComboBox cb_accounts;
    private final XButton addButton;
    private List<Integer> otherAccountIndices;
    
    public boolean initialBalance = false;

    public AddTransactionPanel(String userId, Account acc, AccountPanel.AddTransactionListener lis) {
        account = acc;
        linearPanel = new LinearPanel(Orientation.VERTICAL, new Point(spacing, spacing));
        linearPanel.setOpaque(false);
        linearPanel.setBackground(Theme.getPanelBackground());
        super.setBackground(Theme.getPanelBackground());        
        
        //linearPanel.setBackground(Theme.getPanelBackground());
        
        linearPanel.setSize(WIDTH_CONSTANT, 0);
        super.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Theme.getAuxiliaryButtonBackground()[0]));//Theme.getAuxiliaryButtonBackground()[0]));
        
        buttonGroup = new ButtonGroup();
        typeButtons = new XButton[4];
        String[] typeButtonString = new String[]{"Debit", "Credit", "Transfer", "Update balance"};
        for(int i = 0; i<4; i++){
            typeButtons[i] = CustomComponents.getAuxiliaryButton(typeButtonString[i]);
            typeButtons[i].setSize(COMP_WIDTH/2, 32);
            typeButtons[i].setToggleEnabled(true);
            buttonGroup.add(typeButtons[i]);
        }
        
        amountField = CustomComponents.getNumericalTextField();
        amountField.setSize(COMP_WIDTH, 32);
        detailsField = CustomComponents.getTransactionTextField(COMP_WIDTH, 96);
        
        linearPanel.addComponent(8, CustomComponents.getHeadingLabel("Add Transaction"));
        linearPanel.addComponent(32, CustomComponents.getFadedCaption("Transaction Type"));
        linearPanel.addComponentHorizontally(0, typeButtons[0]);
        linearPanel.addComponentHorizontally(0, typeButtons[1]);
        linearPanel.newRow(spacing);
        linearPanel.addComponentHorizontally(0, typeButtons[2]);
        linearPanel.addComponentHorizontally(0, typeButtons[3]);
        linearPanel.newRow(spacing);
        //for(int i = 0; i<typeButtons.length; i++) linearPanel.addComponent(typeButtons[i]);
        
        linearPanel.addComponent(32, CustomComponents.getFadedCaption("Transfer to Account"));
        
        List<String> accountNames = new ArrayList();
        otherAccountIndices = new ArrayList();
        try {
            Account[] accounts = Main.getInterface().getAccounts(userId);
            for(Account otherAccounts : accounts){
                if(otherAccounts.getAccountId() != acc.getAccountId()){
                    otherAccountIndices.add(otherAccounts.getAccountId());
                    accountNames.add(otherAccounts.getFullname());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddTransactionPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        cb_accounts = CustomComponents.getTransactionCategoryBox(accountNames.toArray(new String[accountNames.size()]));
        linearPanel.addComponent(cb_accounts);
        
        linearPanel.addComponent(32, CustomComponents.getFadedCaption("Amount"));
        linearPanel.addComponent(amountField);
        
        linearPanel.addComponent(32, CustomComponents.getFadedCaption("Details"));
        linearPanel.addComponent(detailsField);
        
        linearPanel.addComponent(8, CustomComponents.getFadedCaption("Category"));
        
        cb_categ = CustomComponents.getTransactionCategoryBox(new String[]{});
        linearPanel.addComponent(cb_categ);
        
        DateChooser dc = new DateChooser();
        dc.setDefaultForeground(Theme.getLabelForeground());
        dc.setSelectedBackground(Theme.getHightlightColor());
        dc.setSize(COMP_WIDTH, COMP_WIDTH);
        dc.setBackground(Theme.getPanelBackground());        
        
        linearPanel.addComponent(32, CustomComponents.getFadedCaption("Date"));
        linearPanel.addComponent(dc);
        
        addButton = CustomComponents.getAuxiliaryButton("Add");
        addButton.setSize(COMP_WIDTH, 32);
        linearPanel.addComponent(32, addButton);
        linearPanel.addSpace(8);
        linearPanel.setSize(WIDTH_CONSTANT, linearPanel.getPreferredSize().height);
        
        super.add(linearPanel);
        super.setSize(WIDTH_CONSTANT, 0);
        
        AddTransactionPanel.this.setInitialControls(true);
        AddTransactionPanel.this.toggleTransferType(false);
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amountString = new String(amountField.getField().getPassword());
                String details = new String(detailsField.getField().getPassword());
                Decimal decimal = new Decimal(amountString);
                Calendar c = dc.getSelectedCalendar();
                String dateString = String.format("%04d%02d%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                int date = Integer.parseInt(dateString);
                
                int type;
                if(initialBalance) type = 0;
                else if(typeButtons[0].isSelected()) type = Transaction.getTypeInteger(Transaction.TransactionType.DEBIT);
                else if(typeButtons[1].isSelected()) type = Transaction.getTypeInteger(Transaction.TransactionType.CREDIT);
                else if(typeButtons[2].isSelected()) type = Transaction.getTypeInteger(Transaction.TransactionType.TRANSFER);
                else type = 0;
                
                Transaction t = new Transaction(
                        -1, acc.getAccountId(), 
                        typeButtons[2].isSelected()?(otherAccountIndices.get(cb_accounts.getSelectedIndex())):0, 
                        type, cb_categ.getSelectedItem(), decimal.getNumericalValue(), date, details, 0);
                lis.add(t);
            }
        });
        
        typeButtons[0].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateAddButton();
                cb_categ.removeAllItems();                
                cb_categ.addItems(account.isLiability()?INCOME_CATEGORIES:EXPENSE_CATEGORIES);
                cb_categ.getMainButton().setEnabled(true);
            }
        });
        
        typeButtons[1].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateAddButton();
                cb_categ.removeAllItems();                
                cb_categ.addItems(account.isAsset()?INCOME_CATEGORIES:EXPENSE_CATEGORIES);
                cb_categ.getMainButton().setEnabled(true);
            }
        });
        
        typeButtons[2].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                toggleTransferType(e.getStateChange() == ItemEvent.SELECTED);
                updateAddButton();
                cb_categ.removeAllItems();   
                cb_categ.addItems(new String[]{"Transfer"});  
                cb_categ.getMainButton().setEnabled(false);
            }
        });
        
        typeButtons[3].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateAddButton();
            }
        });
        
        amountField.getField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                amountField.getField().setSelectionStart(0);
                amountField.getField().setSelectionEnd(amountField.getField().getPassword().length);
            }

            @Override
            public void focusLost(FocusEvent e) {
                
            }
        });
        
        detailsField.getField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                updateAddButton();
            }
        });
        
        updateAddButton();
    }
    
    private void updateAddButton(){
        if(buttonGroup.getSelection() == null){
            addButton.setEnabled(false);
            return;
        }
        if(detailsField.getField().getPassword().length == 0 && !typeButtons[2].isSelected()) {
            addButton.setEnabled(false);
            return;
        }
        
        addButton.setEnabled(true);
    }
    
    public void toggleTransferType(boolean b){
        linearPanel.getComponent(6).setVisible(b);
        linearPanel.getComponent(7).setVisible(b);
        
        int totalH = linearPanel.getComponent(6).getHeight() + linearPanel.getComponent(7).getHeight() + 32;
         
        for(int i = 8; i<linearPanel.getComponentCount(); i++){
            Component c = linearPanel.getComponent(i);
            int y = c.getY();
            if(b) y += totalH;
            else y -= totalH;
            c.setLocation(c.getX(), y);
        }
        
        linearPanel.setSize(WIDTH_CONSTANT, linearPanel.getHeight() + (b?totalH:-totalH));
        linearPanel.repaint();
        super.repaint();
    }
    
    private boolean previouseControls = false;
    
    public void setInitialControls(boolean b){
        if(previouseControls == b) return;
        else previouseControls = b;
        
        typeButtons[0].setEnabled(!b);
        typeButtons[1].setEnabled(!b);
        typeButtons[2].setEnabled(!b);
        typeButtons[3].setSelected(b);
        
        if(b){
            amountField.getField().requestFocus();
            detailsField.setEnabled(false);
            detailsField.getField().setText("Starting Balance");
            initialBalance = true;
            
            cb_categ.removeAllItems();   
            cb_categ.addItems(new String[]{"Balance"});  
            cb_categ.getMainButton().setEnabled(false);
        }
        else{
            detailsField.setEnabled(true);
            buttonGroup.clearSelection();
            detailsField.getField().setText(null);
            initialBalance = false;
            
            cb_categ.removeAllItems();  
            cb_categ.addItems(new String[]{"Select a transaction type"});
            cb_categ.getMainButton().setEnabled(false);
        }
        
        updateAddButton();
    }
    
    
    
    
}
