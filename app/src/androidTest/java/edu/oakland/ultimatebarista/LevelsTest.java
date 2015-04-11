package edu.oakland.ultimatebarista;

import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

import java.util.Arrays;

/**
 * Created by Ryan on 4/10/2015.
 */

public class LevelsTest extends ActivityInstrumentationTestCase2<Levels> {
    private Levels mLevels;
    public LevelsTest(){
        super(Levels.class);
    }
    protected void setUp() throws Exception{
        super.setUp();
        mLevels = getActivity();
    }
    public void testPreconditions(){
        assertNotNull("mLevels is null", mLevels);
    }
	public void testLevelLunL() {
        int testInt = mLevels.level;

        switch (testInt) {
            case 21:
            case 20:
                assertTrue("Level 20 isn't enabled.", mLevels.level20.isEnabled());
            case 19:
                assertTrue("Level 19 isn't enabled.", mLevels.level19.isEnabled());
            case 18:
                assertTrue("Level 18 isn't enabled.", mLevels.level18.isEnabled());
            case 17:
                assertTrue("Level 17 isn't enabled.", mLevels.level17.isEnabled());
            case 16:
                assertTrue("Level 16 isn't enabled.", mLevels.level16.isEnabled());
            case 15:
                assertTrue("Level 15 isn't enabled.", mLevels.level15.isEnabled());
            case 14:
                assertTrue("Level 14 isn't enabled.", mLevels.level14.isEnabled());
            case 13:
                assertTrue("Level 13 isn't enabled.", mLevels.level13.isEnabled());
            case 12:
                assertTrue("Level 12 isn't enabled.", mLevels.level12.isEnabled());
            case 11:
                assertTrue("Level 11 isn't enabled.", mLevels.level11.isEnabled());
            case 10:
                assertTrue("Level 10 isn't enabled.", mLevels.level10.isEnabled());
            case 9:
                assertTrue("Level 9 isn't enabled.", mLevels.level9.isEnabled());
            case 8:
                assertTrue("Level 8 isn't enabled.", mLevels.level8.isEnabled());
            case 7:
                assertTrue("Level 7 isn't enabled.", mLevels.level7.isEnabled());
            case 6:
                assertTrue("Level 6 isn't enabled.", mLevels.level6.isEnabled());
            case 5:
                assertTrue("Level 5 isn't enabled.", mLevels.level5.isEnabled());
            case 4:
                assertTrue("Level 4 isn't enabled.", mLevels.level4.isEnabled());
            case 3:
                assertTrue("Level 3 isn't enabled.", mLevels.level3.isEnabled());
            case 2:
                assertTrue("Level 2 isn't enabled.", mLevels.level2.isEnabled());
            case 1:
                assertTrue("Level 1 isn't enabled.", mLevels.level1.isEnabled());
                break;
        }
    }
}