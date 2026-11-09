package com.andrzej_kosmowski.visit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VisitSpecifications {
    public static Specification<VisitEntity> availableOnly(boolean onlyAvailable) {
        return (root, query, cb) -> {
            if (!onlyAvailable) {
                return cb.conjunction();
            }
            return cb.isNull(root.get("patient"));
        };
    }

    public static Specification<VisitEntity> startsAfter(LocalDateTime from) {
        return (root, query, cb) -> {
            if (from == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("startTime"), from);
        };
    }

    public static Specification<VisitEntity> startsBefore(LocalDateTime to) {
        return (root, query, cb) -> {
            if (to == null) {
                return cb.conjunction();
            }
            return cb.lessThan(root.get("startTime"), to);
        };
    }

    public static Specification<VisitEntity> hasSpecialization(String specialization) {
        return (root, query, cb) -> {
            if (specialization == null) {
                return  cb.conjunction();
            }
            return cb.equal(cb.lower(root.join("doctor").get("specialization")), specialization.toLowerCase());
        };
    }
}
