package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.doctor.DoctorUseCase;
import com.andrzej_kosmowski.patient.PatientUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VisitConfiguration {

    @Bean
    VisitService visitService(VisitAdapter visitAdapter, DoctorUseCase doctorUseCase,
                              PatientUseCase patientUseCaseUseCase) {
        return new VisitService(visitAdapter, doctorUseCase, patientUseCaseUseCase);
    }
}
