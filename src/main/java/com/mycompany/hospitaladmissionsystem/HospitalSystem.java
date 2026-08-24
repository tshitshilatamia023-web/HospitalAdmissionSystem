/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hospitaladmissionsystem;

/**
 *
 * @author tshit
 */
//   - the ArrayList Class - patients is an ArrayList since the number
//     of patients isn't fixed in advance
//   - Using Two-Dimensional Arrays - bedGrid is a genuine 2D array
//   - Sorting Array Elements Using the Bubble Sort Algorithm
import java.util.ArrayList;

public class HospitalSystem {

    private static final int ROWS = 4;
    private static final int COLS = 5;
    private static final int TOTAL_BEDS = ROWS * COLS; // 20 beds total

    private ArrayList<Patient> patients;

    private Inpatient[][] bedGrid;

    public HospitalSystem() {
        patients = new ArrayList<>();
        bedGrid = new Inpatient[ROWS][COLS];
    }

    // turns a bed number like 5 into "B05"
    private String bedCode(int bedNumber) {
        if (bedNumber < 10) {
            return "B0" + bedNumber;
        } else {
            return "B" + bedNumber;
        }
    }

    private int rowFor(int bedNumber) {
        return (bedNumber - 1) / COLS;
    }

    private int colFor(int bedNumber) {
        return (bedNumber - 1) % COLS;
    }

    private Inpatient getBedAt(int bedNumber) {
        return bedGrid[rowFor(bedNumber)][colFor(bedNumber)];
    }

    private void setBedAt(int bedNumber, Inpatient inpatient) {
        bedGrid[rowFor(bedNumber)][colFor(bedNumber)] = inpatient;
    }

    // ---------- patient management ----------

