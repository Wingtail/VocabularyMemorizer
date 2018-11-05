# Vocabulary Memorizer

Vocabulary Memorizer helps you to effectively memorize vocabulary words. It automatically generates an offline dictionary of your word list that presents each word's definition, etymology, and synonyms. It also provides you quiz activities that actively help you memorize your vocabulary.

* The current VocabularyMemorizer is an alpha version. Please report any bugs through the Issues tab.

### Prerequisites

1. Java version greater than 1.7

### Installation

Vocabulary Memorizer is currently available for only Mac OS.

1. Download the Vocabulary Memorizer: https://sourceforge.net/projects/vocabularymemorizer/files/VocabularyMemorizer.zip/download
2. Unzip VocabularyMemorizer.zip
3. Drag the VocabularyMemorizer app into the Applications folder

### Using VocabularyMemorizer

* Make sure you are connected to the internet. VocabularyMemorizer uses web-scraping features to retrieve the definitions, synonyms, and etymology for each word. 

#### Loading your wordlist into VocabularyMemorizer

1. Make your vocabulary list in a txt file and save it.
    Each word in your list should occupy one line in the .txt file.
2. Click File>Load Wordlist and select the .txt file that has your vocabulary list
3. Refer to the Console tab to check the app's progress in retrieving all of the wordlists.

* You do not have to necessarily go through this process to search individual words. You can search a word into the textfield in any of the Definition, Etymology, and Synonyms tab. VocabularyMemorizer will automatically search your word on the internet and add it into your temporary dictionary. At first, it will take some time to scrape the vocabulary you searched. However, there will be no delay for the second time you search that word since it is saved into the temporary dictionary.

#### Saving/Loading Dictionary

* Vocabulary Memorizer saves and loads dictionary files in .dic format.

To save the dictionary that VocabularyMemorizer temporarily created for you, click File>Save Dictionary.
To load the dictionary that you have already saved, click File>Load Dictionary and search your .dic file.

#### Using Practice(Quiz) tab

Right click to see all the options that the Practice environment presents to you.
* Select setPracticeAmount and type the number of words you want to memorize. VocabularyMaker will randomly select words that you don't know into the practice session.
* Select selectWordstoPractice to select specific words that you want to practice.
* Select setMasteredWordstoNeedPractice to make all the words you have previously mastered into the practice list.
