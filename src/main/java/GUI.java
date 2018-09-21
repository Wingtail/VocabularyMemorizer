import javafx.scene.input.KeyCode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

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
    private JEditorPane QuizEditor;
    private JPanel Console;
    private JTextField console;
    private JTextArea prompt;

    Word currentWord;

    String command;
    Dictionary dictionary;
    boolean autosave = false;
    boolean textErase = false;
    Quiz quiz;

    public GUI() {
        dictionary = new Dictionary("standard");
        deditor.setContentType("text/html");
        eeditor.setContentType("text/html");
        keyword.setContentType("text/html");
        QuizEditor.setContentType("text/html");

        dictionary = dictionary.load("standard.dic");

        if(dictionary == null)
        {
            dictionary = new Dictionary("standard");
        }

        quiz = new Quiz(dictionary);

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
                    deditor.setText("<html></html>");
                    searchWord();
                    dfield.setText("");
                }
                else if(!dfield.getText().contains("/")){
                    System.out.println(dfield.getText());
                    deditor.setText(renderCandidates(dictionary.searchRelativeWords(dfield.getText())));
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
                    efield.setText("");
                    searchWord();
                }else if(!efield.getText().contains("/")){
                    eeditor.setText(renderCandidates(dictionary.searchRelativeWords(efield.getText())));
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
                    searchWord();
                }else if(!kfield.getText().contains("/")){
                    keyword.setText(renderCandidates(dictionary.searchRelativeWords(kfield.getText())));
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
        pfield.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER)
                {
                    command = pfield.getText();
                    pfield.setText("");
                    renderWord(quizTime(command));
                }
            }
        });

        QuizEditor.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if(!quiz.activated)
                {
                    quiz.nextWord();

                    System.out.println("Num Words in Dictionary: "+quiz.dictionary.numWords);
                    if(quiz.currentWord != null) {
                        QuizEditor.setText("<html><center><font size=\"40\"><b>" + quiz.currentWord.getWord() + "</b></font></center></html>");
                        quiz.activated = true;
                    }else{
                        QuizEditor.setText("<html><center><font size=\"40\"><b>" + "NO WORDS TO TEST!" + "</b></font></center></html>");
                    }

                }else if(quiz.currentWord == null)
                {
                    quiz.activated = false;
                }
            }
        });
        console.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(textErase)
                {
                    console.setText("");
                    textErase = false;
                }
                if(e.getKeyChar() == KeyEvent.VK_ENTER)
                {
                    command = console.getText();
                    executeCommand();
                    textErase = true;
                }
            }
        });
    }

    public boolean quizTime(String key)
    {
        return quiz.checkCorrect(key);
    }

    public String renderCandidates(ArrayList<Word> words)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><font size = \"36\">");

        builder.append("<ul>");
        if(words!=null) {
            for (Word define : words) {
                builder.append("<li>");
                builder.append("<font size = \"18\">");
                builder.append(define.getWord());
                builder.append("</font>");
                builder.append("</li>");
            }
        }
        builder.append("</ul>");
        builder.append("</font></html>");
        return builder.toString();
    }

    public void renderWord(boolean correct)
    {
        /*System.out.println("---------------------");
        for(Word word : quiz.dictionary.totalWords)
        {
            System.out.println(word.getWord()+"  seconds left: "+(word.time - System.currentTimeMillis())/1000);
        }*/

        if(correct) {
            long time = System.currentTimeMillis();
            quiz.nextWord();
            if(quiz.currentWord == null)
            {
                QuizEditor.setText("<html><center><font size=\"40\"><b>" + "NO WORDS LEFT TO TEST" + "</b></font></center></html>");
            }else {
                QuizEditor.setText("<html><center><font size=\"40\"><b>" + quiz.currentWord.getWord() + "</b></font></center></html>");
            }
        }else{
            String keys = keywordBuilder(quiz.currentWord);
            QuizEditor.setText(keys);
        }

    }

    public String executeCommand()
    {
            if(command.replaceAll("\\s","").equals("save"))
            {
                dictionary.addWord(currentWord);
                prompt.append("saved\n");
            }else if(command.replaceAll("\\s","").equals("clear"))
            {
                prompt.setText("");
            }
            else if(command.contains("addKeyword"))
            {
                currentWord.addKeyword(command.substring(command.indexOf("\"")+1,command.length()-1));
                prompt.append("added keyword\n");
            }
            else if(command.replaceAll("\\s","").equals("retrieveWordList"))
            {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                int returnval = chooser.showOpenDialog(null);
                if(returnval == JFileChooser.APPROVE_OPTION) {
                    prompt.append("Retrieving file..\n");
                    dictionary.searchWordsInFile(chooser.getSelectedFile().getPath());
                    prompt.append("Complete!\n");
                }
            }else if(command.replaceAll("\\s","").equals("toggleAutoSave"))
            {
                autosave = !autosave;
                prompt.append("autosave is now "+autosave+"\n");

            }else if(command.contains("saveDictionary"))
            {
                if(command.indexOf(" ") >= 0)
                {
                    dictionary.name = command.substring(command.indexOf(" "));
                }
                dictionary.save();
                prompt.append(dictionary.name+" saved! Words: "+dictionary.numWords+"\n");

            }
            else if(command.contains("loadDictionary"))
            {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                int returnval = chooser.showOpenDialog(null);
                if(returnval == JFileChooser.APPROVE_OPTION) {
                    prompt.append("Retrieving file..\n");
                    dictionary = dictionary.load(chooser.getSelectedFile().getPath());
                    quiz.dictionary = dictionary;
                    prompt.append(dictionary.name+" loaded! Words: "+dictionary.numWords+"\n");
                    prompt.append("Complete!\n");
                }

            }else if(command.equals("help"))
            {
                prompt.append("loadDictionary \'dictionaryName\'-> loadsDictionaryFile. Don't need quotations while writing dictionary name\n");
                prompt.append("saveDictionary -> savesDictionaryFile\n");
                prompt.append("toggleAutoSave -> Autosave or not?\n");
                prompt.append("addKeyword -> add keyword to current word you've searched\n");
                prompt.append("retrieveWordList -> scrapes all vocabulary put onto a text file into useful interfaces\n");
                prompt.append("save -> saves current word you've searched (practically useless after you toggle auto save)\n");
            }
            else{
                prompt.append("Command Not Found!\nTry typing help");
            }
            textErase = true;

        return command;
    }

    public void searchWord()
    {
        command = command.replaceAll("\\s|/","");
        currentWord = search(command);
        if(autosave)
        {
            dictionary.addWord(currentWord);
        }
        deditor.setText(definitionBuilder(currentWord));
        eeditor.setText(etymologyBuilder(currentWord));
        keyword.setText(keywordBuilder(currentWord));
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

        if(word.etymology() != null) {
            String[] words = word.etymology().split("\\s");
            boolean quoted = false;
            for (String w : words) {
                if (w.startsWith("\"")) {
                    quoted = true;
                }
                if (w.contains("French") || w.contains("Latin") || w.contains("Greek")) {
                    builder.append("<font size = \"18\" color=\"blue\">");
                    builder.append(w);
                    builder.append(" ");
                    builder.append("</font>");
                } else {
                    if (quoted) {
                        builder.append("<font size = \"18\" color=\"green\">");
                        builder.append(w);
                        builder.append(" ");
                        builder.append("</font>");
                    } else {
                        builder.append("<font size = \"18\">");
                        builder.append(w);
                        builder.append(" ");
                        builder.append("</font>");
                    }
                }

                if (w.endsWith("\"")) {
                    quoted = false;
                }
            }
            builder.append("</html>");
        }
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
