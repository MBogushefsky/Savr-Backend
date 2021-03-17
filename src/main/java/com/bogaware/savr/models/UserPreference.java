package com.bogaware.savr.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "UserPreference")
public class UserPreference {
    @Column(name = "ID")
    @Id
    private String Id;
    @Column(name = "UserID")
    private String userId;
    @Column(name = "Preference")
    private String preference;
    @Column(name = "Value")
    private boolean value;
}
