package com.bogaware.savr.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TwilioMessage")
public class TwilioMessage {
    @Column(name = "ID")
    @Id
    private String Id;
    @Column(name = "ToPhoneNumber")
    private String toPhoneNumber;
    @Column(name = "FromPhoneNumber")
    private String fromPhoneNumber;
    @Column(name = "Incoming")
    private boolean incoming;
    @Column(name = "Body")
    private String body;
    @Column(name = "DateTime")
    private Timestamp dateTime;
}
