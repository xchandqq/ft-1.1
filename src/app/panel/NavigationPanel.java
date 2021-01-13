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
import comp.ButtonGroupListener;
import comp.LinearPanel;
import comp.XButton;
import comp.XButtonGroup;
import comp.XLabel;
import comp.XScrollPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

/**
 *
 * @author Christian
 */
public class NavigationPanel extends LinearPanel{
    
    private final String userId;
    
    private final XButtonGroup navigationButtonGroup;
    private XButtonGroup accountsButtonGroup;
    private final Component parent;
    private final XButtonGroup othersButtonGroup;
    private final XScrollPanel xsc;
    
    private ButtonGroupListener statementListener;
    private ButtonGroupListener accountListener;
    
    private final int buttonHeight = 32;

    public NavigationPanel(Component parent, String userId) throws SQLException {
        super(Orientation.VERTICAL, new Point());
        this.userId = userId;
        
        super.setLayout(null);
        super.setBackground(Theme.getAuxiliaryButtonBackground()[0]);
        //super.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 4, Theme.getAuxiliaryButtonBackground()[0]));//Theme.getAuxiliaryButtonBackground()[0]));
        
        this.parent = parent;
        this.parent.addComponentListener(parentListener);
        
        navigationButtonGroup = new XButtonGroup(
                new Dimension(180, buttonHeight*5), false, 0, true,
                CustomComponents.getNavigationButton("Accounts Statement"),
                CustomComponents.getNavigationButton("I&E Statement"),
                CustomComponents.getNavigationButton("Monthly Statement"),
                CustomComponents.getNavigationButton("Annual Statement"),
                CustomComponents.getNavigationButton("Transactions")
        );
        
        XLabel accountLabel = CustomComponents.getFadedCaption("Accounts");
        
        othersButtonGroup = new XButtonGroup(new Dimension(180, buttonHeight*2), false, 0, false, 
                CustomComponents.getAuxiliaryButton("Add Account"),
                CustomComponents.getAuxiliaryButton("Settings")
        );
        
        xsc = new XScrollPanel();
        xsc.setSize(180, 600);
        xsc.setBackground(Theme.getAuxiliaryButtonBackground()[0]);
        
        String firstname = Main.getInterface().getUserFirstname(userId);
        
        super.addSpace(8);
        super.addComponentHorizontally(8, CustomComponents.getHeadingLabel("Hi, "+firstname+"!"));
        super.newRow(0);
        
        super.addSpace(24);
        super.addComponentHorizontally(8, CustomComponents.getFadedCaption("Financial Statements"));
        super.newRow(0);
        
        super.addComponent(navigationButtonGroup);
        
        super.addSpace(24);
        super.addComponentHorizontally(8, accountLabel);
        super.newRow(0);
        
        super.addComponent(xsc);
        super.addComponent(othersButtonGroup);
        
        othersButtonGroup.getButton(0).addActionListener((ActionEvent ae) -> {
            AddAccountDialog aad = new AddAccountDialog(userId);
            if(aad.hasAddCommand()){
                try {
                    Account acc = aad.getAccount();
                    Main.getInterface().addAccountWithBalance(userId, acc, aad.getInitialBalance());
                    updateAccountsList(acc);
                } catch (SQLException ex) {
                    Logger.getLogger(NavigationPanel.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, "Datebase Error: "+ex.getLocalizedMessage(), "", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        navigationButtonGroup.setButtonGroupListener(new ButtonGroupListener() {
            @Override
            public void action(ActionEvent e, int buttonIndex) {
                accountsButtonGroup.getButtonGroup().clearSelection();
                statementListener.action(e, buttonIndex);
            }
        });
        
        //super.setSize(super.getPreferredSize());
        NavigationPanel.this.updateAccountsList(null);
    }
    
    public void setStatementListener(ButtonGroupListener lis){
        statementListener = lis;
    }
    
    public void setAccountListener(ButtonGroupListener lis){
        accountListener = lis;
    }
    
    public void updateAccountsList(Account toggleToAccount) throws SQLException{
        int w = 180;
        int h = buttonHeight;
        Account[] accs = Main.getInterface().getAccounts(userId);
        XButton[] xbtns = new XButton[accs.length];
        int selection = -1;
        for(int i = 0; i<accs.length; i++){
            xbtns[i] = CustomComponents.getNavigationButton(accs[i].getFullname());
            xbtns[i].setName("acc_id="+accs[i].getAccountId());
            if(toggleToAccount != null && selection==-1){
                if(accs[i].getFullname().equals(toggleToAccount.getFullname())) selection = i;
            }
        }
        accountsButtonGroup = new XButtonGroup(new Dimension(w, h*xbtns.length), false, 0, true, xbtns);    
        accountsButtonGroup.setOpaque(false);
        accountsButtonGroup.getButtonGroup().clearSelection();    
        accountsButtonGroup.setButtonGroupListener(new ButtonGroupListener() {
            @Override
            public void action(ActionEvent e, int buttonIndex) {
                navigationButtonGroup.getButtonGroup().clearSelection();
                accountListener.action(e, buttonIndex);
            }
        });
        
        if(selection != -1){
            xbtns[selection].doClick();
        }
        
        xsc.removeAll();
        xsc.add(accountsButtonGroup);
        xsc.repaint();
    }
    
    private final ComponentListener parentListener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            int w = getPreferredSize().width;
            int h = ((Component)e.getSource()).getHeight();
            setSize(w,h);
            xsc.setSize(xsc.getWidth(), h - xsc.getY() - 8 - othersButtonGroup.getHeight() - 8);
            othersButtonGroup.setLocation(othersButtonGroup.getX(), h - othersButtonGroup.getHeight() - 8);
        }
    };
}
