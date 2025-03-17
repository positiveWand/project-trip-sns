package com.positivewand.tourin.domain.user;

import com.positivewand.tourin.domain.user.entity.Bookmark;
import com.positivewand.tourin.domain.user.entity.BookmarkId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {
    Page<Bookmark> findByUserId(Long userId, Pageable pageable);

    void deleteByUserId(Long userId);
}
