package com.positivewand.tourin.domain.user;

import com.positivewand.tourin.domain.tourspot.TourSpotReviewLikeRepository;
import com.positivewand.tourin.domain.tourspot.TourSpotReviewRepository;
import com.positivewand.tourin.domain.user.dto.UserDto;
import com.positivewand.tourin.domain.user.entity.User;
import com.positivewand.tourin.domain.user.exception.DuplicateUserException;
import com.positivewand.tourin.domain.user.exception.NoSuchUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TourSpotReviewRepository tourSpotReviewRepository;
    private final TourSpotReviewLikeRepository tourSpotReviewLikeRepository;

    public UserDto findUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchUserException("등록된 회원이 없습니다.");
        }

        return UserDto.createFromUser(user.get());
    }

    public Page<UserDto> findUsers(int page, int size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size, Sort.by("username").ascending()));

        return users.map(UserDto::createFromUser);
    }

    public Page<UserDto> findUsers(String query, int page, int size) {
        Page<User> users = userRepository.findByUsernameContaining(query, PageRequest.of(page, size, Sort.by("username").ascending()));

        return users.map(UserDto::createFromUser);
    }

    @Transactional
    public void createUser(String username, String password, String name, String email) {
        Optional<User> duplicateUser = userRepository.findByUsername(username);

        if(duplicateUser.isPresent()) {
            throw new DuplicateUserException("이미 가입된 회원이 있습니다.");
        }

        User user = User.createUser(username, password, name, email);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(String username, String name, String email) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchUserException("등록된 회원이 없습니다.");
        }

        user.get().updateProfile(name, email);
        userRepository.save(user.get());
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchUserException("등록된 회원이 없습니다.");
        }

        user.get().changePassword(newPassword);
        userRepository.save(user.get());
    }

    @Transactional
    public void deleteUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchUserException("등록된 회원이 없습니다.");
        }

        // 회원의 관광지 후기 좋아요 삭제
        tourSpotReviewLikeRepository.deleteByUserId(user.get().getId());
        
        // 회원의 관광지 후기 삭제
        tourSpotReviewRepository.deleteByUser_Id(user.get().getId());

        // 회원의 북마크 삭제
        bookmarkRepository.deleteByUserId(user.get().getId());

        // 회원 삭제
        userRepository.delete(user.get());
    }
}
