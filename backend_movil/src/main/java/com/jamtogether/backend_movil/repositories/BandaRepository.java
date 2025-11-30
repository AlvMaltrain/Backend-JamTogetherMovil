package com.jamtogether.backend_movil.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jamtogether.backend_movil.models.Banda;

public interface BandaRepository extends JpaRepository<Banda, Long> {
    //Buscar bandas por género
    List<Banda> findByGenero(String genero);

}
