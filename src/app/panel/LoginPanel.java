/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.panel;

import app.CustomComponents;
import app.Main;
import app.Theme;
import comp.LinearPanel;
import comp.XButton;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Christian
 */
public class LoginPanel extends LinearPanel{
    
    private final XTextField tf_user;
    private final XTextField tf_pass;
    private final XButton btn_login;
    private final XButton btn_create;
    
    private final JFrame parentFrame;
    
    public LoginPanel(JFrame frame) {
        super(Orientation.VERTICAL, new Point(0, 0));
        super.setBackground(Theme.getPanelBackground());
        
        this.parentFrame = frame;
        
        JLabel imgLabel = new JLabel(new ImageIcon(getClass().getResource("/app/res/login_img.png")));
        imgLabel.setSize(imgLabel.getPreferredSize());        
        
        super.setBackground(Theme.getPanelBackground());
        super.setLayout(null);
        
        tf_user = CustomComponents.getUsernameField();
        tf_user.setSize(240, 32);
        
        tf_pass = CustomComponents.getPasswordField();
        tf_pass.setSize(240, 32);
        
        XLabel informationLabel = CustomComponents.getSubheadingLabel("");
        informationLabel.setSize(240, 16);
        informationLabel.setHorizontalAlignment(JLabel.CENTER);
        
        btn_login = CustomComponents.getApproveButton("Login");
        btn_login.setSize(240, 32);
        
        btn_create = CustomComponents.getAuxiliaryButton("Create Book");
        btn_create.setSize(240, 32);
        
        XLabel label = CustomComponents.getSubheadingLabel("or");
        label.setSize(240, label.getPreferredSize().height);
        label.setHorizontalAlignment(JLabel.CENTER);
        
        tf_user.getField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                tf_user.setErrorEnabled(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(tf_user.getField().getPassword().length == 0){
                    tf_user.setErrorEnabled(true);
                    informationLabel.setForeground(Color.RED);
                    informationLabel.setText("Enter username");
                }
                else informationLabel.setText(null);
            }
        });
        
        tf_pass.getField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                tf_pass.setErrorEnabled(false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(tf_pass.getField().getPassword().length == 0){
                    tf_pass.setErrorEnabled(true);
                    informationLabel.setForeground(Color.RED);
                    informationLabel.setText("Enter password");
                }
                else informationLabel.setText(null);
            }
        });
        
        tf_user.getField().addActionListener((ActionEvent e) -> {
            tf_pass.getField().requestFocus();
        });
        
        tf_pass.getField().addActionListener((ActionEvent e) -> {
            btn_login.doClick();
        });
        
        btn_login.addActionListener((ActionEvent e) -> {
            
            String user = new String(tf_user.getField().getPassword());
            String password = new String(tf_pass.getField().getPassword());
            boolean correctCredentials;
            try {
                correctCredentials = Main.getInterface().hasUserCredentials(user, password);
                if(correctCredentials){
                    String userId = Main.getInterface().getUserId(user, password);
                    Main.runMainPanel(userId);
                }
                else{            
                    tf_pass.setErrorEnabled(true);
                    informationLabel.setForeground(Color.RED);
                    informationLabel.setText("Incorrect password");
                }
            } catch (SQLException ex) {
                Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, null, ex);
                tf_pass.setErrorEnabled(true);
                informationLabel.setForeground(Color.RED);
                informationLabel.setText("Error occurred with database");
            }
        });
        
        btn_create.addActionListener((ActionEvent e) -> {
            parentFrame.setVisible(false);
            CreateBookDialog cbd = new CreateBookDialog();
            parentFrame.setVisible(true);
            if(cbd.hasCreated()){
                int res = JOptionPane.showConfirmDialog(this, "Do you want to login?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(res == JOptionPane.YES_OPTION){
                    String[] information = cbd.getInformation();
                    tf_user.getField().setText(cbd.getInformation()[3]);
                    tf_pass.getField().setText(cbd.getInformation()[4]);
                    information = null;
                    btn_login.doClick();
                }
            }
        });
        
        LoginPanel.this.add(imgLabel);
        super.addComponent(0, imgLabel);
        super.highestComponentW = 0;
        super.initialComponentX = 8;        
        
        super.addComponent(16, CustomComponents.getHeadingLabel("Financial Tracker"));
        super.addComponent(-4, CustomComponents.getSubheadingLabel("The smart way to manage finances"));
        super.addComponent(16, tf_user);
        super.addComponent(8, tf_pass);
        super.addComponent(8, informationLabel);
        super.addComponent(8, btn_login);
        super.addComponent(0, label);
        super.addComponent(0, btn_create);
        super.addSpace(8);
        
        //test
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                tf_user.getField().setText("xchandqq");
                tf_pass.getField().setText("Jan_251995");
                btn_login.doClick();
            }
        }).start();
    }
    
    
    
}
