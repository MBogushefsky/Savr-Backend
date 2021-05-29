package com.bogaware.savr.models.bank;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "goal_value")
public class GoalValue {
    @Column(name = "ID")
    @Id
    private String Id;
    @Column(name = "GoalID")
    private String goalId;
    @Column(name = "Name")
    private String name;
    @Column(name = "Value")
    private String value;
}
