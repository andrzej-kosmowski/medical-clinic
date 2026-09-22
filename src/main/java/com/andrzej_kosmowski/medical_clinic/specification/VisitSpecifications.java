package com.andrzej_kosmowski.medical_clinic.specification;

import com.andrzej_kosmowski.medical_clinic.model.Visit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VisitSpecifications {
    public static Specification<Visit> hasNoPatient() {
        return (root, query, cb) -> cb.isNull(root.get("patient"));
    }

    public static Specification<Visit> startsBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get("startTime"), from),
                cb.lessThan(root.get("startTime"), to)
        );
    }

    public static Specification<Visit> hasSpecialization(String specialization) {
        return (root, query, cb) -> {
            if (specialization == null) {
                return  cb.conjunction();
            }
            return cb.equal(cb.lower(root.join("doctor").get("specialization")), specialization.toLowerCase());
        };
    }
}
