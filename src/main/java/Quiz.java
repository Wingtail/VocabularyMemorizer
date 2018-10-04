import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static jdk.nashorn.internal.objects.NativeMath.max;

public class Quiz {

    Dictionary dictionary;
    Word currentWord = null;

    boolean correct = false;

    short state = 0; //0 is inactive, 1 is active, 2 is all words need to wait, 3 is section complete, 4 is wait, 5 is incorrect

    String lastInput;

    double maxTimetoWait = 0.0;

    ArrayList<Word> dictionaryWords;
    ArrayList<Word> activeWords;

    public Quiz(Dictionary dictionary)
    {
        this.dictionary = dictionary;
        activeWords = new ArrayList<>();
        dictionaryWords = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    checkState();
                }
            }
        });
        thread.start();
        //dictionaryWords.addAll(dictionary.totalWords);
    }

    public void selectiveLearn(ArrayList<Word> wordlist)
    {
        state = 1;
        dictionaryWords = wordlist;
    }

    public void consecRightEval(boolean correct)
    {
        if(currentWord.dataPoints > 50)
        {
            currentWord.totConsecRight = (int)Math.round(currentWord.avgConsecRight);
            currentWord.dataPoints = 1;
        }
        if(correct)
        {
            currentWord.consecRight++;
            currentWord.consecWrong = 0;
            currentWord.avgConsecRight = (double)(currentWord.totConsecRight+currentWord.consecRight)/(currentWord.dataPoints+1);
            currentWord.time = System.currentTimeMillis()+retentionCurve(currentWord.avgConsecRight);
        }else{
            currentWord.consecWrong++;
            currentWord.totConsecRight += currentWord.consecRight - currentWord.consecWrong;
            currentWord.dataPoints++;
            currentWord.consecRight = 0;
            currentWord.avgConsecRight = (double)currentWord.totConsecRight/currentWord.dataPoints;
            currentWord.time = System.currentTimeMillis()+retentionCurve(currentWord.avgConsecRight);
        }

    }

    public boolean checkCorrect(String key)
    {
        lastInput = key;
        if(currentWord.keywords().length <=0)
        {
            System.out.println("No keywords!");
            return true;
        }

        for(String keyword:currentWord.keywords())
        {
            if(keyword.equals(key))
            {
                consecRightEval(true);
                return true;
            }
        }
        consecRightEval(false);
        return false;
    }


    public double retentionCurve(double avgConsecRight)
    {
        double a = -8923.43;
        double b = 3241.47;
        double c = 210.349;
        double d = -2926.55;
        double f = -1.65282;

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
            frame.setContentPane(new AddKeyWord(this, frame).main);
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

    public void override()
    {
        if(state == 5 && !correct) {
            consecRightEval(true);
            correct = true;
            state = 1;
        }
    }

    public void submitAnswer(String key)
    {
        if(checkCorrect(key))
        {
            consecRightEval(true);
            correct = true;
            state = 1;
        }else if(!correct)
        {
            state = 5;
            consecRightEval(false);
        }
    }

    public void checkState()
    {
        switch(state)
        {
            case 0:
                if(dictionaryWords.size() > 0)
                {
                    state = 1;
                }
                break;
            case 1:
                nextWord();
                break;
            case 2:
                getMaxTime();
                if(maxTimetoWait < 0)
                {
                    state = 1;
                }
                break;
            case 3:
                if(dictionaryWords.size() > 0)
                {
                    state = 1;
                }
                break;

        }
    }

    public void makeQuizSet(int numWords)
    {
        ArrayList<Word> wordset = new ArrayList<>();
        int i;
        int count = 0;
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

    public void nextWord()
    {
        currentWord = null;
        if(activeWords.size() > 0) {
            while(activeWords.get(activeWords.size()-1).avgConsecRight >= 6)
            {
                activeWords.remove(activeWords.size()-1).mastered = true;
            }
            quicksort(0,activeWords.size()-1,activeWords);

            if(activeWords.get(0).time - System.currentTimeMillis() > 0)
            {
                currentWord = null;
            }else{
                currentWord = activeWords.get(0);
            }
        }

        while(currentWord == null && dictionaryWords.size()>0)
        {
            currentWord = dictionaryWords.remove(dictionaryWords.size()-1);

                activeWords.add(currentWord);
                while(dictionaryWords.contains(currentWord)) {
                    dictionaryWords.remove(currentWord);
                }


            if((currentWord.keywords() == null))
            {
                currentWord = null;
            }
        }
        if(activeWords.size() <= 0 && dictionaryWords.size() <= 0){
            state = 3;
        }else if(currentWord == null){
            getMaxTime();
            state = 2;
        }else{
            state = 4;
        }
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
