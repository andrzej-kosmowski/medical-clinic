package com.andrzej_kosmowski.facility;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacilityConfiguration {

    @Bean
    FacilityService facilityService(FacilityAdapter facilityAdapter) {
        return new FacilityService(facilityAdapter);
    }
}