package com.mjc.school.repository.impl;

import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Tag> readAll(Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
        Root<Tag> root = criteriaQuery.from(Tag.class);

        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> order.isAscending() ?
                            criteriaBuilder.asc(root.get(order.getProperty())) :
                            criteriaBuilder.desc(root.get(order.getProperty())))
                    .toList();
            criteriaQuery.orderBy(orders);
        }

        criteriaQuery.select(root);
        TypedQuery<Tag> typedQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize());

        return typedQuery.getResultList();
    }

    @Override
    public List<Tag> readAllByIds(List<Long> ids) {
        return entityManager.createQuery("SELECT t FROM Tag t WHERE t.id IN :ids", Tag.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public List<Tag> readAllByNewsId(Long id) {
        return entityManager.createQuery("""
                        SELECT t FROM Tag t
                        JOIN t.news n
                        WHERE n.id = :id""", Tag.class)
                .setParameter("id", id)
                .getResultList();
    }

    @Override
    public Optional<Tag> readById(Long id) {
        return Optional.ofNullable(entityManager.find(Tag.class, id));
    }

    @Override
    public Tag create(Tag entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Tag update(Tag entity) {
        return entityManager.merge(entity);
    }

    @Override
    public boolean deleteById(Long id) {
        return entityManager.createQuery("DELETE FROM Tag t WHERE t.id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }
}