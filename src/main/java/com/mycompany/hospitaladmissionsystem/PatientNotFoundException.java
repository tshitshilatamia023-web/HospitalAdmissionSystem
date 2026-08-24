/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hospitaladmissionsystem;

/**
 *
 * @author tshit
 */
public class PatientNotFoundException extends Exception {
    public PatientNotFoundException(String message) {
        super(message);
    }
}

// thrown when someone tries to register a patient id that's already taken
class DuplicatePatientException extends Exception {
    public DuplicatePatientException(String message) {
        super(message);
    }
}

// thrown for bed problems - either the bed is taken or there are none left
class BedNotAvailableException extends Exception {
    public BedNotAvailableException(String message) {
        super(message);
    }
}

// thrown for trying to give a bed to someone who isn't an inpatient
class InvalidCategoryException extends Exception {
    public InvalidCategoryException(String message) {
        super(message);
    }
}