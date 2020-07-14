package proPets.lostFound.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import proPets.lostFound.model.AccessCode;

public interface LostFoundJPARepository extends JpaRepository<AccessCode, String> {

	Optional <String> findIdByAccessCode (String accessCode);
}
