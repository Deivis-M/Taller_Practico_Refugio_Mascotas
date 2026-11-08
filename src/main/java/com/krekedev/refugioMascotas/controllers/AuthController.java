package com.krekedev.refugioMascotas.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krekedev.refugioMascotas.entidades.Usuario;
import com.krekedev.refugioMascotas.repositories.UsuarioRepository;
import com.krekedev.refugioMascotas.services.UsuarioService;
import com.krekedev.refugioMascotas.utils.JwtUtil;

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
			String token = JwtUtil.generarToken(aunteticado.get().getUsername(), aunteticado.get().getRol());

			return ResponseEntity
					.ok(Map.of("Usuario Ingresado: ", aunteticado.get().getUsername(), "Token Generado: ", token));

		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrecta");
		}

	}

	@GetMapping("/perfil")
	public ResponseEntity<?> verPerfil(@RequestHeader(value = "Authorization", required = false) String autHeader) {

		if (autHeader == null || !autHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Acceso Denegado: Debes proveer un token valido en la cabezera Authorization ");
		}
		String token = autHeader.substring(7);

		DecodedJWT datosToken = JwtUtil.validarToken(token);

		if (datosToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso Denegado:Token Invalido o Expirado ");
		}

		String usuario = datosToken.getSubject();
		String rol = datosToken.getClaim("rol").asString();

		if (rol.equals("ADMIN")) {
			return ResponseEntity.ok(Map.of(

					"Mensaje",
					"Bienvenido, voluntario del refugio. Puedes registrar rescates, actualizar fichas de salud y autorizar solicitudes de adopción.",
					"Usuario", usuario, "Rol", rol, "Estatus", "Autenticacion Exitosa"));
		} else if (rol.equals("USER")) {
			return ResponseEntity.ok(Map.of(

					"Mensaje",
					"Bienvenido, adoptante. Puedes ver las mascotas disponibles, postularte para adoptar y actualizar tus datos de contacto.",
					"Usuario", usuario, "Rol", rol, "Estatus", "Autenticacion Exitosa"));
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Su rol no esta disponible por el momento");

		}
	}

}
