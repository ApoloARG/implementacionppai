package com.dsi.bolsines.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsi.bolsines.model.Usuario;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, String> {
}
