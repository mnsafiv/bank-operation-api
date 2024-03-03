package ru.safonoviv.bankoperationapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

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

    @Column(name = "client_account_balance_start")
    private double balanceStart;

    @Column(name = "client_account_balance_current")
    private double balanceCurrent;

    @Column(name = "client_account_verified")
    private boolean verified;

    @OneToOne(mappedBy = "clientAccount",fetch = FetchType.EAGER)
    private User user;

}
