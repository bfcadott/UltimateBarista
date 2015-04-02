package edu.oakland.ultimatebarista;

import android.test.ActivityInstrumentationTestCase2;

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
        assertEquals("The empty array wasn't correct.", propEmptyArray, mGame.emptyArray);
    }
}