package app.nss.webchat.repository;

import app.nss.webchat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    User findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);

    List<User> findAllChatRoomsById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
