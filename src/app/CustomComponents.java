/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import app.panel.AddTransactionPanel;
import comp.LinearPanel;
import comp.NewComboBox;
import comp.XButton;
import comp.XLabel;
import comp.XTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 *
 * @author Christian
 */
public class CustomComponents{
    
    public static LinearPanel getAccountRowHeader(){
        LinearPanel lp = new LinearPanel(LinearPanel.Orientation.HORIZONTAL, new Point(0, 0));
        lp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lp.setBackground(Theme.getHightlightColor());
        String[] txts = new String[]{
            "Debit", "Credit", "Balance", "Category", "Details"
        };
        XLabel[] btns = new XLabel[txts.length];
        
        int off = 16;
        Border a = BorderFactory.createEmptyBorder(0, off, 0, off);
        Border b = BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY);
        Border c = BorderFactory.createCompoundBorder(b, a);
        
        for(int i = 0; i<btns.length; i++){
            btns[i] = new XLabel(txts[i]);
            btns[i].setFont(new Font("Segoe UI", Font.BOLD, 14));
            btns[i].setBackground(Theme.getHightlightColor());
            btns[i].setBorder(c);
            //btns[i].setForeground(Theme.getAuxiliaryButtonForeground());
            btns[i].setHorizontalAlignment(XButton.LEFT);//(i>=1&&i<=3)?XButton.RIGHT:XButton.LEFT);
            btns[i].setSize(TransactionRow.WIDTH_COLUMNS[i], 40);
            btns[i].setForeground(Theme.getLabelForeground());
            lp.addComponent(btns[i]);
        }
        lp.setSize(lp.getPreferredSize());
        return lp;
    }
    
    public static XButton getTransactionTypeButton(String text){
        XButton btn = new XButton(text, true);
        btn.setBackground(Theme.getApproveButtonBackground());
        btn.setForeground(Theme.getApproveButtonForeground());
        btn.setBorder(Theme.getApproveButtonBorder());
        return btn;
    }
    
    public static XTextField getNumericalTextField(){
        XTextField xtf = getTextField();
        xtf.setCharacterLimit(16);
        xtf.getField().setText("0");
        xtf.getField().setHorizontalAlignment(JTextField.RIGHT);
        xtf.getField().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(Character.isDigit(e.getKeyChar())){
                    //okay
                }
                else if(e.getKeyChar() == '.'){
                    //okay
                }
                else e.consume();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                
            }
        });
        xtf.getField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                
            }

            @Override
            public void focusLost(FocusEvent e) {
                try{
                    Double.parseDouble(new String(xtf.getField().getPassword()));
                }
                catch(Exception ex){
                    xtf.getField().setText("0");
                }
            }
        });
