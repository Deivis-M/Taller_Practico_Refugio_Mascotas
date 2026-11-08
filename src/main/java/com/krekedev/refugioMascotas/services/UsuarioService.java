package com.krekedev.refugioMascotas.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.krekedev.refugioMascotas.entidades.Usuario;
import com.krekedev.refugioMascotas.repositories.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public Usuario guardar(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public Optional<Usuario> autenticar(String username, String password) {

		Optional<Usuario> usuario = usuarioRepository.findByUsername(username);

		if (usuario.isPresent()) {
			if (usuario.get().getPassword().equals(password)) {
				return usuario;
			} else {
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}

	}

}
