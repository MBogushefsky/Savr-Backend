package com.bogaware.savr.models.bank;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "goal_type")
public class GoalType {
    @Column(name = "ID")
    @Id
    private String Id;
    @Column(name = "Name")
    private String name;
}
