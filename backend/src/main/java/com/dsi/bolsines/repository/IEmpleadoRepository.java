package com.dsi.bolsines.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsi.bolsines.model.Empleado;

@Repository
public interface IEmpleadoRepository extends JpaRepository<Empleado, Long> {
}
