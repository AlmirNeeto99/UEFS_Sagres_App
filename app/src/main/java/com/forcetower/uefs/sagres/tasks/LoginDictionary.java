package com.forcetower.uefs.sagres.tasks;

/**
 * Created by João Paulo on 08/01/2018.
 */

public interface LoginDictionary {
    int STARTED = 0;
    int ATT_LOGIN = 1;
    int FAILED_CONNECT_LOGIN = 2;
    int CORRECT_LOGIN = 3;
    int INCORRECT_LOGIN = 4;
    int FOUND_NAME = 5;
    int ATT_STUDENT_PAGE = 6;
    int FAILED_CONNECT_STUDENT = 7;
    int CONNECTED_STUDENT_PAGE = 8;
    int CONNECTED_GRADES_PAGE = 9;
}
