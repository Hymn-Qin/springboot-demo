package com.example.demo.service;

import com.example.demo.data.model.RegisterUser;
import com.example.demo.model.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

//@CacheConfig ： 主要用于配置该类中会用到的一些共用的缓存配置。
//在这里@CacheConfig(cacheNames = "user")：配置了该数据访问对象中返回的内容将存储于名为student的缓存对象中，我们也可以不使用该注解，
//直接通过@Cacheable自己配置缓存集的名字来定义；
@CacheConfig(cacheNames = "user")
public interface UserService {


    //CachePut更新


    public User saveUser(RegisterUser register);

    //1. @Cacheable：配置了queryStudentBySno函数的返回值将被加入缓存。
    //      同时在查询时，会先从缓存中获取，若不存在才再发起对数据库的访问。
    //2. #p0代表第一个参数
    //      key = "#p0" 第一个参数作为key键
    //3. condition：缓存对象的条件，非必需，也需使用SpEL表达式，只有满足表达式条件的内容才会被缓存，
    //      比如：@Cacheable(key = "#p0", condition = "#p0.length() < 3")，
    //      表示只有当第一个参数的长度小于3的时候才会被缓存；
    //4. unless：另外一个缓存条件参数，非必需，需使用SpEL表达式。它不同于condition参数的地方在于它的判断时机，
    //      该条件是在函数被调用之后才做判断的，所以它可以通过对result进行判断；
    //5. keyGenerator：用于指定key生成器，非必需。若需要指定一个自定义的key生成器，
    //      我们需要去实现org.springframework.cache.interceptor.KeyGenerator接口，并使用该参数来指定；
    //6. cacheManager：用于指定使用哪个缓存管理器，非必需。只有当有多个时才需要使用；
    //7. cacheResolver：用于指定使用那个缓存解析器，非必需。
    //      需通过org.springframework.cache.interceptor.CacheResolver接口来实现自己的缓存解析器，并用该参数指定；
    @Cacheable(key = "#p0")
    public User findByUserId(Integer id);

//    @Cacheable(key = "#p0")
    public User findUserByUsername(String username);

    //@CacheEvict：配置于函数上，通常用在删除方法上，用来从缓存中移除相应数据。
    //除了同@Cacheable一样的参数之外，它还有下面两个参数：
    //    allEntries：非必需，默认为false。当为true时，会移除所有数据；
    //    beforeInvocation：非必需，默认为false，会在调用方法之后移除数据。当为true时，会在调用方法之前移除数据。
    @CacheEvict(key = "#p0", allEntries = true)
    public void deleteUserByUserId(Integer id);

    //@CachePut：配置于函数上，能够根据参数定义条件来进行缓存，其缓存的是方法的返回值，它与@Cacheable不同的是，
    // 它每次都会真实调用函数，所以主要用于数据新增和修改操作上。
    // 它的参数与@Cacheable类似，具体功能可参考上面对@Cacheable参数的解析；
    @CachePut(key = "#p0.id")
    public User updateUser(User user);

    //缓存实现
    //
    //要使用上Spring Boot的缓存功能，还需要提供一个缓存的具体实现。Spring Boot根据下面的顺序去侦测缓存实现：
    //
    //    Generic
    //
    //    JCache (JSR-107)
    //
    //    EhCache 2.x
    //
    //    Hazelcast
    //
    //    Infinispan
    //
    //    Redis
    //
    //    Guava
    //
    //    Simple
    //
    //除了按顺序侦测外，我们也可以通过配置属性spring.cache.type来强制指定。
}
