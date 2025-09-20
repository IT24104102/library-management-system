package lk.sliit.lms.reservations;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByBookId(Long bookId);
    Optional<Reservation> findByUserIdAndBookId(Long userId, Long bookId);
}
