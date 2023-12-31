package com.example.mybookshopapp.dto.book;

public interface ShortBookDtoProjection {
    String getSlug();

    String getImage();

    String getTitle();

    Integer getDiscount();

    Boolean getIsBestseller();

    Integer getPrice();

    Integer getDiscountPrice();

    String getAuthors();
}
