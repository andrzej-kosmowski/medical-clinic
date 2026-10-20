package com.andrzej_kosmowski.medical_clinic.specification;

import com.andrzej_kosmowski.medical_clinic.model.Visit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VisitSpecifications {
    public static Specification<Visit> availableOnly(boolean onlyAvailable) {
        return (root, query, cb) -> {
            if (!onlyAvailable) {
                return cb.conjunction();
            }
            return cb.isNull(root.get("patient"));
        };
    }

    public static Specification<Visit> startsAfter(LocalDateTime from) {
        return (root, query, cb) -> {
            if (from == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("startTime"), from);
        };
    }

    public static Specification<Visit> startsBefore(LocalDateTime to) {
        return (root, query, cb) -> {
            if (to == null) {
                return cb.conjunction();
            }
            return cb.lessThan(root.get("startTime"), to);
        };
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
