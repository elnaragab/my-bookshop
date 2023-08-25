package com.example.MyBookShopApp.services.util;

import com.example.MyBookShopApp.dto.book.request.RequestDto;
import com.example.MyBookShopApp.model.book.BookEntity;
import com.example.MyBookShopApp.model.book.links.Book2UserEntity;
import com.example.MyBookShopApp.model.book.links.Book2UserTypeEntity;
import com.example.MyBookShopApp.model.enums.StatusType;
import com.example.MyBookShopApp.model.user.UserEntity;
import com.example.MyBookShopApp.repositories.Book2UserRepository;
import com.example.MyBookShopApp.repositories.Book2UserTypeRepository;
import com.example.MyBookShopApp.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CookieUtils {
    public final String PATH = "/";
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public static final String DELIMITER = "/";
    private final Book2UserTypeRepository book2UserTypeRepository;
    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;


    public Cookie createTokenCookie(String value, int age) {
        Cookie cookie = new Cookie("token", value);
        cookie.setMaxAge(age);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath(PATH);
        return cookie;
    }

    public void addBook2Cookie(RequestDto requestDto) {
        String name = requestDto.getStatus().getCookieName();
        Cookie[] cookies = request.getCookies();
        Cookie cookie;
        if (cookies == null) {
            cookie = new Cookie(name, null);
        } else {
            cookie = getCookieByName(cookies, name);
        }
        StringJoiner sj = new StringJoiner(DELIMITER);
        if (cookie != null && !StringUtils.isEmpty(cookie.getValue())) {
            sj.add(cookie.getValue());
        }
        if (!sj.toString().contains(requestDto.getSlug())) {
            sj.add(requestDto.getSlug());
        } else {
            removeBookFromCookie(requestDto);
            return;
        }
        removeBookFromAnotherCookieIfExists(requestDto);
        Cookie newCookie = new Cookie(name, sj.toString());

        newCookie.setPath(PATH);
        response.addCookie(newCookie);
    }

    private void removeBookFromAnotherCookieIfExists(RequestDto request) {
        switch (request.getStatus()) {
            case CART:
                request.setStatus(StatusType.KEPT);
                removeBookFromCookie(request);
                break;
            case KEPT:
                request.setStatus(StatusType.CART);
                removeBookFromCookie(request);
                break;
        }
    }


    public void removeBookFromCookie(RequestDto requestDto) {
        String name = requestDto.getStatus().getCookieName();
        Cookie cookie;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        } else {
            cookie = getCookieByName(cookies, name);
        }
        if (cookie == null || StringUtils.isEmpty(cookie.getValue())) {
            return;
        }
        String newCookie = Arrays.stream(cookie.getValue().split(DELIMITER))
                .filter(b -> !b.equals(requestDto.getSlug()))
                .collect(Collectors.joining(DELIMITER));
        cookie.setValue(newCookie);
        cookie.setPath(PATH);
        response.addCookie(cookie);
    }

    public List<String> getBookSlugsByStatus(StatusType status) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        Cookie cookie = getCookieByName(
                cookies,
                status.getCookieName()
        );
        if (cookie == null || StringUtils.isEmpty(cookie.getValue())) {
            return null;
        }
        return Arrays.stream(cookie.getValue().split(DELIMITER))
                .collect(Collectors.toList());
    }

    public Cookie getCookieByName(
            Cookie[] cookies,
            String name
    ) {
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<String> getSlugsFromCookie(StatusType statusType) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return new ArrayList<>();
        }

        Cookie cookie = getCookieByName(cookies, statusType.getCookieName());
        if (cookie == null || StringUtils.isEmpty(cookie.getValue())) {
            return new ArrayList<>();
        }
        return Arrays.stream(cookie.getValue().split(DELIMITER)).collect(Collectors.toList());
    }

    @Transactional
    public void mergeSelectedBooks(StatusType statusType, UserEntity user) {
        List<String> slugs = getSlugsFromCookie(statusType);
        Book2UserTypeEntity typeEntity = book2UserTypeRepository.findByCode(statusType);
        List<BookEntity> books = null;
        if (!slugs.isEmpty()) {
            books = bookRepository.findKeptOrCartBooksBySlugIn(slugs, user.getEmail());
        }
        if (CollectionUtils.isEmpty(books)) {
            return;
        }
        List<Book2UserEntity> links = new ArrayList<>();
        books.forEach(b -> {
            if (book2UserRepository.existsByUserAndBook(user, b)) {
                book2UserRepository.updateTypeByUserAndBook(typeEntity, user, b);
            } else {
                links.add(Book2UserEntity.builder()
                        .user(user)
                        .book(b)
                        .time(LocalDateTime.now())
                        .type(typeEntity).build());
            }
        });
        if (CollectionUtils.isNotEmpty(links)) {
            book2UserRepository.saveAll(links);
        }
    }
}
