package client.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class MyJpopup extends JWindow {

    public MyJpopup(Window ownersWindow, Component inner, Point location){
        super(ownersWindow);
        add(inner);
        Point pCopy = new Point(location.x, location.y);
        SwingUtilities.convertPointFromScreen(pCopy, inner);
        setLocation(pCopy);
        installWindowFocusListener();
        pack();
        setVisible(true);
    }

    public static MyJpopup getInstance(Component owner, Component inner, Point location){
        Window ownersWindow = getComponentsWindow(owner);
        return new MyJpopup(ownersWindow , inner, location);
    }

    private void installWindowFocusListener(){
        final MyJpopup popup = this;
        addWindowFocusListener(new WindowFocusListener(){
            public void windowGainedFocus(WindowEvent e){
                //do nothing
            }

            public void windowLostFocus(WindowEvent e){
                popup.setVisible(false);
                popup.dispose();
            }
        });
    }

    private static Window getComponentsWindow(Component component){
        Component comp = component.getParent();
        if(comp instanceof Window){
            return (Window)comp;
        }else{
            return getComponentsWindow(comp);
        }
    }
}
