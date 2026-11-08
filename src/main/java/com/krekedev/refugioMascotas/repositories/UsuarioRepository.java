package com.krekedev.refugioMascotas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.krekedev.refugioMascotas.entidades.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario ,Long> {
	
	Optional<Usuario> findByUsername(String username);

}
