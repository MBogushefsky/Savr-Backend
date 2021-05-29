package com.bogaware.savr.models.bank;

import lombok.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "goal")
public class Goal {
    @Column(name = "ID")
    @Id
    private String Id;
    @Column(name = "UserID")
    private String userId;
    @Column(name = "TypeID")
    private String typeId;
    @Column(name = "Name")
    private String name;
    @Column(name = "CreatedDate")
    private Timestamp createdDate;
}
