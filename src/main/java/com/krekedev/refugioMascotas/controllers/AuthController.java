package com.krekedev.refugioMascotas.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krekedev.refugioMascotas.entidades.Usuario;
import com.krekedev.refugioMascotas.repositories.UsuarioRepository;
import com.krekedev.refugioMascotas.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UsuarioService usuarioServicio;

	private final UsuarioRepository usuarioRepository;

	public AuthController(UsuarioService usuarioServicio, UsuarioRepository usuarioRepository) {
		super();
		this.usuarioServicio = usuarioServicio;
		this.usuarioRepository = usuarioRepository;
	}

	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioNuevo = usuarioServicio.guardar(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioNuevo);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro al registrar usuario: " + e.getMessage());
		}

	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {

		String username = credenciales.get("username");
		String password = credenciales.get("password");

		Optional<Usuario> aunteticado = usuarioServicio.autenticar(username, password);

		if (aunteticado.isPresent()) {
			return ResponseEntity.ok(Map.of("Usuario Ingresado", aunteticado.get()));

		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrecta");
		}

	}

}
