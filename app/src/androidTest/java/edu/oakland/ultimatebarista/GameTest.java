package edu.oakland.ultimatebarista;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import junit.framework.Assert;

import java.util.Arrays;

/**
 * Created by Ryan on 3/30/2015.
 */

public class GameTest extends ActivityInstrumentationTestCase2<Game> {
    private Game mGame;
    public GameTest(){
        super(Game.class);
    }
    protected void setUp() throws Exception{
        super.setUp();
        mGame = getActivity();
    }
    public void testPreconditions(){
        assertNotNull("mGame is null", mGame);
    }
    public void testArrays(){
        int[] propEmptyArray = {-1,0,-1,0,0,0,0,0,0,0,0,0};
        assertTrue("The empty array wasn't correct.", Arrays.equals(propEmptyArray, mGame.emptyArray));
	}
	public void testRandomGen(){
		int[] testArray = new int[12];
        mGame.level = 20;
		mGame.randomDrinkGen();
		testArray = mGame.customerOrder.clone();
		mGame.randomDrinkGen();
		assertFalse("Random drink was the same as old drink.", Arrays.equals(testArray, mGame.customerOrder));
    }
     @UiThreadTest
	 public void testUserBevCreation(){
		 int[] testArray = new int[12];
		 testArray = mGame.userBeverage.clone();
		 mGame.smallButton.performClick();
		 assertFalse("The user drink array was not changed.", Arrays.equals(testArray, mGame.userBeverage));
	 }
     @UiThreadTest
	 public void testUserBevChecking(){
		 int testInteger = mGame.drinksMade;
		 mGame.randomDrinkGen();
		 mGame.userBeverage = mGame.customerOrder.clone();
		 mGame.handButton.performClick();
		 assertFalse("Drink counter did not increment when correct drink checked.", testInteger == mGame.drinksMade);
	 }
     @UiThreadTest
	 public void testIndividualLevelObj(){
		 long testTimeLimit;
		 int testDrinkGoal;
         mGame.level = 20;
         mGame.setUpLevel();
		 testTimeLimit = mGame.timeLimit;
		 testDrinkGoal = mGame.drinksGoal;
         mGame.level = 19;
         mGame.setUpLevel();
		 assertFalse("Time limit hasn't changed", testTimeLimit == mGame.timeLimit);
		 assertFalse("Drink goal hasn't changed.", testDrinkGoal == mGame.drinksGoal);
	 }
}