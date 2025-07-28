package com.example.fileserver.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Long id;
    private String fullName;
    @Column(unique = true)
    private String username;
    private String password;
    private String contactNumber;
    private String email;
    private String profilePictureUrl; // URL to the user's profile picture
    private Long createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    private String status;
}
















