/*
 * File: Hangman.java
 * ------------------
 * This program will eventually play the Hangman game from
 * Assignment #4.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Hangman extends ConsoleProgram {

	/***********************************************************
	 *              CONSTANTS                                  *
	 ***********************************************************/
	
	/* The number of guesses in one game of Hangman */
	private static final int N_GUESSES = 7;
	/* The width and the height to make the karel image */
	private static final int KAREL_SIZE = 150;
	/* The y-location to display karel */
	private static final int KAREL_Y = 230;
	/* The width and the height to make the parachute image */
	private static final int PARACHUTE_WIDTH = 300;
	private static final int PARACHUTE_HEIGHT = 130;
	/* The y-location to display the parachute */
	private static final int PARACHUTE_Y = 50;
	/* The y-location to display the partially guessed string */
	private static final int PARTIALLY_GUESSED_Y = 530;
	/* The y-location to display the incorrectly guessed letters */
	private static final int INCORRECT_GUESSES_Y = 560;
	/* The fonts of both labels */
	private static final String PARTIALLY_GUESSED_FONT = "Courier-36";
	private static final String INCORRECT_GUESSES_FONT = "Courier-26";
	/* The Text file location*/
	private static final String FILE_NAME = "D:\\STUDY\\CS106A-Stanford.programming methodology(2016-17)\\Course\\week-5\\Assignment-04\\Startter\\Assignment4\\HangmanLexicon.txt";
	
	/***********************************************************
	 *              Instance Variables                         *
	 ***********************************************************/
	
	private RandomGenerator rg = new RandomGenerator();
	
	private int wordLength = 0;
	private String result = "";
	private String guessStr = "";
	private boolean guessFlag = false;
	private boolean counterFlag = false;
	private boolean prevoiusleyGuessed = false;
	char guess;
	int countGuess = 0;
	int loopCounter = 0;
	private GImage karel;
	private GLine lineLeft_1, lineRight_1, lineLeft_2, lineRight_2, lineLeft_3, lineRight_3, lineMiddle;
	private GLabel resultL, guessL;
	private ArrayList<String> list = new ArrayList<String>();
	
	private GCanvas canvas = new GCanvas();
	
	/***********************************************************
	 *                    Methods                              *
	 ***********************************************************/
	
	public void run() {
		println("Welcome to Hangman\n");
		makeDataList();
		playConsoleGame();
	}
	
	// make the list of data from the text doc
	private void makeDataList() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
			while(true) {
				String line = br.readLine();
				if(line == null) {
					break;
				}
				list.add(line);
			}
			br.close();
		} catch (IOException e) {
			println("An error occured: " + e);
		}
	}
	
	public void init() {
		add(canvas);
		drawBackground();
		drawStrings();
		drawResultField();
	}
	
	// set the result with guessed text view in the graphics canvas
	private void drawResultField() {
		resultL = new GLabel("--", canvas.getWidth()/2 - result.length()/2, PARTIALLY_GUESSED_Y);
		resultL.setFont(PARTIALLY_GUESSED_FONT);
		canvas.add(resultL);
		
		guessL = new GLabel("--", canvas.getWidth()/2 - result.length()/2, INCORRECT_GUESSES_Y);
		guessL.setFont(INCORRECT_GUESSES_FONT);
		canvas.add(guessL);
	}
	
	//draws Strings that holds karel with the parachute
	private void drawStrings() {
		
		int x1 = (canvas.getWidth()/2 - KAREL_SIZE/2) + KAREL_SIZE/2;
		lineMiddle = getLine(x1);
		
		x1 = getX1((PARACHUTE_WIDTH/2) * (-1));
		lineLeft_1 = getLine(x1);
		
		x1 = getX1(PARACHUTE_WIDTH/2);
		lineRight_1 = getLine(x1);
		
		x1 = getX1((PARACHUTE_WIDTH/4) * (-1));
		lineLeft_2 = getLine(x1);
		
		x1 = getX1(PARACHUTE_WIDTH/4);
		lineRight_2 = getLine(x1);
		
		x1 = getX1((PARACHUTE_WIDTH/8) * (-1));
		lineLeft_3 = getLine(x1);
		
		x1 = getX1(PARACHUTE_WIDTH/8);
		lineRight_3 = getLine(x1);
	}
	
	// returns the x co ordinate that very from line to line
	private int getX1(int P_Width) {
		return canvas.getWidth()/2 + P_Width;
	}
	
	// rerurns line that just draws on the canvus
	private GLine getLine(int x1) {
		int x0 = (canvas.getWidth()/2 - KAREL_SIZE/2) + KAREL_SIZE/2;
		int y0 = KAREL_Y ;
		int y1 = PARACHUTE_Y +  PARACHUTE_HEIGHT;
		GLine line = new GLine(x0, y0, x1, y1);
		canvas.add(line);
		return line;
	}
	
	// Draws the graphical view of the canvas
	private void drawBackground() {
		GImage bg = new GImage("background.jpg");
		bg.setSize(canvas.getWidth(), canvas.getHeight());
		canvas.add(bg, 0, 0);
		
		GImage parachute = new GImage("parachute.png");
		parachute.setSize(PARACHUTE_WIDTH, PARACHUTE_HEIGHT);
		canvas.add(parachute,canvas.getWidth()/2 - PARACHUTE_WIDTH/2,PARACHUTE_Y );
		
		karel = new GImage("karel.png");
		karel.setSize(KAREL_SIZE, KAREL_SIZE);
		canvas.add(karel, canvas.getWidth()/2 - KAREL_SIZE/2, KAREL_Y);
	}
	
	//control the console play
	private void playConsoleGame() {
		String word = getRandomWord();
		loopCounter = 0;
		wordLength = word.length();
		setInitialResult();
		printResult();
		
		while(loopCounter < N_GUESSES) {
			println("You have " +( N_GUESSES-loopCounter) + " guess left");
			String guessStr = readLine("Your guess: ");
			updateResult(guessStr, word);
			setResultLabel();
			if(countGuess == wordLength) {
				break;
			}
			if(counterFlag != true) {
				loopCounter++;
				cutString(loopCounter);
				counterFlag = false;
			}
			counterFlag = false;
		}
		printEndMessege(word);
	}
	
	// Update the result text view on the canvas
	private void setResultLabel() {
		resultL.setLabel(result);
		resultL.setLocation(canvas.getWidth()/2 - resultL.getWidth()/2, PARTIALLY_GUESSED_Y);
		
		guessL.setLabel(guessStr);
		guessL.setLocation(canvas.getWidth()/2 - guessL.getWidth()/2, INCORRECT_GUESSES_Y);
	}
	
	// Remove the Strings of the parachute
	private void cutString(int stNum) {
		switch(stNum) {
		case 1:
			canvas.remove(lineLeft_1);
			break;
		case 2:
			canvas.remove(lineRight_1);
			break;
		case 3:
			canvas.remove(lineLeft_2);
			break;
		case 4:
			canvas.remove(lineRight_2);
			break;
		case 5:
			canvas.remove(lineLeft_3);
			break;
		case 6:
			canvas.remove(lineRight_3);
			break;
		case 7:
			canvas.remove(lineMiddle);
			break;
		default:
		}
	}
	
	// prints the user wins or looses
	private void printEndMessege(String word) {
		if(countGuess == wordLength) {
			println("You win.");
			println("The word was: "+ word);
		}else {
			setKarelUpsideDown();
			println("You are completely hung.");
			println("The word was: "+ word);
			println("Your Guess: "+ guessStr);
		}
	}
	
	// Set the karel to upside down when user is out of guess
	private void setKarelUpsideDown() {
		karel.setImage("KarelFlipped.png");
		karel.setSize(KAREL_SIZE, KAREL_SIZE);
	}
	
	// Update the result of guess
	
	//update the current result value depending on the guess
	private void updateResult(String guessStr, String word) {
		if(guessStr.length() == 1 && Character.isLetter(guessStr.charAt(0))) {
			guess = getGuess(guessStr);
			result = getUpdatedResult(word);
			printResult();
		}else {
			println("not Valid guess");
			printResult();
		}
	}
	
	
	//returns the result output string
	private String getUpdatedResult(String word) {
		String res = "";
		guessFlag = false;
		
		for(int i=0; i<word.length(); i++) {
			if(word.charAt(i) == guess) {
				res += guess;
				guessFlag = true;
				counterFlag = true;
				if(isGussedFirstTime()) {
					countGuess++;
				}else {
					prevoiusleyGuessed = true;
				}
			}else if(Character.isLetter(result.charAt(i))){
				res += result.charAt(i);
			}else {
				res += "-";
			}
		}
		printGuessedMessege();
		return res;
	}
	
	private boolean isGussedFirstTime() {
		int c = 0;
		for(int i=0; i<guessStr.length()-1; i++) {
			if(guessStr.charAt(i) == guess) {
				c = 1;
			}
		}
		return c == 0;
	}
	//print the guessed character was present on the word or not
	private void printGuessedMessege() {
		if(guessFlag) {
			if(prevoiusleyGuessed) {
				println("You already Gussed that");
				prevoiusleyGuessed = false;
			}else {
				println("That guess is correct.");
			}
		}else{
			println("There are no " + guess + "'s in the word");
		}
		guessFlag = false;
	}
	
	//returns the guessed character
	private char getGuess(String str) {
		char ch = str.charAt(0);
		guessStr += Character.toUpperCase(ch);
		return Character.toUpperCase(ch);
	}
	
	//set result value all as "-"
	private void setInitialResult() {
		for(int i=0; i<wordLength; i++ ) {
			result += "-";
		}
	}
	
	//prints the resultant ouput String
	private void printResult() {
		println("Your Word now looks like this: " + result);
	}
	
	/**
	 * Method: Get Random Word
	 * -------------------------
	 * This method returns a word to use in the hangman game. It randomly 
	 * selects from the array list
	 */
	private String getRandomWord() {
		int index = rg.nextInt(list.size()-1);
		return list.get(index);
	}

}
