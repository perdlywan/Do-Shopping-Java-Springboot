package com.example.do_shopping.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String created_by;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updated_by;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;

    @LastModifiedBy
    @Column(name = "deleted_by")
    private String deleted_by;
}
