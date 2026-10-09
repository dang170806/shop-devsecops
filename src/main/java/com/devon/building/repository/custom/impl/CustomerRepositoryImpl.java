package com.devon.building.repository.custom.impl;

import com.devon.building.model.builder.CustomerSearchBuilder;
import com.devon.building.repository.custom.CustomerRepositoryCustom;
import com.devon.building.repository.entity.CustomerEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional(readOnly = true)
public class CustomerRepositoryImpl implements CustomerRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<CustomerEntity> searchCustomers(CustomerSearchBuilder builder, Pageable pageable) {
        Query dataQuery = buildSearchQuery(builder, false);
        dataQuery.setFirstResult((int) Math.min(pageable.getOffset(), Integer.MAX_VALUE));
        dataQuery.setMaxResults(pageable.getPageSize());
        @SuppressWarnings("unchecked")
        List<CustomerEntity> customers = dataQuery.getResultList();

        Number total = (Number) buildSearchQuery(builder, true).getSingleResult();
        return new PageImpl<>(customers, pageable, total.longValue());
    }

    private Query buildSearchQuery(CustomerSearchBuilder builder, boolean count) {
        StringBuilder sql = new StringBuilder(count
                ? "SELECT COUNT(DISTINCT c.id) FROM customer c"
                : "SELECT DISTINCT c.* FROM customer c");
        if (builder.getStaffId() != null) {
            sql.append(" JOIN assignmentcustomer ac ON ac.customerid = c.id");
        }
        sql.append(" WHERE c.is_active = 1");

        Map<String, Object> parameters = new HashMap<>();
        addLikeFilter(sql, parameters, "c.fullname", "fullName", builder.getFullName());
        addLikeFilter(sql, parameters, "c.email", "email", builder.getEmail());
        addLikeFilter(sql, parameters, "c.phone", "phone", builder.getPhone());

        if (builder.getStatus() != null) {
            sql.append(" AND c.status = :status");
            parameters.put("status", builder.getStatus().name());
        }
        if (builder.getStaffId() != null) {
            sql.append(" AND ac.staffid = :staffId");
            parameters.put("staffId", builder.getStaffId());
        }

        if (!count) {
            sql.append(" ORDER BY c.createddate DESC, c.fullname, c.id");
        }
        Query query = count
                ? entityManager.createNativeQuery(sql.toString())
                : entityManager.createNativeQuery(sql.toString(), CustomerEntity.class);
        parameters.forEach(query::setParameter);
        return query;
    }

    private void addLikeFilter(StringBuilder sql, Map<String, Object> parameters,
                               String column, String parameterName, String value) {
        if (value != null && !value.isBlank()) {
            sql.append(" AND LOWER(").append(column).append(") LIKE :").append(parameterName);
            parameters.put(parameterName, "%" + value.trim().toLowerCase() + "%");
        }
    }
}
