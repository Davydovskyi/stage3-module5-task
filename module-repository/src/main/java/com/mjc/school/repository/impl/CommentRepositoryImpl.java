package com.mjc.school.repository.impl;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.model.Comment;
import com.mjc.school.repository.model.Comment_;
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
public class CommentRepositoryImpl implements CommentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Comment> readAll(Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> criteriaQuery = criteriaBuilder.createQuery(Comment.class);
        Root<Comment> root = criteriaQuery.from(Comment.class);
        root.fetch(Comment_.news);

        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> order.isAscending() ?
                            criteriaBuilder.asc(root.get(order.getProperty())) :
                            criteriaBuilder.desc(root.get(order.getProperty())))
                    .toList();
            criteriaQuery.orderBy(orders);
        }

        criteriaQuery.select(root);
        TypedQuery<Comment> typedQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize());

        return typedQuery.getResultList();
    }

    @Override
    public Optional<Comment> readById(Long id) {
        return Optional.ofNullable(entityManager.find(Comment.class, id));
    }

    @Override
    public Comment create(Comment entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Comment update(Comment entity) {
        entity.preUpdate();
        return entityManager.createQuery("""
                        UPDATE Comment c SET c.content = :content,
                        c.lastUpdatedDate = :lastUpdatedDate
                        WHERE c.id = :id
                        """)
                .setParameter("content", entity.getContent())
                .setParameter("lastUpdatedDate", entity.getLastUpdatedDate())
                .setParameter("id", entity.getId())
                .executeUpdate() > 0 ? entityManager.find(Comment.class, entity.getId()) : null;
    }

    @Override
    public boolean deleteById(Long id) {
        return entityManager.createQuery("DELETE FROM Comment c WHERE c.id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    @Override
    public List<Comment> readAllByNewsId(Long id) {
        return entityManager.createQuery("SELECT c FROM Comment c WHERE c.news.id = :id", Comment.class)
                .setParameter("id", id)
                .getResultList();
    }
}
