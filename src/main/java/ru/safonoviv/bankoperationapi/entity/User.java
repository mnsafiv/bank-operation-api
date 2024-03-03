package ru.safonoviv.bankoperationapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@Cacheable
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "_user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_login")
    private String login;

    @JsonIgnore
    @Column(name = "user_password")
    private String password;


    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_client_id", referencedColumnName = "client_account_id")
    private ClientAccount clientAccount;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_info_client_id", referencedColumnName = "user_info_id")
    private UserInfo userInfo;


    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<UserRole> userRoles;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<UserContact> userContact;

    public User(Long id, ClientAccount clientAccount, UserInfo userInfo) {
        this.id = id;
        this.clientAccount = clientAccount;
        this.userInfo = userInfo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
