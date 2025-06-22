package pl.sejdii.example.adapter.out.postgres.room;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface RoomRepository extends JpaRepository<RoomEntity, Integer> {

  @Query(
      """
            select r from RoomEntity r
            left join fetch r.activeReservations
            where r.identifier = :identifier
            """)
  Optional<RoomEntity> findByIdentifier(String identifier);
}
