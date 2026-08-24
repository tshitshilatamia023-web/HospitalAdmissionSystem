/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hospitaladmissionsystem;

/**
 *
 * @author tshit
 */
public class Inpatient extends Patient {

    private int wardNumber;
    private int bedNumber; // 0 means "doesn't have a bed yet"

    public Inpatient(String patientId, String firstName, String lastName, int age, String gender, String medicalCondition, int wardNumber) {
        super(patientId, firstName, lastName, age, gender, medicalCondition, PatientCategory.INPATIENT);
        this.wardNumber = wardNumber;
        this.bedNumber = 0;
    }

    public int getWardNumber() {
        return wardNumber;
    }

    public int getBedNumber() {
        return bedNumber;
    }

    public boolean hasBed() {
        return bedNumber != 0;
    }

    public void setBedNumber(int bedNumber) {
        this.bedNumber = bedNumber;
    }

    private String getBedCode() {
        if (bedNumber < 10) {
            return "B0" + bedNumber;
        } else {
            return "B" + bedNumber;
        }
    }

    @Override
    public void displayDetails() {
      
        super.displayDetails();
        System.out.println("Ward Number: " + wardNumber);
        if (hasBed()) {
            System.out.println("Bed Number: " + getBedCode());
        } else {
            System.out.println("Bed Number: not allocated yet");
        }
    }
}