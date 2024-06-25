package org.example.customerservice.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "relative", indexes = {
        @Index(name = "idx_relative_name", columnList = "relativeName"),
        @Index(name = "idx_relative_customer_id", columnList = "customerId"),
        @Index(name = "idx_created_at", columnList = "createdAt")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RelativeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String relativeName;

    Integer age;

    String jobName;

    String customerId;

    Boolean softDelete = false;

    Date createdAt;

    String createdBy;

    Date updatedAt;

    String lastUpdatedBy;
}
