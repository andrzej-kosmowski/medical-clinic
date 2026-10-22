package com.andrzej_kosmowski.patient;

import com.andrzej_kosmowski.user.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatientConfiguration {
    @Bean
    PatientService patientService(PatientAdapter patientAdapter, UserUseCase userUseCase) {
        return new PatientService(patientAdapter,  userUseCase);
    }
}
