/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hospitaladmissionsystem;

/**
 *
 * @author tshit
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// tests for feature 5 - basically one test per thing the brief asked us to test
public class HospitalSystemTest {

    private HospitalSystem hospitalSystem;

    // runs before each test so every test starts fresh, no leftover data
    // from whatever test ran before it
    @BeforeEach
    void setUp() {
        hospitalSystem = new HospitalSystem();
    }

    @Test
    void testRegisterPatient() throws Exception {
        Patient patient = new Patient("P001", "John", "Smith", 30, "Male", "Flu", PatientCategory.OUTPATIENT);
        hospitalSystem.registerPatient(patient);
        assertEquals(1, hospitalSystem.getTotalPatients());
    }

    @Test
    void testSearchPatient() throws Exception {
        Patient patient = new Patient("P002", "Jane", "Doe", 25, "Female", "Migraine", PatientCategory.EMERGENCY);
        hospitalSystem.registerPatient(patient);

        Patient found = hospitalSystem.searchPatient("P002");
        assertEquals("Doe", found.getLastName());
    }

    @Test
    void testUpdatePatientDetails() throws Exception {
        Patient patient = new Patient("P003", "Amy", "Adams", 40, "Female", "Flu", PatientCategory.OUTPATIENT);
        hospitalSystem.registerPatient(patient);

        hospitalSystem.updatePatient("P003", "Amy", "Adams", 41, "Female", "Recovered");

        Patient updated = hospitalSystem.searchPatient("P003");
        assertEquals(41, updated.getAge());
        assertEquals("Recovered", updated.getMedicalCondition());
    }

    @Test
    void testDeletePatient() throws Exception {
        Patient patient = new Patient("P004", "Tom", "Lee", 50, "Male", "Cold", PatientCategory.OUTPATIENT);
        hospitalSystem.registerPatient(patient);

        hospitalSystem.deletePatient("P004");

        assertEquals(0, hospitalSystem.getTotalPatients());
        assertThrows(PatientNotFoundException.class, () -> hospitalSystem.searchPatient("P004"));
    }

    @Test
    void testAllocateBed() throws Exception {
        Inpatient inpatient = new Inpatient("P005", "Sam", "Ray", 60, "Male", "Surgery", 1);
        hospitalSystem.registerPatient(inpatient);

        hospitalSystem.allocateBed(inpatient);

        assertTrue(inpatient.hasBed());
        assertEquals(1, hospitalSystem.getTotalOccupiedBeds());
    }

    @Test
    void testReleaseBed() throws Exception {
        Inpatient inpatient = new Inpatient("P006", "Kim", "Park", 35, "Female", "Fracture", 1);
        hospitalSystem.registerPatient(inpatient);
        hospitalSystem.allocateBed(inpatient);

        hospitalSystem.releaseBed(inpatient);

        assertFalse(inpatient.hasBed());
        assertEquals(0, hospitalSystem.getTotalOccupiedBeds());
    }

    @Test
    void testPreventDuplicatePatientIds() throws Exception {
        Patient patient1 = new Patient("P007", "Alex", "Wong", 28, "Male", "Cough", PatientCategory.OUTPATIENT);
        hospitalSystem.registerPatient(patient1);

        Patient patient2 = new Patient("P007", "Someone", "Else", 45, "Female", "Fever", PatientCategory.EMERGENCY);

        assertThrows(DuplicatePatientException.class, () -> hospitalSystem.registerPatient(patient2));
    }

    @Test
    void testPreventAllocatingOccupiedBed() throws Exception {
        // put the first inpatient in bed 5 on purpose, then try to put the
        // second inpatient in that exact same bed - should get blocked
        Inpatient inpatient1 = new Inpatient("P008", "Nina", "Cole", 33, "Female", "Surgery", 1);
        Inpatient inpatient2 = new Inpatient("P009", "Leo", "Grant", 29, "Male", "Observation", 1);
        hospitalSystem.registerPatient(inpatient1);
        hospitalSystem.registerPatient(inpatient2);

        hospitalSystem.allocateBed(inpatient1, 5);

        assertThrows(BedNotAvailableException.class, () -> hospitalSystem.allocateBed(inpatient2, 5));
    }

    @Test
    void testPreventBedAllocationWhenFull() throws Exception {
        // fill up all 20 beds one by one
        for (int i = 1; i <= 20; i++) {
            Inpatient inpatient = new Inpatient("P" + (100 + i), "First" + i, "Last" + i, 20, "Male", "Condition", 1);
            hospitalSystem.registerPatient(inpatient);
            hospitalSystem.allocateBed(inpatient);
        }

        // now try to squeeze one more patient in, should fail since the ward is full
        Inpatient extraPatient = new Inpatient("P200", "Extra", "Patient", 22, "Female", "Condition", 1);
        hospitalSystem.registerPatient(extraPatient);

        assertThrows(BedNotAvailableException.class, () -> hospitalSystem.allocateBed(extraPatient));
    }

    @Test
    void testSortPatientsBySurname() throws Exception {
        hospitalSystem.registerPatient(new Patient("P010", "Zoe", "Zebra", 20, "Female", "Cold", PatientCategory.OUTPATIENT));
        hospitalSystem.registerPatient(new Patient("P011", "Amy", "Apple", 21, "Female", "Cold", PatientCategory.OUTPATIENT));
        hospitalSystem.registerPatient(new Patient("P012", "Mike", "Mango", 22, "Male", "Cold", PatientCategory.OUTPATIENT));

        hospitalSystem.sortPatientsBySurname();

        // after sorting, surnames should read Apple, Mango, Zebra in that order
        assertEquals("Apple", hospitalSystem.getPatients().get(0).getLastName());
        assertEquals("Mango", hospitalSystem.getPatients().get(1).getLastName());
        assertEquals("Zebra", hospitalSystem.getPatients().get(2).getLastName());
    }

    @Test
    void testSortPatientsById() throws Exception {
        hospitalSystem.registerPatient(new Patient("P020", "Zoe", "Zebra", 20, "Female", "Cold", PatientCategory.OUTPATIENT));
        hospitalSystem.registerPatient(new Patient("P019", "Amy", "Apple", 21, "Female", "Cold", PatientCategory.OUTPATIENT));
        hospitalSystem.registerPatient(new Patient("P021", "Mike", "Mango", 22, "Male", "Cold", PatientCategory.OUTPATIENT));

        hospitalSystem.sortPatientsById();

        // after sorting by id, they should read P019, P020, P021 in order
        assertEquals("P019", hospitalSystem.getPatients().get(0).getPatientId());
        assertEquals("P020", hospitalSystem.getPatients().get(1).getPatientId());
        assertEquals("P021", hospitalSystem.getPatients().get(2).getPatientId());
    }
}