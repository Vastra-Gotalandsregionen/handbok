package se.vgregion.ifeedpoc.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se.vgregion.ifeedpoc.model.IfeedList;

import java.util.List;

public interface IfeedListRepository extends JpaRepository<IfeedList,Long> {

    @Query("select u from #{#entityName} u where u.id = ?1")
    List<IfeedList> findById(Long id);
}