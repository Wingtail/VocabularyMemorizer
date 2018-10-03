import java.util.ArrayList;

import static jdk.nashorn.internal.objects.NativeMath.max;

public class Quiz {


    Dictionary dictionary;
    boolean activated;
    Word currentWord = null;
    boolean sectionComplete = false;

    double maxTimetoWait = 0.0;

    ArrayList<Word> dictionaryWords;
    ArrayList<Word> activeWords;

    public Quiz(Dictionary dictionary)
    {
        this.dictionary = dictionary;
        activated = false;
        activeWords = new ArrayList<>();
        dictionaryWords = new ArrayList<>();
        //dictionaryWords.addAll(dictionary.totalWords);
    }

    public void selectiveLearn(ArrayList<Word> wordlist)
    {
        sectionComplete = false;
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
            sectionComplete = true;
        }else if(currentWord == null){
            getMaxTime();
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
