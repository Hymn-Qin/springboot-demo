package com.example.demo.model;

//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;

//public interface UserRepository extends JpaRepository<User, Long> {
//
//    User findByUsername(String username);
//
//    User findByUsernameOrEmail(String username, String email);
//
//    Page<User> findAll(Pageable pageable);
//
//    Page<User> findByUsername(String username, Pageable pageable);
//
//    //自定义SQL查询
//    @Modifying
//    @Query("UPDATE User u set u.username = ?1 where u.id = ?2")
//    int modifyByIdAndUserId(String username, Long id);
//}
