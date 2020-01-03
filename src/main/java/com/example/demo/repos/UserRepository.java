package com.example.demo.repos;

import com.example.demo.model.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 参数一 T :当前需要映射的实体
 * 参数二 ID :当前映射的实体中的OID的类型
 */

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    User findByUsernameOrEmail(String username, String email);

    Page<User> findAll(Pageable pageable);

    Page<User> findByUsername(String username, Pageable pageable);

    @Query(value = "select * from tb_user where name=?", nativeQuery = true)
    List<User> queryByNameUseSQL(String name);

    //自定义SQL查询
    @Modifying //需要执行一个更新操作
    @Query("UPDATE User u set u.username = ?1 where u.id = ?2")
    int modifyByIdAndUserId(String username, Integer id);

    @Query("update User u set u.name= ?1 where u.id= ?2")
    //需要执行一个更新操作
    @Modifying
    void updateUsersNameById(String name, Integer id);

    @Transactional
    void deleteUserByUsername(String username);

    /**
     * Here we demonstrate the usage of SpEL expression within a custom query. With the
     * {@link example.springdata.jpa.security.SecurityEvaluationContextExtension} in place we can safely access auth
     * information provided by the Spring Security Context. The Spring Data Repository infrastructure will translate the
     * given query string into the parameterized form: <code>
     *     select o from BusinessObject o where o.owner.emailAddress like ?
     * </code> and set the the result SpEL expression evaluated at method invocation time as parameter value.
     *
     * @return
     */
//    @Query("select o from BusinessObject o where o.owner.emailAddress like ?#{hasRole('ROLE_ADMIN') ? '%' : principal.emailAddress}")
//    List<BusinessObject> findBusinessObjectsForCurrentUser();

    /**
     * Here we apply a dynamic filter condition in there query depending of the role of the current principal.
     *
     * @return
     */
//    @Query("select o from BusinessObject o where o.owner.id = ?#{principal.id} or 1=?#{hasRole('ROLE_ADMIN') ? 1 : 0}")
//    List<BusinessObject> findBusinessObjectsForCurrentUserById();

    /**
     * Here we demonstrate the use of SecurityContext information in dynamic SpEL parameters in a JPQL update statement.
     */
//    @Modifying(clearAutomatically = true)
//    @Query("update BusinessObject b set b.data = upper(b.data), b.lastModifiedBy = :#{#security.principal}, b.lastModifiedDate = :#{new java.util.Date()}")
//    void modifiyDataWithRecordingSecurityContext();
}
