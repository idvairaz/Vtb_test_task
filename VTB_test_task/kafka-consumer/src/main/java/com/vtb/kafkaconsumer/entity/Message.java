package com.vtb.kafkaconsumer.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "msgUuid", unique = true, nullable = false)
    private String msgUuid;

    @Column(name = "head", nullable = false)
    private Boolean head;

    @Column(name = "timeRq", nullable = false)
    private Long timeRq;
}
