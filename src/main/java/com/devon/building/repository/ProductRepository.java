package com.devon.building.repository;

import com.devon.building.form.ProductForm;
import com.devon.building.model.ProductInfo;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.entity.BuildingEntity;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;

@Transactional
@RequiredArgsConstructor
@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public BuildingEntity findProduct(Long id) {
        try {
            String sql = "Select e from " + BuildingEntity.class.getName() + " e Where e.id =:id ";
            Query query = entityManager.createQuery(sql, BuildingEntity.class);
            query.setParameter("id", id);
            return (BuildingEntity) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ProductInfo findProductInfo(Long id) {
        BuildingEntity building = this.findProduct(id);
        if (building == null) {
            return null;
        }
        return new ProductInfo(building.getId(), building.getName(), building.getRentPrice(), building.getStreet(), building.getWard(), building.getDistrict());
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(ProductForm productForm) {
        Long id = productForm.getId();

        BuildingEntity building = null;

        boolean isNew = false;
        if (id != null) {
            building = this.findProduct(id);
        }
        if (building == null) {
            isNew = true;
            building = new BuildingEntity();
            building.setCreateDate(new Date());
        }
        building.setId(id);
        building.setName(productForm.getName());
        building.setRentPrice((long) productForm.getPrice());

        if (productForm.getFileData() != null) {
            byte[] image = null;
            try {
                image = productForm.getFileData().getBytes();
            } catch (IOException e) {
            }
            if (image != null && image.length > 0) {
                building.setImage(image);
            }
        }
        if (isNew) {
            entityManager.persist(building);
        }
        // If error in DB, Exceptions will be thrown out immediately
        entityManager.flush();
    }

    @Transactional(readOnly = true)
    public PaginationResult<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage, String likeName) {

        // MAIN QUERY
        String sql = "Select new " + ProductInfo.class.getName() + "(p.id, p.name, p.price, p.street, p.ward, p.district) from " + BuildingEntity.class.getName() + " p ";

        // COUNT QUERY
        String countSql = "Select count(p) from " + BuildingEntity.class.getName() + " p ";

        boolean hasLike = likeName != null && !likeName.isEmpty();

        if (hasLike) {
            sql += " where lower(p.name) like :likeName ";
            countSql += " where lower(p.name) like :likeName ";
        }

        sql += " order by p.createDate desc ";

        // Tạo TypedQuery
        TypedQuery<ProductInfo> query = entityManager.createQuery(sql, ProductInfo.class);

        // Tạo CountQuery
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);
        if (hasLike) {
            String v = "%" + likeName.toLowerCase() + "%";
            query.setParameter("likeName", v);
            countQuery.setParameter("likeName", v);
        }

        // Trả về phân trang
        return new PaginationResult<>(query, countQuery, page, maxResult, maxNavigationPage);
    }

    @Transactional(readOnly = true)
    public PaginationResult<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage) {
        return queryProducts(page, maxResult, maxNavigationPage, null);
    }

}
