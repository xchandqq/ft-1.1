/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.panel;

import app.Theme;
import comp.ButtonGroupListener;
import comp.XButton;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author Christian
 */
public class MainPanel extends JPanel{

    private final String userId;
    private final NavigationPanel navigationPanel;
    private AccountPanel accountPanel = null;
    
    public MainPanel(String userId) throws SQLException {
        this.userId = userId;
        
        super.setLayout(null);
        super.setBackground(Theme.getPanelBackground());
        
        navigationPanel = new NavigationPanel(this, userId);
        navigationPanel.setStatementListener(statementButtonGroupListener);
        navigationPanel.setAccountListener(accountButtonGroupListener);
        super.add(navigationPanel);        
    }
    
    private final ButtonGroupListener statementButtonGroupListener = new ButtonGroupListener() {
        @Override
        public void action(ActionEvent e, int buttonIndex) {
            
        }
    };
    
    private final ButtonGroupListener accountButtonGroupListener = new ButtonGroupListener() {
        @Override
        public void action(ActionEvent e, int buttonIndex) {
            XButton button = (XButton) e.getSource();
            int accountId = Integer.parseInt(button.getName().split("=")[1]);
            try {
                if(getComponentCount() == 2) remove(accountPanel);
                accountPanel = new AccountPanel(MainPanel.this, userId, accountId);
                accountPanel.setLocation(navigationPanel.getWidth(), 0);
                accountPanel.componentListener.componentResized(null);
                if(MainPanel.this.getComponentCount() == 1) MainPanel.this.add(accountPanel);
                accountPanel.setSize(getWidth() - navigationPanel.getWidth(), getHeight());
                repaint();
                //Account acc = Main.getInterface().getAccount(userId, accountId);
                //System.out.println(acc.getFullname());
            } catch (SQLException ex) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
}
