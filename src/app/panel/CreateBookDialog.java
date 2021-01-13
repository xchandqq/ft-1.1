/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.panel;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Christian
 */
public class CreateBookDialog extends JDialog{
    
    private final CreateBookPanel cbp;

    public CreateBookDialog() {
        super((JFrame)null, "Create Book", true);
        
        cbp = new CreateBookPanel(this);
        cbp.initializeStep(CreateBookPanel.STEP_PERSONAL_INFORMATION);
        
        super.add(cbp);
        super.setSize(cbp.getSize());
        super.setResizable(false);
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }
    
    public boolean hasCreated(){
        return cbp.hasCreated();
    }
    
    public String[] getInformation(){
        return new String[]{
            cbp.getLastName(),
            cbp.getMiddleName(),
            cbp.getFirstName(),
            cbp.getUsername(),
            cbp.getPassword()
        };
    }
    
    
    
}
