package com.mjc.school.repository.impl;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.model.Author;
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
public class AuthorRepositoryImpl implements AuthorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Author> readAll(Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Author> criteriaQuery = criteriaBuilder.createQuery(Author.class);
        Root<Author> root = criteriaQuery.from(Author.class);

        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> order.isAscending() ?
                            criteriaBuilder.asc(root.get(order.getProperty())) :
                            criteriaBuilder.desc(root.get(order.getProperty())))
                    .toList();
            criteriaQuery.orderBy(orders);
        }

        criteriaQuery.select(root);
        TypedQuery<Author> typedQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize());

        return typedQuery.getResultList();
    }

    @Override
    public Optional<Author> readById(Long id) {
        return Optional.ofNullable(entityManager.find(Author.class, id));
    }

    @Override
    public Optional<Author> readByNewsId(Long id) {
        return entityManager.createQuery("""
                        SELECT DISTINCT a FROM Author a
                        JOIN a.news n
                        WHERE n.id = :id""", Author.class)
                .setParameter("id", id)
                .getResultList().stream()
                .findAny();
    }

    @Override
    public Author create(Author entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Author update(Author entity) {
        entity.preUpdate();
        return entityManager.createQuery("UPDATE Author a SET a.name = :name," +
                        "a.lastUpdatedDate = :lastUpdatedDate " +
                        "WHERE a.id = :id")
                .setParameter("name", entity.getName())
                .setParameter("lastUpdatedDate", entity.getLastUpdatedDate())
                .setParameter("id", entity.getId())
                .executeUpdate() > 0 ? entity : null;
    }

    @Override
    public boolean deleteById(Long id) {
        return entityManager.createQuery("DELETE FROM Author a WHERE a.id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }
}