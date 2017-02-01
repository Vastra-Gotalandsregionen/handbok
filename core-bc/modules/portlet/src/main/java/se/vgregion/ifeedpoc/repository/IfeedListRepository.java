package se.vgregion.ifeedpoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.vgregion.ifeedpoc.model.IfeedList;

public interface IfeedListRepository extends JpaRepository<IfeedList,Long> {

    @Query("select u from #{#entityName} u where u.name = ?1")
    IfeedList findByName(String name);
}