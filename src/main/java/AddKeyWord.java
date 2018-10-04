import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class AddKeyWord {
    private JTextField textField1;
    public JPanel main;

    Quiz quiz;
    JFrame frame;

    public AddKeyWord(Quiz q, JFrame f) {
        this.quiz = q;
        frame = f;
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    String keyword = textField1.getText();
                    keyword.replaceAll("\\s|;|,|:", "");
                    if(!keyword.equals(""))
                    {
                        quiz.currentWord.addKeyword(keyword);
                    }
                    quiz.override();
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                }
            }
        });
    }
}
