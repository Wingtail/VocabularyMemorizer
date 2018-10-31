import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static jdk.nashorn.internal.objects.NativeMath.max;
import static jdk.nashorn.internal.runtime.ScriptObject.spillAllocationLength;

public class Quiz {

    Dictionary dictionary;
    Word currentWord = null;

    boolean correct = false;
    char quizType = 0; // 1 is flashcard, 2 is multiplechoice
    boolean state = false; // true is active false is in-transition

    String lastInput;

    double maxTimetoWait = 0.0;
    private double avgTimePerQuestion = 0.0;
    private long startTime;

    private int totalQuestions = 0;
    private Answer[] answer;
    private String[] choices;

    private int numWords = 0;

    GUI gui;

    ArrayList<Word> dictionaryWords;
    ArrayList<Word> activeWords;

    public Quiz(Dictionary dictionary, GUI g)
    {
        this.dictionary = dictionary;
        activeWords = new ArrayList<>();
        dictionaryWords = new ArrayList<>();
        gui = g;
        //dictionaryWords.addAll(dictionary.totalWords);
    }

    public void selectiveLearn(ArrayList<Word> wordlist)
    {
        dictionaryWords = wordlist;
    }

    public void scoreEval(boolean correct)
    {
        currentWord.evaluateScore(correct);
        if(currentWord.avgConsecRight < 0)
        {
            currentWord.avgConsecRight = 1;
        }
        double retention = retentionCurve(currentWord.avgConsecRight);
        if(retention < 0)
        {
            retention = 0;
        }
        currentWord.time = System.currentTimeMillis()+retention;
        currentWord.evaluateLevel();
    }

    public boolean checkCorrect(String key)
    {
        lastInput = key;


        if(answer.length <=0)
        {
            System.out.println("No keywords!");
            return true;
        }

        for(Answer keyword:answer)
        {
            if(keyword.answer.equals(key))
            {
                scoreEval(true);
                return true;
            }
        }
        scoreEval(false);
        return false;
    }


    public double retentionCurve(double avgConsecRight)
    {
        double a = -8923.43;
        double b = 3241.47;
        double c = 210.349;
        double d = -2926.55;
        double f = -1.65282;

        //return Math.exp(Math.log10(avgConsecRight*numWords))*10;

        return max((a*Math.log10(b*Math.pow(avgConsecRight+c,f))+d)*1000,0.0);
    }

    public double max(double a, double b)
    {
        return (a>b)?a:b;
    }

    public void addKeyword()
    {
        if(currentWord != null) {
            JFrame frame = new JFrame();
            frame.setContentPane(new AddKeyWord(currentWord, frame).main);
            frame.setPreferredSize(new Dimension(200, 100));
            frame.pack();
            frame.setVisible(true);
        }
    }

    public void setPracticeAmount()
    {
            JFrame frame = new JFrame();
            frame.setContentPane(new setQuizAmount(this, frame).main);
            frame.setPreferredSize(new Dimension(200,100));
            frame.pack();
            frame.setVisible(true);
    }

    public void selectWords()
    {
        JFrame frame = new JFrame();
        frame.setContentPane(new selectWord(this, frame, dictionary).panel);
        frame.setPreferredSize(new Dimension(400,600));
        frame.pack();
        frame.setVisible(true);
    }

    public void override()
    {
        if(state && !correct) {
            scoreEval(true);
            correct = true;
        }
    }

    public void submitAnswer(String key)
    {
        if(state) {
            if (checkCorrect(key) && quizType != 1) {
                correct = true;
                sayCorrect();
            } else if(quizType != 1){
                System.out.println("INCORRECT!");
                showAnswer();
            }else{
                correct = true;
                nextWord();
            }
        }else{
            state = true;
            nextWord();
        }
    }

    public void sayCorrect()
    {
        gui.QuizEditor.setText(gui.sayCorrect());
        state = false;
    }

    public void showAnswer()
    {
        gui.QuizEditor.setText(gui.answerBuilder(answer[0], currentWord));
    }

    public void makeQuizSet(int numWords)
    {
        ArrayList<Word> wordset = new ArrayList<>();
        activeWords = new ArrayList<>();
        int i;
        int count = 0;
        this.numWords = numWords;
        for(i=0;i<numWords;i++)
        {
            if(!dictionary.totalWords.get(count).mastered)
            {
                wordset.add(dictionary.totalWords.get(count));
            }else{
                i--;
            }

            if(count >= dictionary.totalWords.size())
            {
                break;
            }
            count++;
        }
        dictionaryWords = wordset;
    }

    public void makeQuizSet(ArrayList<Word> numWords)
    {
        this.numWords = numWords.size();
        for(Word word : numWords)
        {
            dictionaryWords.add(word);
        }
    }

    public void formatMastered()
    {
        for(Word word:dictionary.totalWords)
        {
            word.mastered = false;
        }
    }

    public void slotWord(Word word)
    {
        double time = (word.time - System.currentTimeMillis());
        int slot = (int) ((time / (avgTimePerQuestion / 1000)));
        if(activeWords.size()>0) {
            if (slot > activeWords.size()) {
                slot = activeWords.size() - 1;
            }else if(slot < 0)
            {
                slot = 0;
            }
            if (!word.mastered) {
                //activeWords.remove(word);
                activeWords.add(slot, word);
            }
        }else{
            activeWords.add(word);
        }
        System.out.println("---------- "+slot);
        for(Word w:activeWords)
        {
            System.out.println(w.toString()+"  "+w.avgConsecRight);
        }
    }

