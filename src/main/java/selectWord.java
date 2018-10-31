import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class selectWord {

    JPanel panel;
    JScrollPane scroll;
    JButton submit;

    JFrame frame;
    Quiz quiz;
    Dictionary dictionary;

    ArrayList<JCheckBox> checkBox = new ArrayList<>();

    public selectWord(Quiz q, JFrame f, Dictionary dic)
    {
        this.quiz = q;
        frame = f;
        this.dictionary = dic;

        panel = new JPanel();
        scroll = new JScrollPane();
        submit = new JButton("Submit");

        Box box = Box.createVerticalBox();

        for(Word word : dictionary.totalWords)
        {
            JCheckBox check = new JCheckBox(word.toString());
            box.add(check);
            checkBox.add(check);
        }

        scroll = new JScrollPane(box);
        scroll.setPreferredSize(new Dimension(200,500));

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int count = 0;
                ArrayList<Word> words = new ArrayList<>();

                for(JCheckBox check : checkBox)
                {
                    if(check.isSelected())
                    {
                        words.add(dictionary.totalWords.get(count));
                    }
                    count++;
                }
                quiz.makeQuizSet(words);
                quiz.nextWord();
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });



        panel.add(scroll);
        panel.add(submit);
    }

    /*public static void main(String[] args)
    {

        JFrame frame = new JFrame("VocabAssistant");
        frame.setContentPane(new selectWord().panel);
        frame.setPreferredSize(new Dimension(1000,600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }*/

}
