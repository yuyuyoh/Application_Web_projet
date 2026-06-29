package fr.miage.motus.partie_service.repository;

import fr.miage.motus.partie_service.entity.Essai;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EssaiRepository extends JpaRepository<Essai, Long> {

    List<Essai> findByPartieIdOrderByNumero(Long partieId);
}
