package proPets.lostFound.dao.postgreSQL;

import org.springframework.data.jpa.repository.JpaRepository;

import proPets.lostFound.model.accessCode.AccessCode;

public interface LostFoundPostgreSQLRepository extends JpaRepository<AccessCode, String> {

}
