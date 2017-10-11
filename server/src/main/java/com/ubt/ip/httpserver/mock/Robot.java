package com.ubt.ip.httpserver.mock;

import com.ubt.ip.httpserver.constants.ServerConstants;

/**
 * 虚拟机器人
 */

public class Robot {

    /**
     * 机器人回读状态
     */
    private int mReadMode = ServerConstants.ReadMode.NONE;

    public int getReadMode() {
        return mReadMode;
    }
    
    public void enterReadMode() {
        mReadMode = ServerConstants.ReadMode.ALL;
    }

    public void exitReadMode() {
        mReadMode = ServerConstants.ReadMode.NONE;
    }

    private static final int MIN_MOTOR_ID = 1;
    private static final int MAX_MOTOR_ID = 7;
    private static final int MOTOR_SIZE = 7;

    private int[] mMotorsDegree = new int[MOTOR_SIZE];

    public void setMotorDegree(int motorId, int degree) {
        if (motorId < MIN_MOTOR_ID || motorId > MAX_MOTOR_ID) {
            throw new IllegalArgumentException("motorId: " + motorId + " is illegal");
        }
        mMotorsDegree[motorId - 1] = degree;
    }

    public int getMotorDegree(int motorId) {
        if (motorId < MIN_MOTOR_ID || motorId > MAX_MOTOR_ID) {
            throw new IllegalArgumentException("motorId: " + motorId + " is illegal");
        }
        return mMotorsDegree[motorId - 1];
    }

    public void setMotorsDegree(int[] motorsId, int[] degrees) {
        for (int i = 0; i < motorsId.length; i++) {
            setMotorDegree(motorsId[i], degrees[i]);
        }
    }

    public int[] getMotorsDegree(int[] motorsId) {
        int[] degrees = new int[motorsId.length];
        for (int i = 0; i < motorsId.length; i++) {
            degrees[i] = getMotorDegree(motorsId[i]);
        }
        return degrees;
    }
}