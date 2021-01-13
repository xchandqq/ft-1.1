/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 *
 * @author Christian
 */
public class Theme {    
    
    private static boolean lightTheme = false;
    
    public static void setTheme(boolean light){
        lightTheme = light;
    }
    
    public static Color[] getAuxiliaryButtonBackground(){
        return BACKGROUND_AUXILIARY_BUTTON[lightTheme?0:1];
    }
    
    public static Color[] getApproveButtonBackground(){
        return BACKGROUND_APPROVE_BUTTON[lightTheme?0:1];
    }
    
    public static Color[] getDeclineButtonBackground(){
        return BACKGROUND_DECLINE_BUTTON[lightTheme?0:1];
    }
    
    public static Color[] getAuxiliaryButtonForeground(){
        return FOREGROUND_AUXILIARY_BUTTON[lightTheme?0:1];
    }
    
    public static Color[] getApproveButtonForeground(){
        return FOREGROUND_APPROVE_BUTTON[lightTheme?0:1];
    }
    
    public static Color[] getDeclineButtonForeground(){
        return FOREGROUND_DECLINE_BUTTON[lightTheme?0:1];
    }
    
    public static Color getPanelBackground(){
        return lightTheme?Color.WHITE:Color.BLACK;
    }
    
    public static Color getLabelForeground(){
        return lightTheme?Color.BLACK:Color.WHITE;
    }
    
    public static Border[] getAuxiliaryButtonBorder(){
        return BORDER_AUXILIARY_BUTTON[lightTheme?0:1];
    }
    
    public static Border[] getApproveButtonBorder(){
        return BORDER_APPROVE_BUTTON[lightTheme?0:1];
    }
    
    public static Border[] getDeclineButtonBorder(){
        return BORDER_DECLINE_BUTTON[lightTheme?0:1];
    }
    
    public static Color[] getTextFieldBackground(){
        return BACKGROUND_TEXTFIELD[lightTheme?0:1];
    }
    
    public static Color[] getTextFieldForeground(){
        return FOREGROUND_TEXTFIELD[lightTheme?0:1];
    }
    
    public static Border[] getTextFieldBorder(){
        return BORDER_TEXTFIELD[lightTheme?0:1];
    }
    
    public static Color getHightlightColor(){
        return hightlightColor[lightTheme?0:1];
    }
    
    private final static Color disabledBackgroundColor = new Color(128,128,128);
    private final static Color[] hightlightColor = new Color[]{
        new Color(167,122,92), new Color(133,88,58)
    };
    
    private final static Border[][] BORDER_TEXTFIELD = new Border[][]{
        new Border[]{
            BorderFactory.createLineBorder(new Color(109,109,109), 1),
            BorderFactory.createLineBorder(new Color(70,70,70), 2),
            null, null,
            BorderFactory.createLineBorder(Color.GRAY, 2),
            BorderFactory.createLineBorder(Color.RED, 2),
            null,
            BorderFactory.createLineBorder(hightlightColor[0], 2)//BorderFactory.createLineBorder(new Color(32,170,170), 2)
            
        },
        new Border[]{
            BorderFactory.createLineBorder(new Color(128,128,128), 1),
            BorderFactory.createLineBorder(new Color(168,168,168), 2),
            null, null,
            BorderFactory.createLineBorder(Color.GRAY, 2),
            BorderFactory.createLineBorder(Color.RED, 2),
            null,
            BorderFactory.createLineBorder(hightlightColor[1], 2)
        }
    };
    
    private final static Color[][] BACKGROUND_TEXTFIELD = new Color[][]{
        new Color[]{
            new Color(208,208,208),
            new Color(224,224,224),
            null, null, 
            new Color(208,208,208),
            
        },
        new Color[]{
            new Color(24, 24, 24),
            new Color(14, 14, 14),
            null, null, 
            new Color(24, 24, 24),
        }
    };
    
    private final static Color[][] FOREGROUND_TEXTFIELD = new Color[][]{
        new Color[]{
            Color.BLACK,
            null, null, null,
            Color.GRAY,            
            Color.BLACK,
            Color.BLACK,
            Color.BLACK
            
        },
        new Color[]{
            Color.WHITE,
            null, null, null,
            Color.GRAY,
            Color.BLACK,
            Color.BLACK,
            Color.BLACK
        }
    };
    
