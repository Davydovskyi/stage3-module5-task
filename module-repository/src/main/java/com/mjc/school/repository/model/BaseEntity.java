package com.mjc.school.repository.model;

public interface BaseEntity<K> {

    K getId();

    void setId(K id);
}
