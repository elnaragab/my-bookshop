package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.model.book.review.BookReviewLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {


    Optional<BookReviewLikeEntity> findBookReviewLikeEntityByReviewIdAndUserId(Integer reviewId, Integer userId);
}