    private final static Border[][] BORDER_AUXILIARY_BUTTON = new Border[][]{
        new Border[]{
            BorderFactory.createEmptyBorder(),
            BorderFactory.createLineBorder(new Color(122,122,122), 2),
            null, null, null,  null, null,
            BorderFactory.createLineBorder(Color.BLACK, 2)
        },
        new Border[]{
            BorderFactory.createEmptyBorder(),
            BorderFactory.createLineBorder(new Color(133,133,133), 2),
            null, null, null,  null, null,        
            BorderFactory.createLineBorder(Color.WHITE, 2)
        }
    };
    
    private final static Border[][] BORDER_APPROVE_BUTTON = new Border[][]{
        new Border[]{
            BorderFactory.createEmptyBorder(),
            BorderFactory.createLineBorder(new Color(0, 244,0), 2),
            null, null, null,  null, null,
            BorderFactory.createLineBorder(Color.BLACK, 2)
        },
        new Border[]{
            BorderFactory.createEmptyBorder(),
            BorderFactory.createLineBorder(new Color(0,170,0), 2),
            null, null, null,  null, null,
            BorderFactory.createLineBorder(Color.WHITE, 2)
        }
    };
    
    private final static Border[][] BORDER_DECLINE_BUTTON = new Border[][]{
        new Border[]{
            BorderFactory.createEmptyBorder(),
            BorderFactory.createLineBorder(new Color(244,0,0), 2),
            null, null, null,  null, null,
            BorderFactory.createLineBorder(Color.BLACK, 2)
        },
        new Border[]{
            BorderFactory.createEmptyBorder(),
            BorderFactory.createLineBorder(new Color(170,0,0), 2),
            null, null, null,  null, null,
            BorderFactory.createLineBorder(Color.WHITE, 2)
        }
    };
    
    private final static Color[][] BACKGROUND_AUXILIARY_BUTTON = new Color[][]{
        new Color[]{            
            new Color(204,204,204),
            null,
            new Color(122,122,122),
            new Color(167,122,92),
            null
        },
        new Color[]{         
            new Color(51,51,51),
            null,
            new Color(133,133,133),
            new Color(133,88,58),
            null
        }
    };
    
    private final static Color[][] BACKGROUND_APPROVE_BUTTON = new Color[][]{
        new Color[]{
            new Color(0, 140, 0),//new Color(153,255,153),
            null,
            new Color(0, 244, 0),
            null,
            new Color(204,204,204),
        },
        new Color[]{
            new Color(96, 224, 96),//new Color(0,102,0),
            null,
            new Color(0, 170, 0),
            null,
            new Color(51,51,51),
        }
    };
    
    private final static Color[][] BACKGROUND_DECLINE_BUTTON = new Color[][]{
        new Color[]{
            new Color(196, 0, 0),//new Color(255,153,153),
            null,
            new Color(244, 0, 0),
            null,
            Color.GRAY
        },
        new Color[]{
            new Color(255,96,96),//new Color(102, 0, 0),
            null,
            new Color(170, 0, 0),
            null,
            Color.GRAY
        }
    };
    
    private final static Color[][] FOREGROUND_AUXILIARY_BUTTON = new Color[][]{
        new Color[]{
            Color.BLACK,
            null,
            null,
            null,
            new Color(128,128,128)
        },
        new Color[]{
            Color.WHITE,
            null,
            null,
            null,
            new Color(128,128,128)
        }
    };
    
    private final static Color[][] FOREGROUND_APPROVE_BUTTON = new Color[][]{
        new Color[]{
            Color.BLACK,
            null,
            null,
            null,
            new Color(128,128,128)
        },
        new Color[]{
            Color.WHITE,
            null,
            null,
            null,
            new Color(128,128,128)
        }
    };
    
    private final static Color[][] FOREGROUND_DECLINE_BUTTON = new Color[][]{
        new Color[]{
            Color.BLACK,
            null,
            null,
            null,
            new Color(128,128,128)
        },
        new Color[]{
            Color.WHITE,
            null,
            null,
            null,
            new Color(128,128,128)
        }
    };
}
