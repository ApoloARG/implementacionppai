package com.dsi.bolsines.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsi.bolsines.model.Sesion;

@Repository
public interface ISesionRepository extends JpaRepository<Sesion, Long> {
}
