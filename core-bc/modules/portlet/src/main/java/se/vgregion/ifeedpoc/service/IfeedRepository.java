package se.vgregion.ifeedpoc.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.vgregion.ifeedpoc.model.Ifeed;
import se.vgregion.ifeedpoc.model.IfeedList;

import java.util.List;

public interface IfeedRepository extends JpaRepository<Ifeed,Long> {

}