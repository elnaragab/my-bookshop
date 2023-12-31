package com.example.mybookshopapp.repositories;

import com.example.mybookshopapp.model.book.BookEntity;
import com.example.mybookshopapp.model.book.RatingEntity;
import com.example.mybookshopapp.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Integer> {
    @Transactional
    @Modifying
    @Query("update RatingEntity r set r.value = ?1 where r.user = ?2 and r.book = ?3")
    void updateValueByUserAndBook(Integer value, UserEntity user, BookEntity book);

    boolean existsByUserAndBook(UserEntity user, BookEntity book);

    @Query("select case when r.value is null then 0 else  r.value end " +
            "from RatingEntity r " +
            "where r.book.slug like ?1 and r.user = ?2")
    Integer getRatingByBookSlugAndUser(String slug, UserEntity user);
}
