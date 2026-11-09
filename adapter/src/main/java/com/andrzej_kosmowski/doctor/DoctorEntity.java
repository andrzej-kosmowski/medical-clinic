package com.andrzej_kosmowski.doctor;

import com.andrzej_kosmowski.facility.FacilityEntity;
import com.andrzej_kosmowski.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String specialization;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id" , nullable = false, unique = true)
    private UserEntity user;
    @ManyToMany
    @JoinTable(
            name = "doctor_facilities",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<FacilityEntity> facilities = new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoctorEntity doctor)) return false;
        return id != null && id.equals(doctor.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
