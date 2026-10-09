package com.devon.building.repository.custom.impl;

import com.devon.building.model.builder.BuildingSearchBuilder;
import com.devon.building.repository.custom.BuildingRepositoryCustom;
import com.devon.building.repository.entity.BuildingEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class BuildingRepositoryImpl implements BuildingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private void buildJoinQuery(StringBuilder sql1, BuildingSearchBuilder builder) {
        Long staffId = builder.getStaffId();
        if (staffId != null) {
            sql1.append(" JOIN assignmentbuilding a ON a.buildingid = b.id");
        }
        Long getRentAreaFrom = builder.getRentAreaFrom();
        Long getRentAreaTo = builder.getRentAreaTo();
        if (getRentAreaFrom != null || getRentAreaTo != null) {
            sql1.append(" JOIN rentarea r ON r.buildingid = b.id");
        }
        sql1.append(" WHERE 1 = 1");
    }

    private void buildSearchQuery(StringBuilder sql, BuildingSearchBuilder builder,
                                  Map<String, Object> parameters) {
        addLikeFilter(sql, parameters, "b.name", "name", builder.getName());
        addExactFilter(sql, parameters, "b.floorarea", "floorArea", builder.getFloorArea());
        addLikeFilter(sql, parameters, "b.district", "district", builder.getDistrictId());
        addLikeFilter(sql, parameters, "b.ward", "ward", builder.getWard());
        addLikeFilter(sql, parameters, "b.street", "street", builder.getStreet());
        addExactFilter(sql, parameters, "b.numberofbasement", "numberOfBasement", builder.getNumberOfBasement());
        addLikeFilter(sql, parameters, "b.direction", "direction", builder.getDirection());
        addLikeFilter(sql, parameters, "b.level", "level", builder.getLevel());
        addLikeFilter(sql, parameters, "b.managername", "managerName", builder.getManagerName());
        addLikeFilter(sql, parameters, "b.managerphone", "managerPhone", builder.getManagerPhone());

        if (builder.getStaffId() != null) {
            sql.append(" AND a.staffid = :staffId");
            parameters.put("staffId", builder.getStaffId());
        }
        if (builder.getRentAreaFrom() != null) {
            sql.append(" AND r.value >= :rentAreaFrom");
            parameters.put("rentAreaFrom", builder.getRentAreaFrom());
        }
        if (builder.getRentAreaTo() != null) {
            sql.append(" AND r.value <= :rentAreaTo");
            parameters.put("rentAreaTo", builder.getRentAreaTo());
        }
        if (builder.getRentPriceFrom() != null) {
            sql.append(" AND b.rentprice >= :rentPriceFrom");
            parameters.put("rentPriceFrom", builder.getRentPriceFrom());
        }
        if (builder.getRentPriceTo() != null) {
            sql.append(" AND b.rentprice <= :rentPriceTo");
            parameters.put("rentPriceTo", builder.getRentPriceTo());
        }
        if (builder.getType() != null && !builder.getType().isEmpty()) {
            sql.append(" AND (");
            for (int i = 0; i < builder.getType().size(); i++) {
                if (i > 0) sql.append(" OR ");
                String parameterName = "type" + i;
                sql.append("b.type LIKE :").append(parameterName);
                parameters.put(parameterName, "%" + builder.getType().get(i) + "%");
            }
            sql.append(")");
        }
    }

    private void addLikeFilter(StringBuilder sql, Map<String, Object> parameters,
                               String column, String parameterName, String value) {
        if (value != null && !value.isBlank()) {
            sql.append(" AND LOWER(").append(column).append(") LIKE :").append(parameterName);
            parameters.put(parameterName, "%" + value.trim().toLowerCase() + "%");
        }
    }

    private void addExactFilter(StringBuilder sql, Map<String, Object> parameters,
                                String column, String parameterName, Long value) {
        if (value != null) {
            sql.append(" AND ").append(column).append(" = :").append(parameterName);
            parameters.put(parameterName, value);
        }
    }

    public Page<BuildingEntity> searchBuilding(BuildingSearchBuilder builder, Pageable pageable) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder dataSql = new StringBuilder("SELECT DISTINCT b.* FROM building b");
        buildJoinQuery(dataSql, builder);
        buildSearchQuery(dataSql, builder, parameters);
        dataSql.append(" ORDER BY b.id ASC");

        Query dataQuery = entityManager.createNativeQuery(dataSql.toString(), BuildingEntity.class);
        parameters.forEach(dataQuery::setParameter);
        int offset = (int) Math.min(pageable.getOffset(), Integer.MAX_VALUE);
        dataQuery.setFirstResult(offset);
        dataQuery.setMaxResults(pageable.getPageSize());
        List<BuildingEntity> buildings = dataQuery.getResultList();

        StringBuilder countSql = new StringBuilder("SELECT COUNT(DISTINCT b.id) FROM building b");
        buildJoinQuery(countSql, builder);
        Map<String, Object> countParameters = new HashMap<>();
        buildSearchQuery(countSql, builder, countParameters);
        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        countParameters.forEach(countQuery::setParameter);
        Number total = (Number) countQuery.getSingleResult();

        return new PageImpl<>(buildings, pageable, total.longValue());
    }

}
