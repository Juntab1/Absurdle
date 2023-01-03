// Program: Absurdle
// Absurdle allows users to play a game of absurdle: 
// an adversarial game of wordle where the manager attempts to make the game 
// as hard as possible by revealing as little information on each round as 
// possible, changing the secret word in response to guesses as necessary. 

// ! = correct
// * = letter is in word
// % = incorrect

import java.util.*;
import java.io.*;

public class Absurdle  {
    public static final boolean DEBUG = false;

    // [[ ALL OF MAIN PROVIDED ]]
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Absurdle.");

        System.out.print("What dictionary would you like to use? ");
        String dictName = console.next();

        System.out.print("What length word would you like to guess? ");
        int wordLength = console.nextInt();

        List<String> contents = loadFile(new Scanner(new File(dictName)));
        Set<String> words = pruneDictionary(contents, wordLength);

        if (DEBUG) 
            System.out.println("words: " + words);

        List<String> guessedPatterns = new ArrayList<>();
        while (!isFinished(guessedPatterns)) {
            System.out.print("> ");
            String guess = console.next();
            String pattern = record(guess, words, wordLength);
            guessedPatterns.add(pattern);
            System.out.println(": " + pattern);
            System.out.println();
        }
        System.out.println("Absurdle " + guessedPatterns.size() + "/∞");
        System.out.println();
        printPatterns(guessedPatterns);
    }

    // [[ PROVIDED ]]
    public static void printPatterns(List<String> list) {
        for (String s : list) {
            System.out.println(s);
        }
    }

    // [[ PROVIDED ]]
    public static boolean isFinished(List<String> patterns) {
        if (patterns.isEmpty()) { // haven't guessed anything yet, so game isn't finished!
            return false;
        }
        String lastPattern = patterns.get(patterns.size() - 1); // get the pattern generated by the most recent guess
        return !lastPattern.contains("%") && !lastPattern.contains("*"); // must be all green to "win"
    }

    // [[ PROVIDED ]]
    public static List<String> loadFile(Scanner dictScan) {
        List<String> contents = new ArrayList<>();
        while (dictScan.hasNext()) {
            contents.add(dictScan.next());
        }

        return contents;
    }

    // This method checks through the list of strings we already have and puts
    // it against the wordLength the user chooses to only get the words from
    // the contents list that is the same length as the wordLength.
    // The words that match the length of the wordLength are put
    // into a set, we created in the method, which is returned.
    // parameter:
    //      - contents: list of words provided
    //      - wordLength: users input of how many letters they would like in the word
    // IllegalArgumentException for when the word length the user chooses is less than 1
    public static Set<String> pruneDictionary(List<String> contents, int wordLength) {
        if (wordLength < 1){
            throw new IllegalArgumentException();
        }
        Set<String> chosenUser = new HashSet<>();
        for (String word : contents){
            if (word.length() == wordLength){
                    chosenUser.add(word);
            }
        }
        return chosenUser;
    }

    // This method will figure out what the next set of words should be for 
    // when the user guesses a word.
    // The given set of words will be updated everytime after
    // the user makes their guess.
    // The method ends up returning the emoji sequence with the most words that 
    // fit that criteria of the emoji.
    // parameter:
    //      - guess: the user's guess
    //      - words: set of words from after the method of pruneDictionary is executed
    //      - wordLength: user's input of how many letters they would like in the word
    // IllegalArgumentException for when the set of words is empty or the guess word length
    // does not equal the desired wordLength the user has declared
    public static String record(String guess, Set<String> words, int wordLength) {
        if (words.isEmpty() || guess.length() != wordLength){
            throw new IllegalArgumentException();
        }
        Map<String, Set<String>> result = new TreeMap<>();
        for (String word : words){
            String guessEmoji = patternFor(word, guess);
            if (!result.containsKey(guessEmoji)){
                result.put(guessEmoji, new TreeSet<>());
            }
            result.get(guessEmoji).add(word);
        }
        String keyForReplaceSet = guessAnswerForUser(result);
        words.clear();
        Set<String> wantValues = result.get(keyForReplaceSet);
        for (String value : wantValues){
            words.add(value);
        }
        return keyForReplaceSet;
    }

    // This is a helper method that scans through the map created in the record method
    // and returns the emoji sequence with the most words attached to it. 
    // parameter:
    //      - result: map that has a key of emojis and words, from the words set, as the values.
    //          A key can hold several words because the values are classified as a set.     
    public static String guessAnswerForUser(Map<String, Set<String>> result){
        String keyForReplaceSet = " ";
        int KeyForReplaceNum = 0;
        for (String key : result.keySet()){
            if ((result.get(key)).size() > KeyForReplaceNum){
                keyForReplaceSet = key;
                KeyForReplaceNum = (result.get(key)).size();
            }
        }
        return keyForReplaceSet;
    }

    // Assumes that the given word is the same length as the given guess. 
    // Returns a wordle pattern of blocks for the given guess, compared to the 
    // given word. 
    public static String patternFor(String word, String guess) {
        for (int charLength = 0; charLength < guess.length(); charLength++){
            char wordChar = word.charAt(charLength);
            char guessChar = guess.charAt(charLength);
            if (wordChar == guessChar){
                word = word.substring(0, charLength) + "!" 
                    + word.substring(charLength+1);
                guess = guess.substring(0, charLength) + "!" 
                    + guess.substring(charLength+1);
            }
        }
        for (int charLength = 0; charLength < guess.length(); charLength++){
            char guessChar = guess.charAt(charLength);
            String guessCharAsStr = guess.valueOf(guessChar);
            if (word.contains(guessCharAsStr) && guessChar != '!'){
                word = word.replaceFirst(guessCharAsStr, "*");
                guess = guess.replaceFirst(guessCharAsStr, "*");
            }
        }
        for (int charLength = 0; charLength < guess.length(); charLength++){
            char guessChar = guess.charAt(charLength);
            String guessCharAsStr = guess.valueOf(guessChar);
            if (Character.isLetter(guessChar)){
                guess = guess.replace(guessCharAsStr, "%");
            }
        }
        return guess;
    }
}