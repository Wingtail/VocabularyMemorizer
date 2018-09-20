import javafx.scene.input.KeyCode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI {
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private JPanel Definition;
    private JPanel Etymology;
    private JPanel Keywords;
    private JPanel Practise;
    private JTextField efield;
    private JTextField kfield;
    private JTextField pfield;
    private JTextField dfield;
    private JEditorPane eeditor;
    private JEditorPane deditor;
    private JEditorPane keyword;

    Word currentWord;

    String command;
    Dictionary dictionary;
    boolean autosave = false;
    boolean textErase = false;

    public GUI() {
        dictionary = new Dictionary();
        deditor.setContentType("text/html");
        eeditor.setContentType("text/html");
        keyword.setContentType("text/html");


        dfield.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(textErase)
                {
                    dfield.setText("");
                    textErase = false;
                }
                if(e.getKeyChar() == KeyEvent.VK_ENTER)
                {
                    command = dfield.getText();
                    deditor.setText("");
                    executeCommand();
                }
            }
        });
        dfield.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(textErase)
                {
                    dfield.setText("");
                    textErase = false;
                }
            }
        });
        efield.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(textErase)
                {
                    efield.setText("");
                    textErase = false;
                }
                if(e.getKeyChar() == KeyEvent.VK_ENTER)
                {
                    command = efield.getText();
                    deditor.setText("");
                    executeCommand();
                }
            }
        });
        efield.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(textErase)
                {
                    efield.setText("");
                    textErase = false;
                }
            }
        });
        kfield.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(textErase)
                {
                    kfield.setText("");
                    textErase = false;
                }
                if(e.getKeyChar() == KeyEvent.VK_ENTER)
                {
                    command = kfield.getText();
                    kfield.setText("");
                    executeCommand();
                }
            }
        });
        kfield.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(textErase)
                {
                    kfield.setText("");
                    textErase = false;
                }
            }
        });
    }

    public void executeCommand()
    {
        if(command.contains("/")) // then it is a command
        {
            command = command.replaceAll("\\s|/","");
            if(command.equals("save"))
            {
                dictionary.addWord(currentWord);
            }else if(command.equals("getWordList"))
            {
                dictionary.searchWordsInFile(command.substring(command.indexOf(" ")+1));
            }else if(command.equals("toggleAutoSave"))
            {
                autosave = !autosave;
                dfield.setText("autosave is "+autosave);
                efield.setText("autosave is "+autosave);
                kfield.setText("autosave is "+autosave);
                textErase = true;
            }else if(command.equals("saveDictionary"))
            {
                dictionary.save();
                dfield.setText("Dictionary saved- Words: "+dictionary.numWords);
                efield.setText("Dictionary saved- Words: "+dictionary.numWords);
                kfield.setText("Dictionary saved- Words: "+dictionary.numWords);
                textErase = true;
            }
            else if(command.equals("loadDictionary"))
            {
                dictionary.save();
                dfield.setText("Dictionary loaded- Words: "+dictionary.numWords);
                efield.setText("Dictionary loaded- Words: "+dictionary.numWords);
                kfield.setText("Dictionary loaded- Words: "+dictionary.numWords);
                textErase = true;
            }
        }else{
            currentWord = search(command);
            deditor.setText(definitionBuilder(currentWord));
            eeditor.setText(etymologyBuilder(currentWord));
            keyword.setText(keywordBuilder(currentWord));
        }
    }

    public String definitionBuilder(Word word)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><b>");
        builder.append("<font size = \"24\">");
        builder.append(word.getWord());
        builder.append("      ");
        builder.append(word.getPronounciation());
        builder.append("</font>");
        builder.append("</b>");

        int count = 0;
        builder.append("<ul>");
        for(String define:word.definitions())
        {
            builder.append("<li>");
            builder.append("<font size = \"12\">");
            builder.append(define);
            builder.append("</font>");
            builder.append("<ul><li>");
            builder.append("<font size = \"12\">");
            builder.append(word.getExampleSentence(count));
            builder.append("</font>");
            builder.append("</li></ul>");
            builder.append("</li>");
            count++;
        }
        builder.append("</ul>");
        builder.append("</html>");
        return builder.toString();
    }

    public String keywordBuilder(Word word)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><font size = \"36\">");
        builder.append(word.getWord());
        builder.append("</font>");

        builder.append("<ul>");
        for(String define:word.keywords())
        {
            builder.append("<li>");
            builder.append("<font size = \"18\">");
            builder.append(define);
            builder.append("</font>");
            builder.append("</li>");
        }
        builder.append("</ul>");
        builder.append("</html>");
        return builder.toString();
    }

    public String etymologyBuilder(Word word)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><b>");
        builder.append("<font size = \"36\">");
        builder.append(word.getWord());
        builder.append("</font>");
        builder.append("</b><br></br>");
        String[] words = word.etymology().split("\\s");
        boolean quoted = false;
        for(String w : words) {
            if(w.startsWith("\""))
            {
                quoted = true;
            }
            if(w.contains("French")||w.contains("Latin")||w.contains("Greek")) {
                builder.append("<font size = \"18\" color=\"blue\">");
                builder.append(w);
                builder.append(" ");
                builder.append("</font>");
            }
            else{
                if(quoted)
                {
                    builder.append("<font size = \"18\" color=\"green\">");
                    builder.append(w);
                    builder.append(" ");
                    builder.append("</font>");
                }else {
                    builder.append("<font size = \"18\">");
                    builder.append(w);
                    builder.append(" ");
                    builder.append("</font>");
                }
            }

            if(w.endsWith("\""))
            {
                quoted = false;
            }
        }
        builder.append("</html>");
        return builder.toString();
    }

    public Word search(String w)
    {
        Word word = null;

        Word[] words = dictionary.searchWord(w);
        if(words != null)
        {
            System.out.println("Triggered");
            word = words[0];
        }else{
            Thread t = new DataScraper(w,dictionary,false);
            t.start();
            DataScraper.threads.add(t);
            try {
                t.join();
            }catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            word = ((DataScraper) t).w;
            System.out.println("Done");
            DataScraper.threads.remove(t);
        }

        return word;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GUI");
        frame.setContentPane(new GUI().mainPanel);
        frame.setPreferredSize(new Dimension(1000,600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
