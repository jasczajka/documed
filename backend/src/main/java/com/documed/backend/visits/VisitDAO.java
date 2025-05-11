package com.documed.backend.visits;

import com.documed.backend.FullDAO;
import com.documed.backend.visits.model.Visit;
import com.documed.backend.visits.model.VisitStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class VisitDAO implements FullDAO<Visit, Visit> {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Visit> rowMapper =
            (rs, rowNum) ->
                    Visit.builder()
                            .id(rs.getInt("id"))
                            .status(VisitStatus.valueOf(rs.getString("status")))
                            .interview(rs.getString("interview"))
                            .diagnosis(rs.getString("diagnosis"))
                            .recommendations(rs.getString("recommendations"))
                            .totalCost(rs.getBigDecimal("total_cost"))
                            .facilityId(rs.getInt("facility_id"))
                            .serviceId(rs.getInt("service_id"))
                            .patientId(rs.getInt("patient_id"))
                            .prescriptionId(rs.getInt("prescription_id"))
                            .build();

    @Override
    public Visit create(Visit creationObject) {
        return null;
    }

    @Override
    public int delete(int id) {
        return 0;
    }

    @Override
    public Optional<Visit> getById(int id) {
        String sql = "SELECT id, status, interview, diagnosis, recommendations, total_cost, facility_id, service_id, patient_information, patient_id, prescription_id FROM visit WHERE id = ?";

        List<Visit> visits = jdbcTemplate.query(sql, rowMapper, id);

        return visits.stream().findFirst();
    }

    @Override
    public List<Visit> getAll() {
        return List.of();
    }
}
