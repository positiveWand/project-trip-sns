package com.positivewand.tourin.domain.user;

import com.positivewand.tourin.domain.tourspot.TourSpotReviewLikeRepository;
import com.positivewand.tourin.domain.tourspot.TourSpotReviewRepository;
import com.positivewand.tourin.domain.user.dto.UserDto;
import com.positivewand.tourin.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UserDto findUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchElementException("등록된 회원이 없습니다.");
        }

        return UserDto.create(user.get());
    }

    public Page<UserDto> findUsers(int page, int size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size, Sort.by("username").ascending()));

        return users.map(UserDto::create);
    }

    public Page<UserDto> findUsers(String query, int page, int size) {
        Page<User> users = userRepository.findByUsernameContaining(query, PageRequest.of(page, size, Sort.by("username").ascending()));

        return users.map(UserDto::create);
    }

    @Transactional
    public void createUser(String username, String password, String name, String email) {
        Optional<User> duplicateUser = userRepository.findByUsername(username);

        if(duplicateUser.isPresent()) {
            throw new IllegalStateException("이미 가입된 회원이 있습니다.");
        }

        User user = User.create(username, passwordEncoder.encode(password), name, email);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(String username, String name, String email) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchElementException("등록된 회원이 없습니다.");
        }

        user.get().updateProfile(name, email);
        userRepository.save(user.get());
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchElementException("등록된 회원이 없습니다.");
        }
        
        if(!passwordEncoder.matches(oldPassword, user.get().getPassword())) {
            throw new BadCredentialsException("기존 비밀번호가 일치하지 않습니다.");
        }

        user.get().changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user.get());
    }

    @Transactional
    public void deleteUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new NoSuchElementException("등록된 회원이 없습니다.");
        }
        
        // 회원 관련 데이터(관광지 후기 좋아요, 관광지 후기, 북마크)와 회원 삭제
        tourSpotReviewLikeRepository.deleteByUserId(user.get().getId());
        tourSpotReviewRepository.deleteByUser_Id(user.get().getId());
        bookmarkRepository.deleteByUserId(user.get().getId());

        userRepository.delete(user.get());
    }
}
