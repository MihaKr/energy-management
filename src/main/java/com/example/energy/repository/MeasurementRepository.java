package com.example.energy.repository;

import com.example.energy.entity.Device;
import com.example.energy.entity.Measurement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class MeasurementRepository {
    @Inject
    EntityManager entityManager;

    public Measurement createMeasurement(Measurement measurement) {
        entityManager.persist(measurement);
        return measurement;
    }

    public Measurement getMeasurement(Integer measurementId) {
        Measurement measurement = entityManager.find(Measurement.class, measurementId);
        if (measurement == null) {
            throw new EntityNotFoundException("Building with id " + measurementId + " not found");
        }
        return measurement;
    }

    public Measurement updateMeasurement(Measurement measurement) {
        entityManager.merge(measurement);
        return measurement;
    }

    public Measurement deleteMeasurement(Integer measurementId) {
        Measurement measurement = entityManager.find(Measurement.class, measurementId);
        if (measurement != null) {
            entityManager.remove(measurement);
            entityManager.flush();
            return measurement;
        }
        return null;
    }

    public List<Measurement> getAllMeasurements(int limit, int offset, String orderDirection) {
        return entityManager.createQuery("SELECT m FROM Measurement m order by m.id " +
                (orderDirection != null && orderDirection.equalsIgnoreCase("desc") ? "DESC" : "ASC")
                , Measurement.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Measurement> findByBuildingIdAndTimestamp(Integer buildingId, Instant from, Instant to) {
        return entityManager.createQuery(
                "SELECT m FROM Measurement m " +
                   "WHERE m.building.id = :buildingId " +
                   "AND m.timestamp >= :from " +
                   "AND m.timestamp <= :to",
                        Measurement.class)
                .setParameter("buildingId", buildingId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    public List<Measurement> findByDeviceIdAndTimestamp(Integer deviceId, Instant from, Instant to) {
        return entityManager.createQuery(
                        "SELECT m FROM Measurement m " +
                                "WHERE m.device.id = :deviceId " +
                                "AND m.timestamp >= :from " +
                                "AND m.timestamp <= :to",
                        Measurement.class)
                .setParameter("deviceId", deviceId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}
