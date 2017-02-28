package se.vgregion.handbok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.vgregion.handbok.model.Ifeed;

public interface IfeedRepository extends JpaRepository<Ifeed,Long> {

    Ifeed findById(String id);
}