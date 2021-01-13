/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.db.DatabaseInterface;
import app.panel.LoginPanel;
import app.panel.MainPanel;
import java.awt.Dimension;
import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Christian
 */
public class Main {   
    
    private static JFrame loginFrame;
    private static JFrame mainFrame;
    
    private static DatabaseInterface di;
    private static File dataDirectory;
    
    public static File getDataDirectory(){
        return dataDirectory;
    }
    
    public static DatabaseInterface getInterface(){
        return di;
    }
    
    public static void runLoginPanel(){
        loginFrame = new JFrame();
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        
        LoginPanel loginPanel = new LoginPanel(loginFrame);
        
        loginFrame.getContentPane().add(loginPanel);
        loginFrame.getContentPane().setPreferredSize(loginPanel.getPreferredSize());
        loginFrame.pack();
        loginFrame.setResizable(false);
        loginFrame.setLocationRelativeTo(null);
        
        loginFrame.setVisible(true);
    }
    
    public static void runMainPanel(String userId) throws SQLException{
        
        mainFrame = new JFrame(getInterface().getFullname(userId));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        MainPanel mainPanel = new MainPanel(userId);
        
        loginFrame.setVisible(false);
        loginFrame.dispose();
        
        mainFrame.getContentPane().add(mainPanel);
        mainFrame.setMinimumSize(new Dimension(1024, 720));
        mainFrame.pack();
        mainFrame.setResizable(true);
        mainFrame.setLocationByPlatform(true);
        mainFrame.setVisible(true);
    }
    
    public static void main(String[] args){
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("PopupMenu.border", BorderFactory.createEmptyBorder());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        dataDirectory = new File("data");
        if(!dataDirectory.exists()) dataDirectory.mkdir();
        
        File dataFile = new File(dataDirectory, "data.db");
        
        try {
            di = DatabaseInterface.connectInterface(dataFile);
            if(!di.hasUserTable()) di.createUserTable();
            runLoginPanel();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
