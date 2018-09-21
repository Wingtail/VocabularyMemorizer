

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class DataScraper extends Thread{


    public static void setSSL() {

        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultHostnameVerifier(
                    new HostnameVerifier() {
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    }
            );
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }catch(KeyManagementException e)
        {
            e.printStackTrace();
        }
    }


    //String word;
    Word w;
    static List<Thread> threads = new ArrayList<Thread>();
    Dictionary dictionary;
    BufferedWriter out;
    boolean save = true;

    public DataScraper(String word, Dictionary dictionary, boolean autosave)
    {
        //this.word = word;
        setSSL();
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        w = new Word(word.split("\\.|,|;|\\s")[0]);
        save = autosave;
        this.dictionary = dictionary;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new
                    FileOutputStream(java.io.FileDescriptor.out), "ASCII"), 512);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        dictionary(w);
        thesaurus(w);
        etymology(w);
        if(save) {
            dictionary.addWord(w);
            dictionary.totalWords.add(w);
        }
    }

    public void thesaurus(Word word) {
        int count = 0;

        while(count <= 20) {
            try {
                Document doc;
                doc = Jsoup.connect("http://www.synonymy.com/synonym.php?word=" + word.toString()).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .referrer("http://www.google.com").timeout(1000 * 10000).ignoreHttpErrors(true).validateTLSCertificates(false).get();
                if (!doc.toString().contains("no thesaurus results")) {
                    Elements keywords = doc.select("td.arial-12-noir").clone().select("span").clone();
                    if (keywords.size() > 0) {
                    /*for(Element element : keywords) {
                        keywords = element.select("strong").clone();
                    }*/
                        for (Element element : keywords) {
                            String[] keys = element.text().split(";\\s|,\\s|:\\s");
                            for (String string : keys) {
                                if (!(string.equals("adjective") || string.equals("noun") || string.equals("verb"))) {
                                    word.addKeyword(string);
                                }
                            }
                        }
                    }
                } else {
                    //System.out.println("Error! No Keywords available!    :   Word: "+word.toString());
                }

                break;

            } catch(SocketException e)
            {
                count++;
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("Exception word: " + word.toString());
                break;
            }
        }
    }

    public void etymology(Word word)
    {
        int count = 0;

        while(count <= 20) {
            try {
                Document doc = Jsoup.connect("https://www.etymonline.com/search?q=" + word.toString()).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .referrer("http://www.google.com").timeout(1000 * 80).ignoreHttpErrors(true).validateTLSCertificates(false).get();
                Elements words = doc.select("a.word--C9UPa.word_thumbnail--2DBNk");
                for (Element element : words) {
                    String str = element.select("p.notranslate.word__name--TTbAA.word_thumbnail__name--1khEg").text().split("\\s")[0];
                    if (str.equals(word.toString())) {
                        int c = 0;
                        while(c<=20) {
                            try {
                                doc = Jsoup.connect(element.attr("abs:href")).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                        .referrer("http://www.google.com").timeout(1000 * 80).ignoreHttpErrors(true).validateTLSCertificates(false).get();
                                break;
                            } catch (SocketException e) {
                                c++;
                            }
                        }
                        //System.out.println("READ "+word.toString());
                        break;
                    }
                }
                String etymology = doc.select("section.word__defination--2q7ZH").text();
                word.setEtymology(etymology);
                break;
            }catch(SocketException e)
            {
                count++;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean containsWithinSentence()
    {
        return true;
    }


    public void dictionary(Word word)
    {
        String[] dictionary;
        String[] sentences;

        String actualWord;
        String pronounciation = "";

        try{
            Document doc = Jsoup.connect("https://www.dictionary.com/browse/"+word.toString()+"?s=t").userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com").timeout(1000*10000).ignoreHttpErrors(true).get();
            actualWord = word.toString();
            if(doc.select("span.css-1m7ldyv.e6aw9qa0").clone().size() > 0)
            {
                String switchWord = doc.selectFirst("h2.css-6gthty.e19m0k9k0").select("a").first().attr("abs:href");
                doc = Jsoup.connect(switchWord).get();
            }
            actualWord = doc.select("section.css-0.e1rg2mtf0").select("h1.css-1qbmdpe.e1rg2mtf5").text().split("\\s|,|;")[0];

            if(doc.select("span.css-1khtv86.e1rg2mtf2").first() != null) {
                pronounciation = doc.select("span.css-1khtv86.e1rg2mtf2").first().text();
            }

            Elements masthead = doc.select("section.css-1748arg.e1wu7xq20").clone();
            List<String> strings = masthead.select("li.css-2oywg7.e10vl5dg5").eachText();
            dictionary = new String[strings.size()];
            sentences = new String[strings.size()];
            for(int i = 0;i<strings.size();i++)
            {
                String string = strings.get(i);
                if(string.contains(":")) {
                    dictionary[i] = string.substring(0, string.indexOf(":"));
                    sentences[i] = string.substring(string.indexOf(":") + 1);
                }else{
                    dictionary[i] = string;
                }
            }

            word.setWord(actualWord);
            word.pronounciation(pronounciation);
            word.setDefinitions(dictionary);
            word.setExampleSentences(sentences);

            try{
                out.write(actualWord+"\n");
                out.write(pronounciation+"\n");
                out.flush();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