//        xtf.getField().getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                String s = new String(xtf.getField().getPassword());
//                if(s.con)
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                
//            }
//        });
        return xtf;
    }
    
    public static XTextField getTransactionTextField(int width, int characterLimit){
        XTextField xtf = getTextField();
        xtf.setCharacterLimit(characterLimit);
        xtf.setSize(width, 32);
        xtf.getField().setHorizontalAlignment(JTextField.LEFT);
        return xtf;
    }
    
    public static NewComboBox getTransactionCategoryBox(String[] s){
        NewComboBox<String> ncb = new NewComboBox(s);
        ncb.setSize(AddTransactionPanel.COMP_WIDTH, 32);
        ncb.setBackground(Theme.getAuxiliaryButtonBackground());
        ncb.setForeground(Theme.getAuxiliaryButtonForeground());
        ncb.setBorder(Theme.getAuxiliaryButtonBorder());
        ncb.getMainButton().setBorder(Theme.getTextFieldBorder());
        ncb.getMainButton().setBackground(Theme.getTextFieldBackground());
        ncb.getMainButton().setForeground(Theme.getTextFieldForeground());
        return ncb;
    }
    
    public static XLabel getFadedCaption(String text){
        XLabel label = new XLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(Theme.getLabelForeground().getRGB()&0x00FFFFFF | ((160)<<24), true));
        label.setToPreferredSize();
        return label;
    }
    
    public static XButton getNavigationButton(String text){
        XButton btn = new XButton(text, true);
        btn.setBorderOffset(0);
        
        btn.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
        btn.setHorizontalAlignment(JButton.LEFT);
        btn.setBackground(Theme.getAuxiliaryButtonBackground());
        btn.setForeground(Theme.getAuxiliaryButtonForeground());
        btn.setBorder(Theme.getAuxiliaryButtonBorder());
        
        btn.setBackground(new Color((Theme.getLabelForeground().getRGB()&0x00FFFFFF) | ((48)<<24), true), XButton.EVENT_ROLLOVER);
        
        btn.setBorder(null, XButton.EVENT_ROLLOVER);
        //btn.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Theme.getLabelForeground()), XButton.EVENT_ROLLOVER);
        return btn;
    }
    
    public static XButton getApproveButton(String text){
        XButton btn = new XButton(text);
        btn.setBackground(Theme.getApproveButtonBackground());
        btn.setForeground(Theme.getApproveButtonForeground());
        btn.setBorder(Theme.getApproveButtonBorder());
        return btn;
    }
    public static XButton getBigTransparentButton(String text){
        XButton btn = new XButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        btn.setBorderOffset(0);
        btn.setForeground(Theme.getAuxiliaryButtonForeground());
        btn.setBackground(new Color(0,0,0,0), XButton.EVENT_DEFAULT);
        btn.setBackground(new Color(128,128,128,128), XButton.EVENT_PRESSED);
        btn.setBorder(Theme.getAuxiliaryButtonBorder());
        btn.setBorder(BorderFactory.createLineBorder(new Color(128,128,128,128), 1), XButton.EVENT_ROLLOVER);
        btn.setSize(btn.getPreferredSize());
        return btn;
    }
    
    public static XButton getTransparentButton(String text){
        XButton btn = new XButton(text);
        btn.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        btn.setBorderOffset(0);
        btn.setForeground(Theme.getAuxiliaryButtonForeground());
        btn.setBackground(new Color(0,0,0,0), XButton.EVENT_DEFAULT);
        btn.setBackground(new Color(128,128,128,128), XButton.EVENT_PRESSED);
        btn.setBorder(Theme.getAuxiliaryButtonBorder());
        btn.setBorder(BorderFactory.createLineBorder(new Color(128,128,128,128), 1), XButton.EVENT_ROLLOVER);
        btn.setSize(btn.getPreferredSize());
        return btn;
    }
    
    public static XButton getAuxiliaryButton(String text){
        XButton btn = new XButton(text);
        btn.setBackground(Theme.getAuxiliaryButtonBackground());
        btn.setForeground(Theme.getAuxiliaryButtonForeground());
        btn.setBorder(Theme.getAuxiliaryButtonBorder());
        return btn;
    }
    
    public static XButton getDeclineButton(String text){
        XButton btn = new XButton(text);
        btn.setBackground(Theme.getDeclineButtonBackground());
        btn.setForeground(Theme.getDeclineButtonForeground());
        btn.setBorder(Theme.getDeclineButtonBorder());
        return btn;
    }
    
    public static XTextField getTextField(){
        XTextField xtf = new XTextField();
        xtf.getField().setForeground(Theme.getLabelForeground());
        xtf.setBorder(Theme.getTextFieldBorder());
        xtf.setBackground(Theme.getTextFieldBackground());
        xtf.setForeground(Theme.getTextFieldForeground());
        xtf.setCharacterLimit(24);
        return xtf;
    }
    
    public static XTextField getUsernameField(){        
        ImageIcon userImage = new ImageIcon(CustomComponents.class.getResource("/app/res/user.png")); //Freepik @ www.flaticon.com
        XTextField xtf = new XTextField(userImage, new Dimension(32,32), 8);
        xtf.setBorder(Theme.getTextFieldBorder());
        xtf.setBackground(Theme.getTextFieldBackground());
        xtf.setForeground(Theme.getTextFieldForeground());
        xtf.setPlaceHolder("Username");
        xtf.setCharacterLimit(24);
        return xtf;
    }
    
    public static XTextField getPasswordField(){        
        ImageIcon passImage = new ImageIcon(CustomComponents.class.getResource("/app/res/privacy.png")); //Freepik @ www.flaticon.com
        ImageIcon passImage2 = new ImageIcon(CustomComponents.class.getResource("/app/res/privacy_show.png")); 
        XTextField xtf = new XTextField(passImage, new Dimension(32,32), 8);
        xtf.getField().setForeground(Theme.getLabelForeground());
        xtf.setBorder(Theme.getTextFieldBorder());
        xtf.setBackground(Theme.getTextFieldBackground());
        xtf.setForeground(Theme.getTextFieldForeground());
        xtf.setAlternateImage(passImage2);
        xtf.setAsPassword(true);
        xtf.setPlaceHolder("Password");
        xtf.setCharacterLimit(24);
        return xtf;
    }
    
    public static XLabel getHeadingLabel(String s){
        XLabel label = new XLabel(s);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        label.setForeground(Theme.getLabelForeground());
        label.setToPreferredSize();
        return label;
    }
    
    public static XLabel getTableRowDateLabel(String s){
        XLabel label = new XLabel(s);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        Color c = Theme.getLabelForeground();
        label.setForeground(Theme.getHightlightColor());
        label.setToPreferredSize();
       // label.setSize(label.getPreferredSize().width, 32);
        return label;
    }
    
    public static XLabel getTableRowLabel(String s){
        XLabel label = new XLabel(s);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(Theme.getLabelForeground());
        label.setSize(label.getPreferredSize().width, 32);
        return label;
    }
    
    public static XLabel getSubheadingLabel(String s){
        XLabel label = new XLabel(s);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(Theme.getLabelForeground());
        label.setToPreferredSize();
        return label;
    }
    
    public static XLabel getMinorCaptionLabel(String s){
        XLabel label = new XLabel(s);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        label.setForeground(Theme.getLabelForeground());
        label.setToPreferredSize();
        return label;
    }
    
    

    
}
