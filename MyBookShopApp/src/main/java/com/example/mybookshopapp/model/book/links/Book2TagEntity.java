package com.example.mybookshopapp.model.book.links;

import com.example.mybookshopapp.model.book.BookEntity;
import com.example.mybookshopapp.model.book.tags.TagEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "book2tag")
@Getter
@Setter
public class Book2TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(cascade = CascadeType.ALL)
    private BookEntity book;
    @ManyToOne(cascade = CascadeType.ALL)
    private TagEntity tag;
}
