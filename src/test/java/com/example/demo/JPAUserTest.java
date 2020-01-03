package com.example.demo;

import com.example.demo.model.Car;
import com.example.demo.model.User;
import com.example.demo.repos.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JPAUserTest extends BasicJPATest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 1、Repository接口
     *  2、CrudRepository接口
     * 主要是完成一些增删改查的操作。注意：CrudRepository接口继承了Repository
     *  3、PagingAndSortingRepository接口
     * 提供了分页与排序的操作，注意：该接口继承了CrudRepository接口
     *  4、JpaRepository接口
     * 继承了PagingAndSortingRepository。对继承的父接口中方法的返回值进行适配。
     *  5、JPASpecificationExecutor接口
     * 该接口主要是提供了多条件查询的支持，并且可以在查询中添加排序与分页。注意JPASpecificationExecutor是单独存在的。完全独立
     */

    @Autowired
    UserRepository repository;

    @Test
    public void testSave() {
        User user = new User("aa@123.com", "aaaa", "aa", "aa", "123456");
        user.setBirthday(new Date(System.currentTimeMillis()));
//        this.repository.save(user);
        entityManager.persist(user);
    }

    @Test
    public void update() {
        try {
            User user = entityManager.find(User.class, 1);
            user.setDisplayName("秦小杰");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("没有id1");
        }

    }

    @Test
    public void merge() {
        User user = new User("aa@123.com", "a", "aa", "aa", "123456");
        user.setId(1);
        Car car = new Car("1q23456", "浙BBBBB");
        user.getCars().add(car);
        entityManager.merge(user);
    }

    @Test
    public void testSaveAndCar() {
        User user = new User("aa@123.com", "123", "aa", "aa", "123456");
        Car car = new Car("1q23456", "浙BBBBB");
        car.setUser(user);
        user.getCars().add(car);
        entityManager.persist(user);
//        this.repository.save(user);
    }

    @Test
    public void testFindAll() {
        List<User> list = this.repository.findAll();
        logger.info("testFindAll: {}", list);
    }

    public void testDelete() {
        this.repository.deleteById(1);
    }

    @Test
    public void testFindByUsername() {
        List<User> list = this.repository.queryByNameUseSQL("aa");
        logger.info("testFindByUsername: {}", list);
    }

    /**
     * Repository--@Query测试
     */
    @Test
    @Transactional //@Transactional与@Test 一起使用时 事务是自动回滚的。
    @Rollback(false) //取消自动回滚
    public void testUpdateUsersNameById() {
        this.repository.updateUsersNameById("张三三", 1);
        Optional<User> user = this.repository.findById(1);
        logger.info("testUpdateUsersNameById: {}", user.get());
    }

    /**
     * 排序查询
     */
    @Test
    public void testPagingAndSortingRepositorySort() {
        //ASC升序 DESC降序
        //这里总共列举了四种排序方式：
        //
        //1）直接创建Sort对象，适合对单一属性做排序
        //
        //2）通过Sort.Order对象创建Sort对象，适合对单一属性做排序
        //
        //3）通过属性的List集合创建Sort对象，适合对多个属性，采取同一种排序方式的排序
        //
        //4）通过Sort.Order对象的List集合创建Sort对象，适合所有情况，比较容易设置排序方式
        //
        //对应着我们的使用场景来进行选择创建Sort对象的方式。
        //Order 定义了排序规则
//        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
//        List<Sort.Order> orders = new ArrayList<>();
//        orders.add(order);
//        Sort sort = Sort.by(orders);
        //Sort 封装了排序规则
//        Sort sort = Sort.by(order);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<User> list = repository.findAll(sort);
        for (User user : list) {
            logger.info("testPagingAndSortingRepositorySort: {}", user);
        }
    }

    /**
     * 分页查询
     */
    @Test
    public void testPagingAndSortingRepositoryPaging() {
        //Pageable:封装了分页的参数，当前页，煤业显示的条数。注意：它的当前页是从0开始
        //PageRequest(page,size):page表示当前页，size表示每页显示多少条
//        Sort sort =  Sort.by(Sort.Direction.DESC, "id");
//        PageRequest.of(1, 2, sort);
//        PageRequest.of(1, 2, Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> page = repository.findAll(pageable);
        logger.info("page count: {}", page.getTotalPages());
        logger.info("user count: {}", page.getTotalElements());
        List<User> list = page.getContent();
        for (User user : list) {
            logger.info("testPagingAndSortingRepositoryPaging: {}", user);
        }
    }

    @Test
    public void testJpaSpecificationExecutor() {
        //repository 实现 JpaSpecificationExecutor 接口
        Specification<User> spec = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                Predicate predicate = criteriaBuilder.equal(root.get("name"), "张三三");
//                return predicate;
                Predicate[] list = new Predicate[]{
                        criteriaBuilder.equal(root.get("name"), "张三三"),
                        criteriaBuilder.equal(root.get("email"), "aa@123.com")
                };
//                criteriaBuilder.or(list);
                return criteriaBuilder.and(list);
            }
        };
        List<User> list = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
        for (User user : list) {
            logger.info("testJpaSpecificationExecutor: {}", user);
        }
    }
}
