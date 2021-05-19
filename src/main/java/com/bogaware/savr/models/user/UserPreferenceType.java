package com.bogaware.savr.models.user;

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
@Table(name = "user_preference_type")
public class UserPreferenceType {
    @Column(name = "ID")
    @Id
    private String Id;
    @Column(name = "Medium")
    private String medium;
    @Column(name = "DataType")
    private String dataType;
    @Column(name = "ReferencedOff")
    private String referencedOff;
    @Column(name = "Category")
    private String category;
    @Column(name = "SectionEnabler")
    private Boolean sectionEnabler;
    @Column(name = "Immutable")
    private Boolean immutable;
    @Column(name = "Name")
    private String name;
    @Column(name = "Description")
    private String description;
    @Column(name = "Order")
    private Integer order;
}
