/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.panel;

import app.Account;
import app.Balance;
import app.CustomComponents;
import app.Main;
import app.Theme;
import app.Transaction;
import app.db.Decimal;
import comp.DateChooser;
import comp.LinearPanel;
import comp.XButton;
import comp.XTextField;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Christian
 */
public class AddAccountDialog extends JDialog{

    private final String userId;
    
    private final LinearPanel linearPanel;
    
    private final XTextField fnameField;
    private final XTextField snameField;
    private final XButton[] typeButtons;
    private final XTextField balanceField;
    private final XButton finishBtn;
    private boolean hasDate = false;
    private boolean addCommand = false;
    private Calendar selectedCalendar = null;
    private Account createdAccount;
            
    public AddAccountDialog(String userId) {
        super((JFrame)null, "Add account", true);
        
        this.userId = userId;
        this.linearPanel = new LinearPanel(LinearPanel.Orientation.VERTICAL, new Point(16, 16));
        this.linearPanel.setBackground(Theme.getPanelBackground());
        
        this.linearPanel.addComponent(CustomComponents.getHeadingLabel("Add new account"));
        this.linearPanel.addComponent(CustomComponents.getSubheadingLabel("Give your new account a name and initial balance"));
        this.linearPanel.addComponent(16, CustomComponents.getFadedCaption("Account name"));
        
        fnameField = CustomComponents.getTextField();
        fnameField.setCharacterLimit(24);
        fnameField.setSize(180, 32);
        
        snameField = CustomComponents.getTextField();
        snameField.setCharacterLimit(3);
        snameField.setSize(80, 32);
        snameField.setPlaceHolder("Short name");
        
        this.linearPanel.addComponentHorizontally(0, fnameField);
        this.linearPanel.addComponentHorizontally(8, snameField);
        this.linearPanel.newRow();
        
        this.linearPanel.addComponent(16, CustomComponents.getFadedCaption("Account type"));
        
        ButtonGroup bg = new ButtonGroup();
        typeButtons = new XButton[2];
        typeButtons[0] = CustomComponents.getAuxiliaryButton("Asset");
        typeButtons[0].setSize(80, 32); 
        typeButtons[0].setToggleEnabled(true);
        bg.add(typeButtons[0]);
        
        typeButtons[1] = CustomComponents.getAuxiliaryButton("Liability");
        typeButtons[1].setSize(80, 32); 
        typeButtons[1].setToggleEnabled(true);     
        bg.add(typeButtons[1]);   
        
        typeButtons[0].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateButton();
            }
        });
        
        typeButtons[1].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateButton();
            }
        });
        this.linearPanel.addComponentHorizontally(0, typeButtons[0]);
        this.linearPanel.addComponentHorizontally(0, typeButtons[1]);
        this.linearPanel.newRow();
        
        this.linearPanel.addComponent(16, CustomComponents.getFadedCaption("Initial balance"));
        
        balanceField = CustomComponents.getNumericalTextField();
        balanceField.setSize(180, 32);
        
        XButton dateBtn = CustomComponents.getAuxiliaryButton("Select date");
        dateBtn.setSize(80, 32);
        dateBtn.setBorderOffset(0);
        dateBtn.addActionListener(actionListener);
        
        this.linearPanel.addComponentHorizontally(0, balanceField);
        this.linearPanel.addComponentHorizontally(8, dateBtn);
        this.linearPanel.newRow();        
        
        XButton cancelBtn = CustomComponents.getTransparentButton("Cancel");
        cancelBtn.setSize(80, 32);       
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddAccountDialog.this.dispose();
            }
        });
        
        finishBtn = CustomComponents.getAuxiliaryButton("Finish");
        finishBtn.setSize(80, 32);
        finishBtn.setEnabled(false);    
        finishBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createdAccount = new Account(
                        fnameField.getText(), snameField.getText(), 
                        typeButtons[0].isSelected()?Account.AccountType.ASSET:Account.AccountType.LIABILITY, 0
                ); 
                
                try {
                    if(!Main.getInterface().accountExists(userId, createdAccount)){                        
                        AddAccountDialog.this.dispose();
                        addCommand = true;
                    }   
                    else{
                        JOptionPane.showMessageDialog(AddAccountDialog.this, "Account name already exists", "", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(AddAccountDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        this.linearPanel.addSpace(48);
        this.linearPanel.addComponentHorizontally(0, cancelBtn);
        this.linearPanel.addComponentHorizontally(109, finishBtn);
        this.linearPanel.newRow();      
        
        this.linearPanel.addSpace(16);
        
        fnameField.getField().getDocument().addDocumentListener(documentListener);
        fnameField.getField().addFocusListener(focusListener);
        snameField.getField().getDocument().addDocumentListener(documentListener);
        balanceField.getField().getDocument().addDocumentListener(documentListener);
        
        super.add(linearPanel);
        super.pack();
        super.setResizable(false);
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            XButton b = (XButton) e.getSource();            
            JPopupMenu menu = new JPopupMenu();
            
            LinearPanel lp = new LinearPanel(LinearPanel.Orientation.VERTICAL, new Point(1, 1));
            lp.setBackground(Theme.getAuxiliaryButtonBackground()[0]);
            
            if(selectedCalendar == null){
                selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR)+1);
            }
            DateChooser dc = new DateChooser(selectedCalendar);
            dc.setDefaultForeground(Theme.getLabelForeground());
            dc.setSelectedBackground(Theme.getHightlightColor());
            dc.setSize(224, 224);
            dc.setBackground(Theme.getPanelBackground());   
            lp.addComponent(dc);
            lp.addSpace(1);
            lp.setSize(lp.getPreferredSize());
            
            ActionListener al = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    menu.setVisible(false);
                    selectedCalendar = dc.getSelectedCalendar();
                    b.setText(new SimpleDateFormat("MM/dd/yy").format(selectedCalendar.getTime()));
                    hasDate = true;
                    updateButton();
                }
            };
            dc.setActionListener(al);
            
            menu.add(lp);
            menu.setSize(lp.getSize());
            menu.show(b, 0, b.getHeight());
        }
    };
    
    private final FocusListener focusListener = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
            
        }

        @Override
        public void focusLost(FocusEvent e) {
            if(fnameField.getField().getPassword().length == 0) return;
            String[] words = new String(fnameField.getField().getPassword()).split(" ");
            String sname = "";
            try{
                switch (words.length) {
                    case 1:
                        if(words[0].length() < 3) return;
                        else sname = words[0].substring(0, 3);
                        break;
                    case 2:
                        if(words[0].length() > 1){
                            sname += words[0].substring(0, 2);
                            sname += words[1].charAt(0);
                        }
                        else{
                            sname += words[0].charAt(0);
                            sname += words[1].substring(0, 2);
                        }   
                        break;
                    default:
                        for(int i = 0; i<3; i++) sname+=words[i].charAt(0);
                        break;
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            snameField.getField().setText(sname);
        }
    };
    
    private final DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateButton();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateButton();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateButton();
        }
    };
    
    private void updateButton(){
        if(fnameField.getField().getPassword().length == 0) finishBtn.setEnabled(false);
        else if(snameField.getField().getPassword().length == 0) finishBtn.setEnabled(false);
        else if(!typeButtons[0].isSelected() && !typeButtons[1].isSelected()) finishBtn.setEnabled(false);
        else if(balanceField.getField().getPassword().length == 0) finishBtn.setEnabled(false);
        else if(!hasDate) finishBtn.setEnabled(false);
        else finishBtn.setEnabled(true);
    }
            
    public boolean hasAddCommand(){
        return addCommand;
    }
    
    public Account getAccount(){
        if(!hasAddCommand()) return null;
        return createdAccount; 
    }
    
    public Balance getInitialBalance(){
        int date = Integer.parseInt(
                String.format("%04d%02d%02d", 
                        selectedCalendar.get(Calendar.YEAR),
                        selectedCalendar.get(Calendar.MONTH),
                        selectedCalendar.get(Calendar.DAY_OF_MONTH))
        );
        
        return new Balance(date, new Decimal(balanceField.getText()));
    }
}
