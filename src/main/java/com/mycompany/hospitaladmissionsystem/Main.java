/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.hospitaladmissionsystem;

/**
 *
 * @author tshit
 */
import java.util.Scanner;

public class Main {

    private static HospitalSystem hospitalSystem = new HospitalSystem();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        System.out.println("=====================================");
        System.out.println(" MediCare Hospital - Admission System");
        System.out.println("=====================================");

        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            if (choice == 1) {
                registerPatientMenu();
            } else if (choice == 2) {
                searchPatientMenu();
            } else if (choice == 3) {
                updatePatientMenu();
            } else if (choice == 4) {
                deletePatientMenu();
            } else if (choice == 5) {
                hospitalSystem.displayAllPatients();
            } else if (choice == 6) {
                allocateBedMenu();
            } else if (choice == 7) {
                releaseBedMenu();
            } else if (choice == 8) {
                hospitalSystem.displayWardLayout();
            } else if (choice == 9) {
                hospitalSystem.displayAvailableBeds();
            } else if (choice == 10) {
                hospitalSystem.displayOccupiedBeds();
            } else if (choice == 11) {
                hospitalSystem.generateWardReport();
            } else if (choice == 12) {
                hospitalSystem.generatePatientReport();
            } else if (choice == 13) {
                sortMenu();
            } else if (choice == 0) {
                running = false;
                System.out.println("Goodbye!");
            } else {
                System.out.println("that's not a valid option, try again");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n----- MAIN MENU -----");
        System.out.println("1. Register a patient");
        System.out.println("2. Search for a patient");
        System.out.println("3. Update patient details");
        System.out.println("4. Delete a patient");
        System.out.println("5. Display all patients");
        System.out.println("6. Allocate a bed");
        System.out.println("7. Release a bed");
        System.out.println("8. Display ward layout");
        System.out.println("9. Display available beds");
        System.out.println("10. Display occupied beds");
        System.out.println("11. Generate ward report");
        System.out.println("12. Generate patient report");
        System.out.println("13. Sort patients");
        System.out.println("0. Exit");
    }

    private static void registerPatientMenu() {
        System.out.println("\n-- Register New Patient --");
        String id = readString("Patient ID: ");
        String firstName = readString("First Name: ");
        String lastName = readString("Last Name: ");
        int age = readInt("Age: ");
        String gender = readString("Gender: ");
        String condition = readString("Medical Condition: ");
        PatientCategory category = readCategory();

        try {
            if (category == PatientCategory.INPATIENT) {
                // only one ward in this system so ward number is always 1
                Inpatient inpatient = new Inpatient(id, firstName, lastName, age, gender, condition, 1);
                hospitalSystem.registerPatient(inpatient);
            } else {
                Patient patient = new Patient(id, firstName, lastName, age, gender, condition, category);
                hospitalSystem.registerPatient(patient);
            }
            System.out.println("patient registered successfully");
        } catch (DuplicatePatientException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    private static void searchPatientMenu() {
        String id = readString("Enter Patient ID to search: ");
        try {
            Patient patient = hospitalSystem.searchPatient(id);
            patient.displayDetails();
        } catch (PatientNotFoundException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    private static void updatePatientMenu() {
        String id = readString("Enter Patient ID to update: ");
        try {
            // show what's already there first so the user knows what they're changing
            hospitalSystem.searchPatient(id).displayDetails();

            String firstName = readString("New First Name: ");
            String lastName = readString("New Last Name: ");
            int age = readInt("New Age: ");
            String gender = readString("New Gender: ");
            String condition = readString("New Medical Condition: ");

            hospitalSystem.updatePatient(id, firstName, lastName, age, gender, condition);
            System.out.println("patient updated successfully");
        } catch (PatientNotFoundException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    private static void deletePatientMenu() {
        String id = readString("Enter Patient ID to delete: ");
        try {
            hospitalSystem.deletePatient(id);
            System.out.println("patient deleted successfully");
        } catch (PatientNotFoundException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    private static void allocateBedMenu() {
        String id = readString("Enter Patient ID to allocate a bed to: ");
        try {
            Patient patient = hospitalSystem.searchPatient(id);
            if (!(patient instanceof Inpatient)) {
                System.out.println("only inpatients can be allocated a bed");
                return;
            }

            int bedNumber = readInt("Enter bed number 1-20 (or 0 for the next available bed): ");
            Inpatient inpatient = (Inpatient) patient;

            if (bedNumber == 0) {
                hospitalSystem.allocateBed(inpatient);
            } else {
                hospitalSystem.allocateBed(inpatient, bedNumber);
            }
            System.out.println("bed allocated successfully");
        } catch (PatientNotFoundException e) {
            System.out.println("error: " + e.getMessage());
        } catch (BedNotAvailableException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    private static void releaseBedMenu() {
        String id = readString("Enter Patient ID to release a bed from: ");
        try {
            Patient patient = hospitalSystem.searchPatient(id);
            if (!(patient instanceof Inpatient)) {
                System.out.println("this patient can't hold a bed, only inpatients can");
                return;
            }
            hospitalSystem.releaseBed((Inpatient) patient);
            System.out.println("bed released successfully");
        } catch (PatientNotFoundException e) {
            System.out.println("error: " + e.getMessage());
        } catch (BedNotAvailableException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    private static void sortMenu() {
        System.out.println("Sort by: 1. Surname   2. Patient ID");
        int choice = readInt("Choice: ");
        if (choice == 1) {
            hospitalSystem.sortPatientsBySurname();
            System.out.println("sorted by surname");
        } else if (choice == 2) {
            hospitalSystem.sortPatientsById();
            System.out.println("sorted by patient id");
        } else {
            System.out.println("not a valid choice");
            return;
        }
        hospitalSystem.displayAllPatients();
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
         
                System.out.println("please enter a whole number");
            }
        }
    }

    private static PatientCategory readCategory() {
        while (true) {
            System.out.print("Category (1=Inpatient, 2=Outpatient, 3=Emergency): ");
            String input = scanner.nextLine().trim();
            if (input.equals("1")) {
                return PatientCategory.INPATIENT;
            } else if (input.equals("2")) {
                return PatientCategory.OUTPATIENT;
            } else if (input.equals("3")) {
                return PatientCategory.EMERGENCY;
            } else {
                System.out.println("please enter 1, 2 or 3");
            }
        }
    }
}
