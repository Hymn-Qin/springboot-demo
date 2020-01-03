package com.example.demo;

import com.example.demo.repos.RoleRepository;
import com.example.demo.model.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JPARoleTest extends BasicJPATest {
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
    RoleRepository repository;

    @Test
    public void testSave() {
        Role role = new Role("vip1");
        this.repository.save(role);
    }

//    @Test
//    public void testFindAll() {
//        List<User> list = this.repository.findAll();
//        logger.info("testFindAll: {}", list);
//    }
}
