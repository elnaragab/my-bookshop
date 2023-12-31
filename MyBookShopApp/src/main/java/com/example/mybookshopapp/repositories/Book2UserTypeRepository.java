package com.example.mybookshopapp.repositories;

import com.example.mybookshopapp.model.book.links.Book2UserTypeEntity;
import com.example.mybookshopapp.model.enums.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Book2UserTypeRepository extends JpaRepository<Book2UserTypeEntity, Integer> {
    @Query("select b from Book2UserTypeEntity b where b.code = ?1")
    Book2UserTypeEntity findByCode(StatusType code);


}
