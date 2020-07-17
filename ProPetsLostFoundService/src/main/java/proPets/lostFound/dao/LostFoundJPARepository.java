package proPets.lostFound.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import proPets.lostFound.model.AccessCode;

public interface LostFoundJPARepository extends JpaRepository<AccessCode, String> {

}
