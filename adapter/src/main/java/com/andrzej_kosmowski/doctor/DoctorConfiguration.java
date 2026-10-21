package com.andrzej_kosmowski.doctor;

import com.andrzej_kosmowski.facility.FacilityUseCase;
import com.andrzej_kosmowski.user.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DoctorConfiguration {

    @Bean
    DoctorService doctorService(DoctorAdapter doctorAdapter, UserUseCase userUseCase, FacilityUseCase facilityUseCase) {
       return new DoctorService(doctorAdapter,  userUseCase, facilityUseCase);
    }
}
