import java.util.Scanner;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

/**
 * Class that contains helper methods for the Review Lab
 **/
public class Review {
  
  private static HashMap<String, Double> sentiment = new HashMap<String, Double>();
  private static ArrayList<String> posAdjectives = new ArrayList<String>();
  private static ArrayList<String> negAdjectives = new ArrayList<String>();
 
  
  private static final String SPACE = " ";
  
  static{
    try {
      Scanner input = new Scanner(new File("cleanSentiment.csv"));
      while(input.hasNextLine()){
        String[] temp = input.nextLine().split(",");
        sentiment.put(temp[0],Double.parseDouble(temp[1]));
        //System.out.println("added "+ temp[0]+", "+temp[1]);
      }
      input.close();
    }
    catch(Exception e){
      System.out.println("Error reading or parsing cleanSentiment.csv");
    }
  
  
  //read in the positive adjectives in postiveAdjectives.txt
     try {
      Scanner input = new Scanner(new File("positiveAdjectives.txt"));
      while(input.hasNextLine()){
        String temp = input.nextLine().trim();
        System.out.println(temp);
        posAdjectives.add(temp);
      }
      input.close();
    }
    catch(Exception e){
      System.out.println("Error reading or parsing postitiveAdjectives.txt\n" + e);
    }   
 
  //read in the negative adjectives in negativeAdjectives.txt
     try {
      Scanner input = new Scanner(new File("negativeAdjectives.txt"));
      while(input.hasNextLine()){
        negAdjectives.add(input.nextLine().trim());
      }
      input.close();
    }
    catch(Exception e){
      System.out.println("Error reading or parsing negativeAdjectives.txt");
    }   
  }
  
  /** 
   * returns a string containing all of the text in fileName (including punctuation), 
   * with words separated by a single space 
   */
  public static String textToString( String fileName )
  {  
    String temp = "";
    try {
      Scanner input = new Scanner(new File(fileName));
      
      //add 'words' in the file to the string, separated by a single space
      while(input.hasNext()){
        temp = temp + input.next() + " ";
      }
      input.close();
      
    }
    catch(Exception e){
      System.out.println("Unable to locate " + fileName);
    }
    //make sure to remove any additional space that may have been added at the end of the string.
    return temp.trim();
  }
  
  /**
   * @returns the sentiment value of word as a number between -1 (very negative) to 1 (very positive sentiment) 
   */
  public static double sentimentVal( String word )
  {
    try
    {
      return sentiment.get(word.toLowerCase());
    }
    catch(Exception e)
    {
      return 0;
    }
  }
  

  
  public static double totalSentiment(String fileName)

  {
    String word = "";
    double totalSentiment = 0.0;
    String review = textToString(fileName);
    review.replaceAll("\\p{Punct}", "");
    for (int i = 0; i < review.length(); i++)
    {
       if(review.substring(i, i+1).equals(" "))
       {
          totalSentiment += sentimentVal(word);
          word = "";
       }else{
          word += review.substring(i, i+1);
          removePunctuation(word);
       }
     }
     return totalSentiment;
   }
   
   public static int starRating(String filename)
   {
      if(totalSentiment(filename) <= -3)
      {
         return 1;
      }
      else if(totalSentiment(filename) <= -1)
      {
         return 2;
      }
      else if(totalSentiment(filename) <= 1)
      {
         return 3;
      }
      else if(totalSentiment(filename) <= 3)
      {
         return 4;
      }
      else
      {
         return 5;
      }
   }
  
  // fakeReview() method added
  public static String fakeReview(String fileName)
  {
    // set fake review
    String review = textToString(fileName);
    String fake = "";
    
    // for each character in the review
    for(int i = 0; i < review.length()-1; i++)
    {
       // if the i equals an asterisk, replace it with a space and add this to the fake review
       if(review.substring(i, i+1).equals("*"))
       {
          i++;
          String replace = "";
          boolean isWord = true;
          while(isWord)
          {
             i++;
             if(review.substring(i, i+1).equals(" "))
             {
                isWord = false;
             }
          }
          replace = randomAdjective() + " ";
          fake += replace;
       }
      // if the review does not have any asterisks, then add it to the review
       else
       {
          fake += review.substring(i, i+1);
       }
    }
    // return the fake review
    return fake; 
  }

  // added fakeReviewStronger() method
  public static String fakeReviewStronger(String fileName)
  {
    // sets the review, word, and sentence
    String review = textToString(fileName);
    String word = "";
    String sentence = "";
    // goes through each character of the review
    for (int i = 0; i < review.length(); i++) 
    {
      // if the review has a space or the character is the last character
      if ((review.substring(i, i + 1).equals(" ")) || (i == review.length() - 1)) {
       // if the word ends a space set the word and add it to the review
        if (word.endsWith(" ")) word = word.substring(0, word.length()-1);
        word += review.substring(i, i + 1);
        
        // if the word starts with an asterisk, takes the sentiment value and sets an int newAdj
        if (word.startsWith("*"))
        {
          
           double s  = sentimentVal(word);
           String newAdj = "";
          
          // if statements for positive and negative words, creates new adjectives to make the review stronger pos or stronger neg
           if (s < 0)
           {
               while ( (newAdj.equals("")) || (sentimentVal(newAdj) >= s) )
                  newAdj = randomNegativeAdj();
           }
           else if (s > 0)
           {
               while ( (newAdj.equals("")) || (sentimentVal(newAdj) <= s) )
                  newAdj = randomPositiveAdj();

           }
          // remove asterisk and keep the neutral adjectives also neutral
           else
           {
               newAdj = word.substring(1);
           }

           sentence += newAdj + getPunctuation(word) + " ";
        } else {
           sentence += word + " ";
        }
        // reset the word
        word = "";
      }
    }
    return sentence;
  }
  
  /**
   * Returns the ending punctuation of a string, or the empty string if there is none 
   */
  public static String getPunctuation( String word )
  { 
    String punc = "";
    for(int i=word.length()-1; i >= 0; i--){
      if(!Character.isLetterOrDigit(word.charAt(i))){
        punc = punc + word.charAt(i);
      } else {
        return punc;
      }
    }
    return punc;
  }
  
   /**
   * Returns the word after removing any beginning or ending punctuation
   */
  public static String removePunctuation( String word )
  {
    while(word.length() > 0 && !Character.isAlphabetic(word.charAt(0)))
    {
      word = word.substring(1);
    }
    while(word.length() > 0 && !Character.isAlphabetic(word.charAt(word.length()-1)))
    {
      word = word.substring(0, word.length()-1);
    }
    
    return word;
  }
 


  
  /** 
   * Randomly picks a positive adjective from the positiveAdjectives.txt file and returns it.
   */
  public static String randomPositiveAdj()
  {
    int index = (int)(Math.random() * posAdjectives.size());
    return posAdjectives.get(index);
  }
  
  /** 
   * Randomly picks a negative adjective from the negativeAdjectives.txt file and returns it.
   */
  public static String randomNegativeAdj()
  {
    int index = (int)(Math.random() * negAdjectives.size());
    return negAdjectives.get(index);
    
  }
  
  /** 
   * Randomly picks a positive or negative adjective and returns it.
   */
  public static String randomAdjective()
  {
    boolean positive = Math.random() < .5;
    if(positive){
      return randomPositiveAdj();
    } else {
      return randomNegativeAdj();
    }
  }
}
