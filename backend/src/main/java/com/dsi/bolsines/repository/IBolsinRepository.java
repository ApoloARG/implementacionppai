package com.dsi.bolsines.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsi.bolsines.model.Bolsin;

@Repository
public interface IBolsinRepository extends JpaRepository<Bolsin, Integer> {
}
