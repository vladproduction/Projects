package com.app.vp.wookiebooks.controller;


import com.app.vp.wookiebooks.dto.BookDto;
import com.app.vp.wookiebooks.dto.UserDto;
import com.app.vp.wookiebooks.exceptions.UserNotFoundException;
import com.app.vp.wookiebooks.mapper.BookMapper;
import com.app.vp.wookiebooks.mapper.UserMapper;
import com.app.vp.wookiebooks.model.Book;
import com.app.vp.wookiebooks.model.User;
import com.app.vp.wookiebooks.service.BookService;
import com.app.vp.wookiebooks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Spring REST controller handles incoming HTTP requests to RestApiController.
 * It provides endpoints for (postman):
 * -[POST] createUser
 * -[GET] getUserById
 * -[GET] findUserByAuthorPseudonym
 * -[PUT] updateAuthorPseudonym
 * -[DELETE]: deleteByUserId
 * Interacts with the following services:
 * -UserService
 * */
@RestController
@RequestMapping("/api/wookie_books")
public class RestApiController {

    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;

    //[POST]: Create User REST API
    @PostMapping("/createUser")
    public ResponseEntity<UserDto> createUser(
            @RequestBody UserDto userDto){
        User user = UserMapper.mapToUser(userDto);
        User savedUser = userService.createUser(user);
        UserDto createdUserDto = UserMapper.mapToUserDto(savedUser);
        return ok(createdUserDto);
    }

    //[GET]: User by userId REST API
    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<Optional<UserDto>> getUserById(
            @PathVariable Long userId){
        Optional<User> userOptional = userService.getUserById(userId);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            UserDto userDto = UserMapper.mapToUserDto(user);
            return ok(Optional.of(userDto));
        }
        return ResponseEntity.notFound().build();
    }

    //[GET]: User by authorPseudonym REST API
    @GetMapping("/findUserByAuthorPseudonym")
    public ResponseEntity<Optional<UserDto>> findUserByAuthorPseudonym(
            @RequestParam String authorPseudonym){
        Optional<User> userOptional = userService.findUserByAuthorPseudonym(authorPseudonym);
        if (userOptional.isPresent()){
            User user = userOptional.get();
            UserDto userDto = UserMapper.mapToUserDto(user);
            return ok(Optional.of(userDto));
        }
        return ResponseEntity.notFound().build();
    }

    //[PUT]: update User by authorPseudonym REST API
    @PutMapping("/updateAuthorPseudonym")
    public ResponseEntity<Optional<UserDto>> updateAuthorPseudonym(
            @RequestParam String authorPseudonym, @RequestParam String newPseudonym){
        Optional<User> optionalUser = userService.updateAuthorPseudonym(authorPseudonym, newPseudonym);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            UserDto userDto = UserMapper.mapToUserDto(user);
            return ok(Optional.of(userDto));
        }
        return ResponseEntity.notFound().build();
    }

    //[DELETE]: delete User by userId REST API
    @DeleteMapping("/deleteByUserId/{userId}")
    public void deleteUserByUserId(@PathVariable Long userId) throws UserNotFoundException {
        userService.deleteUserByUserId(userId);
            ResponseEntity.ok();
    }

    //-------------------------------

    //[POST]: create book
    @PostMapping("/createBook")
    public ResponseEntity<BookDto> createBook(
            @RequestBody BookDto bookDto){
        Book book = BookMapper.mapToBook(bookDto);
        UserDto bookDtoAuthor = bookDto.getAuthor();
        if(bookDtoAuthor != null){
            String authorAuthorPseudonym = bookDtoAuthor.getAuthorPseudonym();
            if(authorAuthorPseudonym != null){
                Optional<User> userOptional = userService.findUserByAuthorPseudonym(authorAuthorPseudonym);
                userOptional.ifPresent(book::setAuthor);
            }
        }
        Book savedBook = bookService.createBook(book);
        BookDto createdBookDto = BookMapper.mapToBookDto(savedBook);
        return ok(createdBookDto);
    }

}