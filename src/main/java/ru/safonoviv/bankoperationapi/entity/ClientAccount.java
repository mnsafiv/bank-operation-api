package ru.safonoviv.bankoperationapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.text.DecimalFormat;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "_client_account")
public class ClientAccount implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_account_id")
    private Long id;

    @Column(name = "client_account_balance_start", columnDefinition="Decimal(10,2)")
    private Float balanceStart;

    @Column(name = "client_account_balance_current", columnDefinition="Decimal(10,2)")
    private Float balanceCurrent;

    @Column(name = "client_account_verified")
    private boolean verified;

    @OneToOne(mappedBy = "clientAccount", fetch = FetchType.EAGER)
    private User user;

}
