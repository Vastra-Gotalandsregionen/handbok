package se.vgregion.ifeedpoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.vgregion.ifeedpoc.model.Ifeed;

public interface IfeedRepository extends JpaRepository<Ifeed,Long> {

    Ifeed findById(String id);
}