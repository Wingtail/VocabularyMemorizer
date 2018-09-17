import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;

public class GUI {

    private JPanel panel1;
    private JTextField textField1;
    private JTabbedPane tabbedPane1;

    Dictionary dictionary = new Dictionary();

    static String command;

    public static void showGUI()
    {

    }

    public GUI()
    {

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == VK_ENTER)
                {
                    command = textField1.getText();
                }
            }
        });
    }



    public static void main(String[] args)
    {
        JFrame frame = new JFrame("GUI");
        frame.setContentPane(new GUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        System.out.println(command);
    }




}
