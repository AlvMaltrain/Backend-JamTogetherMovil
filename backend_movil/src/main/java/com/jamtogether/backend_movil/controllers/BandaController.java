package com.jamtogether.backend_movil.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jamtogether.backend_movil.models.Banda;
import com.jamtogether.backend_movil.models.Usuario;
import com.jamtogether.backend_movil.repositories.BandaRepository;
import com.jamtogether.backend_movil.repositories.UsuarioRepository;

@RestController
@RequestMapping("/api/bandas")
@CrossOrigin(origins = "*")
public class BandaController {

    @Autowired
    private BandaRepository bandaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // -- ENDPOINT PARA CREAR BANDA --
    // URL: POST http://localhost:8080/api/bandas/crear?idUsuario=1
    @PostMapping("/crear")
    public ResponseEntity<?> crearBanda(@RequestBody Banda banda, @RequestParam Long idUsuario) {

        // Buscamos al usuario que quiere crear la banda
        Usuario creador = usuarioRepository.findById(idUsuario).orElse(null);

        if (creador == null) {
            return ResponseEntity.badRequest().body("Error: Usuario no encontrado.");
        }
        // Forzamos el ID a null antes de guardar.
        // Seteamos el id a null para generar uno nuevo
        banda.setId(null); 
        // -------------------------------

        //  Asignamos el creador a la banda (relacion)
        banda.setCreador(creador);

        //  Guardamos la banda en la BD
        Banda nuevaBanda = bandaRepository.save(banda);

        return ResponseEntity.ok(nuevaBanda);
    }

    // -- ENDPOINT PARA LISTAR TODAS LAS BANDAS --
    // URL: GET http://localhost:8080/api/bandas
    @GetMapping
    public List<Banda> listarBandas() {
        return bandaRepository.findAll();
    }
}
