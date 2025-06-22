package pl.sejdii.example.adapter.out.postgres.participant;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ReservationParticipantRepository
    extends JpaRepository<ReservationParticipantEntity, Integer> {

  Optional<ReservationParticipantEntity> findByIdentifier(String value);
}