    public void nextWord()
    {
        if(currentWord != null) {
            avgTimePerQuestion*=totalQuestions;
            totalQuestions++;
            avgTimePerQuestion+=(System.currentTimeMillis() - startTime);
            avgTimePerQuestion = avgTimePerQuestion/totalQuestions;
            if (activeWords.size() > 0) {
                if (currentWord.level > 2) {
                    currentWord.mastered = true;
                }
            }
            slotWord(currentWord);
        }
        currentWord = null;
        if(activeWords.size() > 0)
        {
            if(activeWords.get(0).time - System.currentTimeMillis() > 0 && dictionaryWords.size() > 0)
            {
                currentWord = null;
            }else{
                currentWord = activeWords.remove(0);
            }
        }

        while(currentWord == null && dictionaryWords.size()>0)
        {
            currentWord = dictionaryWords.remove(dictionaryWords.size()-1);
            //activeWords.add(currentWord);
            while(dictionaryWords.contains(currentWord)) {
                dictionaryWords.remove(currentWord);
            }

            if((currentWord.keywords() == null))
            {
                currentWord = null;
            }
        }
        currentWord.evaluateLevel();
        startTime = System.currentTimeMillis();
        selectQuestion();
    }

    public void selectQuestion()
    {
        switch (currentWord.level) {
            case 1:
                flashcard();
                quizType = 1;
                break;
            case 2:
                int balance = currentWord.definitionChoice - currentWord.wordChoice;
                Random rand = new Random();
                if(balance > 0) {
                    multiplechoiceWords();
                }else if(balance < 0){
                    multiplechoice();
                }else if(rand.nextBoolean()){
                    multiplechoice();
                }else{
                    multiplechoiceWords();
                }
                quizType = 2;
                break;
            case 3:
                //dummy
                break;
            case 4:
                //dummy
                break;
        }
    }


    public void flashcard()
    {
        answer = new Answer[1];
        answer[0] = new Answer("","");
        gui.QuizEditor.setText(gui.definitionBuilder(currentWord));
    }

    public void multiplechoiceWords()
    {
        answer = new Answer[1];
        choices = new String[4];
        int randIndex = (int)((Math.random()*4)+1);
        answer[0] = new Answer(Integer.toString(randIndex),currentWord.toString());

        ArrayList<Word> wordcandidates = new ArrayList<>();
        wordcandidates.addAll(activeWords);
        wordcandidates.addAll(dictionaryWords);
        wordcandidates.remove(currentWord);

        if(wordcandidates.size() < 4)
        {
            wordcandidates.addAll(dictionary.totalWords);
        }

        //System.out.println(randIndex+" , "+currentWord.getDefinition(0));
        choices[randIndex-1] = currentWord.toString();
        for(int i = 0; i<4;i++)
        {
            if(i != randIndex - 1)
            {
                if(wordcandidates.size()>0) {
                    Word word = wordcandidates.remove((int) (Math.random() * wordcandidates.size()));
                    //System.out.println(word.toString());
                    choices[i] = word.getWord();
                }
            }
        }
        gui.QuizEditor.setText(gui.choiceBuilder(choices, currentWord.getDefinition(0)));
    }

    public void multiplechoice()
    {
        answer = new Answer[1];
        choices = new String[4];
        int randIndex = (int)((Math.random()*4)+1);
        answer[0] = new Answer(Integer.toString(randIndex),currentWord.getDefinition(0));

        ArrayList<Word> wordcandidates = new ArrayList<>();
        wordcandidates.addAll(activeWords);
        wordcandidates.addAll(dictionaryWords);
        wordcandidates.remove(currentWord);

        if(wordcandidates.size() < 4)
        {
            wordcandidates.addAll(dictionary.totalWords);
        }

        //System.out.println(randIndex+" , "+currentWord.getDefinition(0));
        choices[randIndex-1] = currentWord.getDefinition(0);
        for(int i = 0; i<4;i++)
        {
            if(i != randIndex - 1)
            {
                if(wordcandidates.size()>0) {
                    Word word = wordcandidates.remove((int) (Math.random() * wordcandidates.size()));
                    //System.out.println(word.toString());
                    choices[i] = word.getDefinition(0);
                }
            }
        }
        gui.QuizEditor.setText(gui.choiceBuilder(choices, currentWord.toString()));
    }

    public void getMaxTime()
    {
        double minTime = Double.MAX_VALUE;
        for(Word word : activeWords)
        {
            double timeWait = word.time - System.currentTimeMillis();
            if(timeWait < minTime)
            {
                minTime = timeWait;
            }
        }

        maxTimetoWait = minTime;
    }

    public int partition(int l, int r, ArrayList<Word> arr)
    {
        int left = l;
        int right = r;
        double pivot = arr.get((l+r)/2).time-System.currentTimeMillis();
        while(left<=right)
        {
            while((left <= right)&&arr.get(left).time-System.currentTimeMillis() < pivot)
            {
                left++;
            }
            while((left <= right)&&arr.get(right).time - System.currentTimeMillis() > pivot)
            {
                right--;
            }
            if(left<=right)
            {
                swap(left, right, arr);
                left++;
                right--;
            }
        }
        return left;
    }

    public void quicksort(int l, int r, ArrayList<Word> arr)
    {
        int index = partition(l, r, arr);
        if(l<index-1) {
            quicksort(l,index-1,arr);
        }

        if(index<r)
        {
            quicksort(index,r,arr);
        }
    }

    public void swap(int a, int b, ArrayList<Word> arr)
    {
        Word temp = arr.get(a);
        arr.set(a,arr.get(b));
        arr.set(b,temp);
    }

}

class Answer{
    String answer;
    String description;

    Answer(String a, String d)
    {
        answer = a;
        description = d;
    }
}
