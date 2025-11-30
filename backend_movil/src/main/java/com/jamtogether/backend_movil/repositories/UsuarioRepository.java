package com.jamtogether.backend_movil.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jamtogether.backend_movil.models.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    // Busca por email
    Usuario findByEmail(String email);

    // Busca por email y contraseña (para el login)
    Usuario findByEmailAndPassword(String email, String password);

    //Busca por Ciudad
    List<Usuario> findByCiudad(String ciudad);

}

