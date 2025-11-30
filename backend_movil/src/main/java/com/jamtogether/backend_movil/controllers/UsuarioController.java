package com.jamtogether.backend_movil.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jamtogether.backend_movil.models.Usuario;
import com.jamtogether.backend_movil.repositories.UsuarioRepository;

@RestController
@RequestMapping("/api/usuarios")
//Permite que celular o navegador se conecten sin problemas de seguridad
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    //---ENDPOINT DE REGISTRO---
    //URL: /api/usuarios/registro
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        //Verificamos si el email ya está registrado
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Error: El email ya está registrado.");
        }

        usuario.setId(null);

        //Guardamos el nuevo usuario en la bd
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        //Devolvemos el usuario creado (con el id generado
        return ResponseEntity.ok(nuevoUsuario);
    }

    //---ENDPOINT DE LOGIN---
    //URL: /api/usuarios/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario credenciales) {
        // Buscamos si existe alguien con ese email y contraseña
        Usuario usuario = usuarioRepository.findByEmailAndPassword(
            credenciales.getEmail(),
            credenciales.getPassword()
        );

        if (usuario != null) {
            //Exitoso, devolvemos todo el objeto
            return ResponseEntity.ok(usuario);
        }else{
            return ResponseEntity.status(401).body("Error: Credenciales incorrectas.");
        }
    }

    //--- ENDPOINT PARA ACTUALIZAR PERFIL (Carta de Artista) ---
    //URL: PUT http://localhost:8080/api/usuarios/actualizar?idUsuario=1
    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarPerfil(@RequestParam Long idUsuario, @RequestBody Usuario datosNuevos) {
        
        //Buscamos al usuario existente
        Usuario usuarioExistente = usuarioRepository.findById(idUsuario).orElse(null);

        if (usuarioExistente == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado.");
        }

        //Acualizamos SOLO los datos artisticos (sin tocar email y password)
        //Verificamos que no sean nulos para no borrar info por error
        if (datosNuevos.getInstrumento() != null)
            usuarioExistente.setNombreArtistico(datosNuevos.getNombreArtistico());

        if (datosNuevos.getInstrumento() != null)
            usuarioExistente.setInstrumento(datosNuevos.getInstrumento());

        if (datosNuevos.getDescripcion() != null)
            usuarioExistente.setDescripcion(datosNuevos.getDescripcion());

        if(datosNuevos.getCiudad() != null)
            usuarioExistente.setCiudad(datosNuevos.getCiudad());

        //Guardamos los cambios en la bd
        usuarioRepository.save(usuarioExistente);

        return ResponseEntity.ok(usuarioExistente);
    }

    //---ENDPOINT PARA LISTAR TODOS LOS USUARIOS---
    // URL: GET http://localhost:8080/api/usuarios
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }


//---ENDPOINT PARA SUBIR FOTO DE PERFIL---
    //URL: POST http://localhost:8080/api/usuarios/subir-foto?idUsuario=1
    @PostMapping("/subir-foto")
    public ResponseEntity<?> subirFoto(@RequestParam("archivo") MultipartFile archivo, @RequestParam Long idUsuario) {
        
        try {
            //  Buscamos al usuario
            Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Error: Usuario no encontrado.");
            }

            //  Preparamos la carpeta 
            String carpeta = "uploads";
            Path rutaCarpeta = Paths.get(carpeta);
            
            if (!Files.exists(rutaCarpeta)) { 
                Files.createDirectories(rutaCarpeta);
            }

            //  Generamos nombre y ruta
            String nombreArchivo = idUsuario + "_" + archivo.getOriginalFilename();
            Path rutaCompleta = rutaCarpeta.resolve(nombreArchivo);

            //  Guardamos archivo físico
            Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

            //  Actualizamos BD (
            usuario.setFotoUrl(nombreArchivo); 
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Foto subida exitosamente: " + nombreArchivo);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al subir la imagen: " + e.getMessage());
        }
    }
}
