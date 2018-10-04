import javafx.scene.input.KeyCode;
import org.jsoup.Jsoup;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class GUI {
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private JPanel Definition;
    private JPanel Etymology;
    private JPanel Keywords;
    private JPanel Practise;
    private JTextField efield;
    private JTextField kfield;
    public JTextField pfield;
    private JTextField dfield;
    private JEditorPane eeditor;
    private JEditorPane deditor;
    private JEditorPane keyword;
    public JEditorPane QuizEditor;
    private JPanel Console;
    public JProgressBar consoleProgress;
    public JTextArea prompt;

    JFileChooser chooser;

    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenuItem loadDictionary = new JMenuItem("Load Dictionary");
    JMenuItem loadWordList = new JMenuItem("Load Wordlist");
    JMenuItem saveDictionary = new JMenuItem("Save Dictionary");
    JMenuItem toggleAutoSave = new JMenuItem("Toggle Autosave");

    Word currentWord;

    String command;
    Dictionary dictionary;
    boolean autosave = true;
    boolean textErase = false;
    Quiz quiz;



    public GUI() {
        dictionary = new Dictionary("standard", this);
        deditor.setContentType("text/html");
        eeditor.setContentType("text/html");
        keyword.setContentType("text/html");
        QuizEditor.setContentType("text/html");

        if(dictionary == null)
        {
            dictionary = new Dictionary("standard", this);
        }

        dictionary.encoder = new JsonEncoder("standard");

        deditor.setText(renderCandidates(dictionary.searchRelativeWords(dfield.getText())));
        keyword.setText(renderCandidates(dictionary.searchRelativeWords(kfield.getText())));
        eeditor.setText(renderCandidates(dictionary.searchRelativeWords(efield.getText())));

        quiz = new Quiz(dictionary);

        prompt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                manageDocumentflow();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                manageDocumentflow();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                manageDocumentflow();
            }

            public void manageDocumentflow()
            {
                prompt.setCaretPosition(prompt.getDocument().getLength());
            }

        });

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
                    //deditor.setText("<html></html>");
                    searchWord();
                    dfield.setText("");
                }
            }
        });

         loadDictionary.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 JFileChooser chooser = new JFileChooser();
                 chooser.setCurrentDirectory(new File("."));

                 chooser.setFileFilter(new FileFilter() {

                     public String getDescription() {
                         return "DIC Images (*.dic)";
                     }

                     public boolean accept(File f) {
                         if (f.isDirectory()) {
                             return true;
                         } else {
                             String filename = f.getName().toLowerCase();
                             return filename.endsWith(".dic");
                         }
                     }
                 });

                 int returnval = chooser.showOpenDialog(null);

                 if(returnval == JFileChooser.APPROVE_OPTION) {
                     prompt.append("Retrieving file..\n");
                     dictionary = dictionary.load(chooser.getSelectedFile().getPath());
                     quiz.dictionary = dictionary;
                     prompt.append(dictionary.name+" loaded! Words: "+dictionary.numWords+"\n");
                     prompt.append("Complete!\n");
                 }
             }
         });

         loadWordList.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 chooser = new JFileChooser();
                 chooser.setCurrentDirectory(new File("."));

                 chooser.setFileFilter(new FileFilter() {

                     public String getDescription() {
                         return "TXT Files (*.txt)";
                     }

                     public boolean accept(File f) {
                         if (f.isDirectory()) {
                             return true;
                         } else {
                             String filename = f.getName().toLowerCase();
                             return filename.endsWith(".txt");
                         }
                     }
                 });
                 int returnval = chooser.showOpenDialog(null);

                 if(returnval == JFileChooser.APPROVE_OPTION) {
                     prompt.append("Retrieving file..\n");

                     Thread thread = new Thread(new Runnable() {
                         @Override
                         public void run() {
                             dictionary.searchWordsInFile(chooser.getSelectedFile().getPath());
                             dictionary.searchAllWords(dictionary.start, dictionary.totalWords);
                             prompt.append("Complete!\n");
                         }
                     });

                     thread.start();
                 }
             }
         });

         saveDictionary.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 JFileChooser chooser = new JFileChooser();
                 chooser.setCurrentDirectory(new File("."));
                 int returnval = chooser.showSaveDialog(null);

                 if(returnval == JFileChooser.APPROVE_OPTION) {

                     dictionary.name = chooser.getSelectedFile().getName();
                     System.out.println(chooser.getCurrentDirectory().toString());
                     String dir = chooser.getCurrentDirectory().toString();
                     dictionary.save(dir.substring(0,dir.lastIndexOf("."))+dictionary.name+".dic");
                 }
             }
         });

        efield.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshInspector();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshInspector();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshInspector();
            }

            public void refreshInspector()
            {
                String string = efield.getText().replaceAll("\\s|\\|/|\'|\"", "");
                if((string != null) && !string.equals("")) {
                    eeditor.setText(renderCandidates(dictionary.searchRelativeWords(string)));
                }
            }
        });

        kfield.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshInspector();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshInspector();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshInspector();
            }

            public void refreshInspector()
            {
                String string = kfield.getText().replaceAll("\\s|\\|/|\'|\"", "");
                if((string != null) && !string.equals("")) {
                    keyword.setText(renderCandidates(dictionary.searchRelativeWords(string)));
                }
            }
        });

        dfield.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshInspector();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshInspector();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshInspector();
            }

            public void refreshInspector()
            {
                String string = dfield.getText().replaceAll("\\s|\\|/|\'|\"", "");
                if((string != null) && !string.equals("")) {
                    deditor.setText(renderCandidates(dictionary.searchRelativeWords(string)));
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
                    quiz.submitAnswer(command);
                }
            }
        });

        Thread quizThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int oldCase = -1;
                while(true)
                {
                    switch(quiz.state)
                    {
                        case 0:
                            if(oldCase != 0) {
                                oldCase = 0;
                                QuizEditor.setText("<html><center><font size=\"40\"><b>" + "NO WORDS TO TEST!</b></font></center></html>");
                            }
                            break;
                        case 1:
                            if(oldCase != 1) {
                                oldCase = 1;
                            }
                            break;
                        case 2:
                            if(oldCase != 2) {
                                oldCase = 2;
                                QuizEditor.setText("<html><center><font size=\"40\"><b>" + "Take a break. ETA " + quiz.maxTimetoWait / 1000 + "seconds</b></font></center></html>");
                            }
                            break;
                        case 3:
                            if(oldCase != 3) {
                                oldCase = 3;
                                QuizEditor.setText("<html><center><font size=\"40\"><b>" + "Words Mastered!" + "</b></font></center></html>");
                            }
                            break;
                        case 4:
                            if(oldCase != 4) {
                                oldCase = 4;
                                QuizEditor.setText("<html><center><font size=\"40\"><b>" + quiz.currentWord.getWord() + "</b></font></center></html>");
                            }
                            break;
                        case 5:
                            if(oldCase != 5)
                            {
                                oldCase = 5;
                                String keys = keywordBuilder(quiz.currentWord);
                                QuizEditor.setText(keys);
                            }
                            break;
                    }
                }
            }
        });
        quizThread.start();

        QuizEditor.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.isPopupTrigger())
                {
                    generatePopMenu(e);
                }
            }

        });
    }

    public void generatePopMenu(MouseEvent e)
    {
        JPopupMenu pop = new JPopupMenu();
        JMenuItem item = new JMenuItem("addKeyword");
        JMenuItem override = new JMenuItem("Override");
        JMenuItem quizset = new JMenuItem("setPracticeAmount");

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quiz.addKeyword();
            }
        });
        override.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quiz.override();
            }
        });
        quizset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quiz.setPracticeAmount();
            }
        });
        if(quiz.state > 3)
        {
            pop.add(item);
        }

        if(quiz.state == 5)
        {
            pop.add(override);
        }
        pop.add(quizset);
        pop.show(e.getComponent(), e.getX(), e.getY());
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
        if(words != null || words.length <= 0)
        {
            System.out.println("Triggered");
            word = words[0];
        }else{
            /*Thread t = new DataScraper(w,dictionary,false);
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
            DataScraper.threads.remove(t);*/
        }

        return word;
    }

    public static void main(String[] args) {
        GUI gui = new GUI();

        JFrame frame = new JFrame("VocabAssistant");
        frame.setContentPane(gui.mainPanel);

        gui.menuBar.add(gui.fileMenu);
        gui.fileMenu.add(gui.loadDictionary);
        gui.fileMenu.add(gui.loadWordList);
        gui.fileMenu.add(gui.saveDictionary);
        gui.fileMenu.add(gui.toggleAutoSave);
        frame.setJMenuBar(gui.menuBar);
        frame.setPreferredSize(new Dimension(1000,600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
