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
import comp.LinearPanel;
import comp.XButton;
import comp.XButtonGroup;
import comp.XLabel;
import comp.XTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Christian
 */
public class CreateBookPanel extends LinearPanel{
    
    public final static int STEP_PERSONAL_INFORMATION   = 0;
    public final static int STEP_ACCOUNTS_INFORMATION   = 1;
    public final static int STEP_BOOK_CREDENTIALS       = 2;
    
    private final JDialog parentDialog;
    
    private final JLabel imgLabel;
    private final XLabel informationLabel;
    
    private final XLabel lbl_pi;
    private final XLabel lbl_ln;
    private final XLabel lbl_mn;
    private final XLabel lbl_fn;
    private final XTextField txt_ln;
    private final XTextField txt_mn;
    private final XTextField txt_fn;
    private final XButtonGroup bg_pi;
    
    private final XLabel lbl_bc;
    private final XTextField txt_un;
    private final XTextField txt_pw;
    private final XTextField txt_pw2;
    private final XButtonGroup bg_bc;
    
    public CreateBookPanel(JDialog dialog){
        super(Orientation.VERTICAL, new Point(0, 0));
        this.parentDialog = dialog;
        
        informationLabel = CustomComponents.getSubheadingLabel("Information label");
        informationLabel.setSize(240, 16);
        informationLabel.setHorizontalAlignment(JLabel.CENTER);
        
        super.setBackground(Theme.getPanelBackground());
        super.setLayout(null);
        
        imgLabel = new JLabel(new ImageIcon(getClass().getResource("/app/res/login_img.png")));
        imgLabel.setSize(imgLabel.getPreferredSize());      
        
        lbl_pi = CustomComponents.getHeadingLabel("Personal Information");
        lbl_ln = CustomComponents.getSubheadingLabel("Last name*");
        lbl_mn = CustomComponents.getSubheadingLabel("Middle name");
        lbl_fn = CustomComponents.getSubheadingLabel("First name*");        
        txt_ln = CustomComponents.getTextField();
        txt_mn = CustomComponents.getTextField();
        txt_fn = CustomComponents.getTextField();
        
        bg_pi = new XButtonGroup(new Dimension(240, 32), true, 8, false, 
                CustomComponents.getDeclineButton("Cancel"),
                CustomComponents.getApproveButton("Next"));
        
        txt_ln.setSize(240, 32);
        txt_mn.setSize(240, 32);
        txt_fn.setSize(240, 32);
        
        txt_ln.getField().addActionListener((ActionEvent e) -> {
            txt_mn.getField().requestFocus();
        });
        
        txt_mn.getField().addActionListener((ActionEvent e) -> {
            txt_fn.getField().requestFocus();
        });
        
        txt_fn.getField().addActionListener((ActionEvent e) -> {
            bg_pi.getButton(1).doClick();
        });
        
        txt_ln.getField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                txt_ln.setErrorEnabled(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(txt_ln.getField().getPassword().length == 0){
                    txt_ln.setErrorEnabled(true);
                    informationLabel.setForeground(Color.RED);
                    informationLabel.setText("Last name is empty");
                }
                else informationLabel.setText(null);
            }
        });
        
        txt_fn.getField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                txt_fn.setErrorEnabled(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(txt_fn.getField().getPassword().length == 0){
                    txt_fn.setErrorEnabled(true);
                    informationLabel.setForeground(Color.RED);
                    informationLabel.setText("First name is empty");
                }
                else informationLabel.setText(null);
            }
        });
        
        bg_pi.getButton(0).addActionListener((ActionEvent e) -> {
            dialog.setVisible(false);
            dialog.dispose();
        });
        
        bg_pi.getButton(1).addActionListener((ActionEvent e) -> {
            if(txt_fn.getField().getPassword().length == 0){
                txt_fn.setErrorEnabled(true);
                informationLabel.setForeground(Color.RED);
                informationLabel.setText("First name is empty");
            }
            else if(txt_ln.getField().getPassword().length == 0){
                txt_ln.setErrorEnabled(true);
                informationLabel.setForeground(Color.RED);
                informationLabel.setText("Last name is empty");
            }
            else{
                initializeBookCredentials();
                repaint();
                parentDialog.pack();
                informationLabel.setText(null);
            }
        });
        
        lbl_bc = CustomComponents.getHeadingLabel("Book Credentials");     
        txt_un = CustomComponents.getUsernameField();
        txt_pw = CustomComponents.getPasswordField();
        txt_pw2 = CustomComponents.getPasswordField();     
        txt_pw2.setPlaceHolder("Retype Password");
        
        bg_bc = new XButtonGroup(new Dimension(240, 32), true, 8, false, 
                CustomComponents.getDeclineButton("Back"),
                CustomComponents.getApproveButton("Finish"));
        
        txt_un.setSize(240, 32);
        txt_pw.setSize(240, 32);
        txt_pw2.setSize(240, 32);
        
        txt_un.getField().addActionListener((ActionEvent e) -> {
            txt_pw.getField().requestFocus();
        });
        
        txt_pw.getField().addActionListener((ActionEvent e) -> {
            txt_pw2.getField().requestFocus();
        });
        
        txt_pw2.getField().addActionListener((ActionEvent e) -> {
            bg_bc.getButton(1).doClick();
        });
        
        txt_un.getField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                txt_un.setErrorEnabled(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(txt_un.getField().getPassword().length == 0){
                    txt_un.setErrorEnabled(true);
                    informationLabel.setForeground(Color.RED);
                    informationLabel.setText("Username is empty");
                }
                else informationLabel.setText(null);
            }
        });
        
        txt_pw.getField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                txt_pw.setErrorEnabled(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(txt_pw.getField().getPassword().length == 0){
                    txt_pw.setErrorEnabled(true);
                    informationLabel.setForeground(Color.RED);
                    informationLabel.setText("Password is empty");
                }
                else informationLabel.setText(null);
            }
        });
        
        txt_pw2.getField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                txt_pw2.setErrorEnabled(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                String pw = new String(txt_pw.getField().getPassword());
                String pw2 = new String(txt_pw2.getField().getPassword());
                if(!pw.equals(pw2)){
                    txt_pw2.setErrorEnabled(true);
                    informationLabel.setForeground(Color.RED);
                    informationLabel.setText("Passwords do not match");
                }
                else informationLabel.setText(null);
            }
        });
        
        bg_bc.getButton(0).addActionListener((ActionEvent e) -> {
            initializePersonalInformation();
            repaint();
            parentDialog.pack();
        });
        bg_bc.getButton(1).addActionListener((ActionEvent e) -> {
            if(txt_un.getField().getPassword().length == 0){
                txt_un.setErrorEnabled(true);
                informationLabel.setForeground(Color.RED);
                informationLabel.setText("Username is empty");
                return;
            }
            else if(txt_pw.getField().getPassword().length == 0){
                txt_pw.setErrorEnabled(true);
                informationLabel.setForeground(Color.RED);
                informationLabel.setText("Password is empty");
                return;
            }
            else if(txt_un.getField().getPassword().length < 8){
                txt_un.setErrorEnabled(true);
                informationLabel.setForeground(Color.RED);
                informationLabel.setText("Username must have atleast 8 characters");   
                return;             
            }
            else if(txt_pw.getField().getPassword().length < 8){
                txt_pw.setErrorEnabled(true);
                informationLabel.setForeground(Color.RED);
                informationLabel.setText("Password must have atleast 8 characters");   
                return;
            }
            
            String pw = new String(txt_pw.getField().getPassword());
            String pw2 = new String(txt_pw2.getField().getPassword());
            if(!pw.equals(pw2)){
                txt_pw2.setErrorEnabled(true);
                informationLabel.setForeground(Color.RED);
                informationLabel.setText("Passwords do not match");
                return;
            }
            
                
            try {
                boolean usernameExists = Main.getInterface().usernameExists(new String(txt_un.getField().getPassword()));
                if(usernameExists){
                    txt_un.setErrorEnabled(true);
                    informationLabel.setForeground(Color.RED);
                    informationLabel.setText("Username already exists");
                }
                else{
                    String userId = new SimpleDateFormat("YYYYMMddHHmmss").format(new Date());
                    Main.getInterface().addUserCredentials(new String[]{
                        userId,
                        getLastName(), getMiddleName(), getFirstName(),
                        new String(txt_un.getField().getPassword()), pw
                    });
                    
                    Account coh = new Account("Cash on Hand", "CoH", Account.AccountType.ASSET, Account.FLAG_STAPLE | Account.FLAG_CURRENT);
                    Account rcv = new Account("Receivables", "Rcv", Account.AccountType.ASSET, Account.FLAG_STAPLE);
                    Account pyb = new Account("Payables", "Pyb", Account.AccountType.LIABILITY, Account.FLAG_STAPLE);
                    
                    Main.getInterface().addAccount(userId, coh);
                    Main.getInterface().addAccount(userId, rcv);
                    Main.getInterface().addAccount(userId, pyb);
                    
                    
            
                    create = true;
                    dialog.setVisible(false);
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, null, ex);
                txt_pw2.setErrorEnabled(true);
                informationLabel.setForeground(Color.RED);
                informationLabel.setText("Error occured with database");
            }
        });
        
    }
    
    public void initializeStep(int step){
        switch(step){
            case STEP_PERSONAL_INFORMATION:
                initializePersonalInformation();
                break;
            case STEP_BOOK_CREDENTIALS:
                initializeBookCredentials();
                break;
        }
    }
    
    private void initializePersonalInformation(){
        super.removeAll();
        super.initialComponentX = 0;
        super.initialComponentY = 0;           
        super.addComponent(0, imgLabel);        
        super.highestComponentW = 0;
        super.initialComponentX = 8;
        
        super.addComponent(16, lbl_pi);
        super.addComponent(-4, CustomComponents.getMinorCaptionLabel("* is required"));
        super.addComponent(16, lbl_ln);
        super.addComponent(txt_ln);
        super.addComponent(8, lbl_mn);
        super.addComponent(txt_mn);
        super.addComponent(8, lbl_fn);
        super.addComponent(txt_fn);
        super.addComponent(8, informationLabel);
        super.addComponent(24, bg_pi);
        super.addSpace(8);
        
        txt_ln.getField().requestFocus();
    }
    
    private void initializeBookCredentials(){
        super.removeAll();
        super.initialComponentX = 0;
        super.initialComponentY = 0;           
        super.addComponent(0, imgLabel);        
        super.highestComponentW = 0;
        super.initialComponentX = 8;
        
        super.addComponent(16, lbl_bc);
        super.addComponent(-4, CustomComponents.getMinorCaptionLabel("* is required"));
        super.addComponent(16, txt_un);
        super.addComponent(24, txt_pw);
        super.addComponent(8, txt_pw2);
        super.addComponent(8, informationLabel);
        super.addComponent(24, bg_bc);
        super.addSpace(8);
        
        txt_un.getField().requestFocus();
    }
    
    public boolean create = false;
    public boolean hasCreated(){
        return create;
    }
    
    public String getFirstName(){
        return new String(txt_fn.getField().getPassword());
    }
    
    public String getMiddleName(){
        return new String(txt_mn.getField().getPassword());
    }
    
    public String getLastName(){
        return new String(txt_ln.getField().getPassword());
    }
    
    public String getUsername(){
        return new String(txt_un.getField().getPassword());
    }
    
    public String getPassword(){
        return new String(txt_pw.getField().getPassword());
    }
    
}
