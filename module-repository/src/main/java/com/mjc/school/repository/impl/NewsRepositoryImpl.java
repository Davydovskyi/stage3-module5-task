package com.mjc.school.repository.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class NewsRepositoryImpl implements NewsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<News> readAll(Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<News> criteriaQuery = criteriaBuilder.createQuery(News.class);
        Root<News> root = criteriaQuery.from(News.class);
        root.fetch(News_.author);

        if (pageable.getSort().isSorted()) {
            criteriaQuery.orderBy(getOrders(pageable, criteriaBuilder, root));
        }

        criteriaQuery.select(root);
        TypedQuery<News> typedQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize());

        return typedQuery.getResultList();
    }

    @Override
    public List<News> readAllByFilter(NewsSearchQueryParam filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<News> criteriaQuery = cb.createQuery(News.class);
        Root<News> news = criteriaQuery.from(News.class);

        List<Predicate> predicates = getPredicates(filter, cb, news);

        criteriaQuery.select(news)
                .where(predicates.toArray(Predicate[]::new));

        if (pageable.getSort().isSorted()) {
            criteriaQuery.orderBy(getOrders(pageable, cb, news));
        }

        TypedQuery<News> typedQuery = entityManager.createQuery(criteriaQuery)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize());

        return typedQuery.getResultList();
    }

    private List<Predicate> getPredicates(NewsSearchQueryParam filter, CriteriaBuilder cb, Root<News> news) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getContent() != null) {
            predicates.add(cb.like(cb.lower(news.get(News_.content)), "%" + filter.getContent().toLowerCase() + "%"));
        }

        if (filter.getTitle() != null) {
            predicates.add(cb.like(cb.lower(news.get(News_.title)), "%" + filter.getTitle().toLowerCase() + "%"));
        }

        if (filter.getAuthorName() != null) {
            Join<News, Author> author = news.join(News_.author);
            news.fetch(News_.author);
            predicates.add(cb.like(cb.lower(author.get(Author_.name)), "%" + filter.getAuthorName().toLowerCase() + "%"));
        }

        Join<Object, Object> tag = news.join(News_.TAGS);
        news.fetch(News_.tags);

        if (!filter.getTagIds().isEmpty()) {
            predicates.add(tag.get(Tag_.ID).in(filter.getTagIds()));
        }

        if (!filter.getTagNames().isEmpty()) {
            predicates.add(tag.get(Tag_.NAME).in(filter.getTagNames()));
        }

        return predicates;
    }

    @Override
    public Optional<News> readById(Long newsId) {
        return Optional.ofNullable(entityManager.find(News.class, newsId));
    }

    @Override
    public News create(News model) {
        entityManager.persist(model);
        return model;
    }

    @Override
    public News update(News model) {
        model.preUpdate();
        return entityManager.createQuery("""
                        UPDATE News n SET n.title = :title,
                        n.content = :content,
                        n.author = :author,
                        n.lastUpdatedDate = :lastUpdatedDate
                        WHERE n.id = :id
                        """)
                .setParameter("title", model.getTitle())
                .setParameter("content", model.getContent())
                .setParameter("lastUpdatedDate", model.getLastUpdatedDate())
                .setParameter("author", model.getAuthor())
                .setParameter("id", model.getId())
                .executeUpdate() > 0 ? model : null;
    }

    @Override
    public boolean deleteById(Long newsId) {
        return entityManager.createQuery("DELETE FROM News n WHERE n.id = :id")
                .setParameter("id", newsId)
                .executeUpdate() > 0;
    }

    private List<Order> getOrders(Pageable pageable, CriteriaBuilder criteriaBuilder, Root<News> root) {
        return pageable.getSort().stream()
                .map(order -> order.isAscending() ?
                        criteriaBuilder.asc(root.get(order.getProperty())) :
                        criteriaBuilder.desc(root.get(order.getProperty())))
                .toList();
    }
}