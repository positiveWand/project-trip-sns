package com.positivewand.tourin.domain.user;

import com.positivewand.tourin.domain.tourspot.TourSpotRepository;
import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.user.UserRepository;
import com.positivewand.tourin.domain.user.dto.BookmarkDto;
import com.positivewand.tourin.domain.user.entity.Bookmark;
import com.positivewand.tourin.domain.user.entity.BookmarkId;
import com.positivewand.tourin.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final UserRepository userRepository;
    private final TourSpotRepository tourSpotRepository;
    private final BookmarkRepository bookmarkRepository;

    public Page<BookmarkDto> findBookmarks(String username, int page, int size) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchElementException("등록된 회원이 없습니다.");
        }

        Page<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.get().getId(), PageRequest.of(page, size));

        return bookmarks.map(BookmarkDto::createFromBookmark);
    }

    public BookmarkDto findBookmark(String username, Long tourSpotId) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchElementException("등록된 회원이 없습니다.");
        }

        Optional<Bookmark> bookmark = bookmarkRepository.findById(new BookmarkId(user.get().getId(), tourSpotId));

        if(bookmark.isEmpty()) {
            throw new NoSuchElementException("관광지가 북마크에 존재하지 않습니다.");
        }

        return BookmarkDto.createFromBookmark(bookmark.get());
    }

    @Transactional
    public void addBookmark(String username, Long tourSpotId) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchElementException("등록된 회원이 없습니다.");
        }

        Optional<TourSpot> tourSpot = tourSpotRepository.findById(tourSpotId);

        if(tourSpot.isEmpty()) {
            throw new NoSuchElementException("등록된 관광지가 없습니다.");
        }

        Bookmark bookmark = new Bookmark(user.get(), tourSpot.get());
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void deleteBookmark(String username, Long tourSpotId) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchElementException("등록된 회원이 없습니다.");
        }

        Optional<Bookmark> bookmark = bookmarkRepository.findById(new BookmarkId(user.get().getId(), tourSpotId));

        bookmark.ifPresent(bookmarkRepository::delete);
    }
}