    public void registerPatient(Patient newPatient) throws DuplicatePatientException {
        // go through the list and check nobody already has this id
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getPatientId().equalsIgnoreCase(newPatient.getPatientId())) {
                throw new DuplicatePatientException("a patient with ID " + newPatient.getPatientId() + " already exists");
            }
        }
        patients.add(newPatient);
    }

    public Patient searchPatient(String patientId) throws PatientNotFoundException {
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getPatientId().equalsIgnoreCase(patientId)) {
                return patients.get(i);
            }
        }
        throw new PatientNotFoundException("no patient found with ID " + patientId);
    }

    public void updatePatient(String patientId, String firstName, String lastName, int age, String gender, String medicalCondition) throws PatientNotFoundException {
        // this already throws PatientNotFoundException if it doesn't exist
        // so we don't need to check again here
        Patient patient = searchPatient(patientId);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setMedicalCondition(medicalCondition);
    }

    public void deletePatient(String patientId) throws PatientNotFoundException {
        Patient patient = searchPatient(patientId);

        // if this person had a bed, we need to free it up too, otherwise
        // the bed stays "occupied" forever even though the patient is gone
        if (patient instanceof Inpatient) {
            Inpatient inpatient = (Inpatient) patient;
            if (inpatient.hasBed()) {
                setBedAt(inpatient.getBedNumber(), null);
            }
        }

        patients.remove(patient);
    }

    public void displayAllPatients() {
        if (patients.size() == 0) {
            System.out.println("no patients registered yet");
            return;
        }
        for (int i = 0; i < patients.size(); i++) {
            System.out.println("----------------------------------");
            patients.get(i).displayDetails();
        }
    }

    public int getTotalPatients() {
        return patients.size();
    }

    // gives read access to the patient list - mainly here so unit tests
    // can check things like sort order without needing a separate copy
    // of the same data
    public ArrayList<Patient> getPatients() {
        return patients;
    }

    public void sortPatientsBySurname() {
        for (int i = 0; i < patients.size() - 1; i++) {
            for (int j = 0; j < patients.size() - 1 - i; j++) {
                String surname1 = patients.get(j).getLastName();
                String surname2 = patients.get(j + 1).getLastName();
                if (surname1.compareToIgnoreCase(surname2) > 0) {
                    Patient temp = patients.get(j);
                    patients.set(j, patients.get(j + 1));
                    patients.set(j + 1, temp);
                }
            }
        }
    }

    // same idea but sorting by patient id instead
    public void sortPatientsById() {
        for (int i = 0; i < patients.size() - 1; i++) {
            for (int j = 0; j < patients.size() - 1 - i; j++) {
                String id1 = patients.get(j).getPatientId();
                String id2 = patients.get(j + 1).getPatientId();
                if (id1.compareToIgnoreCase(id2) > 0) {
                    Patient temp = patients.get(j);
                    patients.set(j, patients.get(j + 1));
                    patients.set(j + 1, temp);
                }
            }
        }
    }

    // ---------- bed management ----------
 
    // picks whatever bed is free first and gives it to the inpatient
    // handy for when the user doesn't care which specific bed they get
    public void allocateBed(Inpatient inpatient) throws BedNotAvailableException {
        int freeBed = findFirstAvailableBed();
        if (freeBed == -1) {
            throw new BedNotAvailableException("no beds are available right now");
        }
        setBedAt(freeBed, inpatient);
        inpatient.setBedNumber(freeBed);
    }

    // this version lets the user ask for a specific bed number instead of
    // just grabbing whatever's free - this is what actually lets us stop
    // someone allocating a bed that's already taken
    public void allocateBed(Inpatient inpatient, int bedNumber) throws BedNotAvailableException {
        if (bedNumber < 1 || bedNumber > TOTAL_BEDS) {
            throw new BedNotAvailableException("bed number " + bedNumber + " doesn't exist, choose between 1 and " + TOTAL_BEDS);
        }
        if (getBedAt(bedNumber) != null) {
            throw new BedNotAvailableException(bedCode(bedNumber) + " is already occupied");
        }
        setBedAt(bedNumber, inpatient);
        inpatient.setBedNumber(bedNumber);
    }

    public void releaseBed(Inpatient inpatient) throws BedNotAvailableException {
        if (!inpatient.hasBed()) {
            throw new BedNotAvailableException(inpatient.getFirstName() + " " + inpatient.getLastName() + " doesn't have a bed to release");
        }
        setBedAt(inpatient.getBedNumber(), null);
        inpatient.setBedNumber(0);
    }

    private int findFirstAvailableBed() {
        // nested loop through the actual 2D grid, row by row
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (bedGrid[r][c] == null) {
                    // turn the row/col back into a bed number (1-20)
                    return (r * COLS) + c + 1;
                }
            }
        }
        return -1; // couldn't find one, ward is full
    }

    // builds one row of the ward as a String array so we can pass it to
    // printRow() below - this is the "passing array to a method" bit
    private String[] buildRow(int rowIndex) {
        String[] row = new String[COLS];
        for (int col = 0; col < COLS; col++) {
            int bedNumber = (rowIndex * COLS) + col + 1;
            Inpatient occupant = bedGrid[rowIndex][col];
            if (occupant == null) {
                row[col] = bedCode(bedNumber) + "[Free]";
            } else {
                row[col] = bedCode(bedNumber) + "[" + occupant.getLastName() + "]";
            }
        }
        return row;
    }

    private void printRow(String[] row) {
        for (int i = 0; i < row.length; i++) {
            System.out.print(row[i] + "  ");
        }
        System.out.println();
    }

    public void displayWardLayout() {
        System.out.println("Ward Layout (4 x 5):");
        for (int r = 0; r < ROWS; r++) {
            printRow(buildRow(r));
        }
    }

    public void displayAvailableBeds() {
        System.out.println("Available beds:");
        boolean anyFree = false;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (bedGrid[r][c] == null) {
                    int bedNumber = (r * COLS) + c + 1;
                    System.out.println("  " + bedCode(bedNumber));
                    anyFree = true;
                }
            }
        }
        if (!anyFree) {
            System.out.println("  none, the ward is full");
        }
    }

    public void displayOccupiedBeds() {
        System.out.println("Occupied beds:");
        boolean anyOccupied = false;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (bedGrid[r][c] != null) {
                    int bedNumber = (r * COLS) + c + 1;
                    Inpatient occupant = bedGrid[r][c];
                    System.out.println("  " + bedCode(bedNumber) + " - " + occupant.getFirstName() + " " + occupant.getLastName());
                    anyOccupied = true;
                }
            }
        }
        if (!anyOccupied) {
            System.out.println("  none, every bed is free");
        }
    }

    public int getTotalOccupiedBeds() {
        int count = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (bedGrid[r][c] != null) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getTotalBeds() {
        return TOTAL_BEDS;
    }

    public double getOccupancyPercentage() {
        return (getTotalOccupiedBeds() / (double) TOTAL_BEDS) * 100;
    }

    // ---------- reports ----------

    public void generatePatientReport() {
        System.out.println("===== PATIENT REPORT =====");
        if (patients.size() == 0) {
            System.out.println("no patients registered yet");
            return;
        }
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            System.out.println((i + 1) + ". " + p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName() + " (" + p.getCategory() + ")");
        }
        System.out.println("Total patients: " + patients.size());
    }

    public void generateWardReport() {
        System.out.println("===== WARD REPORT =====");
        System.out.println("Total registered patients: " + getTotalPatients());
        System.out.println("Total occupied beds: " + getTotalOccupiedBeds() + " / " + TOTAL_BEDS);
        double percentage = getOccupancyPercentage();
        System.out.println("Ward occupancy percentage: " + percentage + "%");
    }
}