package com.smartcare.clinicmanagementsystem.config;

import com.smartcare.clinicmanagementsystem.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class MongoDataInitializer implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Clear existing data
        mongoTemplate.dropCollection("prescriptions");
        mongoTemplate.dropCollection("medical_history");

        // Insert sample prescriptions
        insertSamplePrescriptions();

        // Insert sample medical history
        insertSampleMedicalHistory();
    }

    private void insertSamplePrescriptions() {
        // Sample Prescription 1
        Medication med1 = new Medication("Lisinopril", "10mg", "Once daily", 30);
        med1.setInstructions("Take with food in the morning");
        med1.setUnit("tablet");
        med1.setQuantity(30);

        Medication med2 = new Medication("Metformin", "500mg", "Twice daily", 30);
        med2.setInstructions("Take with meals");
        med2.setUnit("tablet");
        med2.setQuantity(60);

        Prescription prescription1 = new Prescription(
                4L, // Patient David Rodriguez (diabetes)
                1L, // Dr. Smith (Cardiology)
                LocalDate.of(2024, 2, 1),
                Arrays.asList(med1, med2)
        );
        prescription1.setAppointmentId(4L);
        prescription1.setInstructions("Monitor blood pressure and blood sugar levels daily");
        prescription1.setNotes("Patient education provided on medication compliance");

        // Sample Prescription 2
        Medication med3 = new Medication("Albuterol Inhaler", "90mcg", "As needed", 30);
        med3.setInstructions("Use for shortness of breath, max 4 times daily");
        med3.setUnit("inhalation");
        med3.setQuantity(1);

        Prescription prescription2 = new Prescription(
                6L, // Patient Frank Gonzalez (asthmatic)
                2L, // Dr. Johnson (Pediatrics)
                LocalDate.of(2024, 2, 10),
                Arrays.asList(med3)
        );
        prescription2.setAppointmentId(6L);
        prescription2.setInstructions("Carry inhaler at all times");
        prescription2.setNotes("Patient demonstrated proper inhaler technique");

        // Sample Prescription 3
        Medication med4 = new Medication("Hydrocortisone Cream", "1%", "Twice daily", 14);
        med4.setInstructions("Apply thin layer to affected areas");
        med4.setUnit("gram");
        med4.setQuantity(30);

        Prescription prescription3 = new Prescription(
                5L, // Patient Eva Lopez (eczema)
                4L, // Dr. Davis (Dermatology)
                LocalDate.of(2024, 2, 5),
                Arrays.asList(med4)
        );
        prescription3.setAppointmentId(5L);
        prescription3.setInstructions("Avoid contact with eyes, wash hands after application");
        prescription3.setNotes("Follow-up in 2 weeks to assess improvement");

        // Save prescriptions
        mongoTemplate.save(prescription1);
        mongoTemplate.save(prescription2);
        mongoTemplate.save(prescription3);

        System.out.println("✓ Sample prescriptions inserted into MongoDB");
    }

    private void insertSampleMedicalHistory() {
        // Medical History for Alice Wilson (Patient ID: 1)
        VitalSigns vitals1 = new VitalSigns(120.0, 80.0, 72.0, 16.0, 98.6);
        vitals1.setOxygenSaturation(98.0);
        vitals1.setWeight(65.0);
        vitals1.setHeight(165.0);
        vitals1.calculateBmi();

        MedicalRecord record1 = new MedicalRecord(
                LocalDateTime.of(2024, 1, 15, 9, 0),
                RecordType.CONSULTATION,
                "Cardiac Evaluation",
                "Patient presented with chest pain during exercise. Vital signs stable.",
                1L // Dr. Smith
        );
        record1.setAppointmentId(1L);
        record1.setDiagnosis("Atypical chest pain, rule out cardiac cause");
        record1.setTreatment("ECG performed, stress test recommended");
        record1.setVitalSigns(vitals1);
        record1.setSymptoms(Arrays.asList("chest pain", "shortness of breath during exercise"));
        record1.setSeverity(3.0);

        MedicalRecord record2 = new MedicalRecord(
                LocalDateTime.of(2024, 1, 18, 14, 0),
                RecordType.FOLLOW_UP,
                "Cardiac Follow-up",
                "Follow-up after stress test. Results within normal limits.",
                1L // Dr. Smith
        );
        record2.setAppointmentId(3L);
        record2.setDiagnosis("Normal stress test, non-cardiac chest pain");
        record2.setTreatment("Continue current lifestyle modifications");
        record2.setSeverity(1.0);

        MedicalHistory history1 = new MedicalHistory(1L, Arrays.asList(record1, record2));
        mongoTemplate.save(history1);

        // Medical History for David Rodriguez (Patient ID: 4) - Diabetes
        VitalSigns vitals2 = new VitalSigns(140.0, 90.0, 78.0, 18.0, 98.4);
        vitals2.setOxygenSaturation(97.0);
        vitals2.setWeight(85.0);
        vitals2.setHeight(175.0);
        vitals2.calculateBmi();

        MedicalRecord record3 = new MedicalRecord(
                LocalDateTime.of(2024, 2, 1, 11, 0),
                RecordType.DIAGNOSIS,
                "Diabetes Management",
                "Regular diabetes check-up. Blood sugar levels elevated.",
                1L // Dr. Smith
        );
        record3.setAppointmentId(4L);
        record3.setDiagnosis("Type 2 Diabetes Mellitus, uncontrolled");
        record3.setTreatment("Adjusted medication dosage, dietary counseling");
        record3.setVitalSigns(vitals2);
        record3.setSymptoms(Arrays.asList("frequent urination", "increased thirst", "fatigue"));
        record3.setSeverity(4.0);
        record3.setNotes("Patient counseled on importance of medication compliance");

        MedicalHistory history2 = new MedicalHistory(4L, Arrays.asList(record3));
        mongoTemplate.save(history2);

        // Medical History for Frank Gonzalez (Patient ID: 6) - Asthma
        VitalSigns vitals3 = new VitalSigns(115.0, 75.0, 68.0, 20.0, 98.2);
        vitals3.setOxygenSaturation(95.0);
        vitals3.setWeight(70.0);
        vitals3.setHeight(170.0);
        vitals3.calculateBmi();

        MedicalRecord record4 = new MedicalRecord(
                LocalDateTime.of(2024, 2, 10, 15, 0),
                RecordType.VACCINATION,
                "Annual Flu Vaccination",
                "Routine flu shot administration. No adverse reactions.",
                2L // Dr. Johnson
        );
        record4.setAppointmentId(6L);
        record4.setTreatment("Influenza vaccine administered");
        record4.setVitalSigns(vitals3);
        record4.setSeverity(0.0);
        record4.setNotes("Patient has history of asthma, monitored for 15 minutes post-vaccination");

        MedicalRecord record5 = new MedicalRecord(
                LocalDateTime.of(2023, 8, 15, 10, 30),
                RecordType.CHRONIC_CONDITION,
                "Asthma Management",
                "Established diagnosis of asthma. Patient using rescue inhaler.",
                2L // Dr. Johnson
        );
        record5.setDiagnosis("Mild persistent asthma");
        record5.setTreatment("Albuterol inhaler as needed, patient education");
        record5.setSymptoms(Arrays.asList("wheezing", "shortness of breath", "chest tightness"));
        record5.setSeverity(2.0);
        record5.setIsConfidential(false);

        MedicalHistory history3 = new MedicalHistory(6L, Arrays.asList(record4, record5));
        mongoTemplate.save(history3);

        // Medical History for Eva Lopez (Patient ID: 5) - Skin condition
        VitalSigns vitals4 = new VitalSigns(110.0, 70.0, 65.0, 16.0, 98.8);
        vitals4.setOxygenSaturation(99.0);
        vitals4.setWeight(58.0);
        vitals4.setHeight(162.0);
        vitals4.calculateBmi();

        MedicalRecord record6 = new MedicalRecord(
                LocalDateTime.of(2024, 2, 5, 9, 30),
                RecordType.DIAGNOSIS,
                "Dermatological Consultation",
                "Patient presents with persistent rash on bilateral arms.",
                4L // Dr. Davis
        );
        record6.setAppointmentId(5L);
        record6.setDiagnosis("Atopic dermatitis (eczema)");
        record6.setTreatment("Topical corticosteroid prescribed, skin care education provided");
        record6.setVitalSigns(vitals4);
        record6.setSymptoms(Arrays.asList("skin rash", "itching", "dry skin"));
        record6.setSeverity(2.5);
        record6.setNotes("Advised to use fragrance-free moisturizers and avoid known triggers");

        MedicalHistory history4 = new MedicalHistory(5L, Arrays.asList(record6));
        mongoTemplate.save(history4);

        System.out.println("✓ Sample medical history inserted into MongoDB");
    }
}