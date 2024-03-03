package ru.safonoviv.bankoperationapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "_user_info")
public class UserInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_info_id")
    private Long id;

    @Column(name = "user_info_date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "user_info_first_name")
    private String firstName;
    @Column(name = "user_info_second_name")
    private String secondName;
    @Column(name = "user_info_middle_name")
    private String middleName;


    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_info_user_id", referencedColumnName = "user_id")
    private User user;
}
