/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.demo.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {

    @JsonIgnore
    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @JsonIgnore
    @LastModifiedDate
	@Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    /**
     * 修改人
     * {@link com.example.demo.common.config.JPAConfiguration getCurrentAuditor()}
     */
    @JsonIgnore
    @Column(name = "last_modified_by", length = 64)
    @LastModifiedBy
    private String lastModifiedBy;

    //@CreatedDate：创建时间
    //
    // @CreatedBy：创建人
    //
    // @LastModifiedDate：最后一次修改时间
    //
    // @LastModifiedBy：最后一次修改人

    //那你想把创建人、修改人设置成什么呢？
    //
    //需要@PrePersist、@PreUpdate 注解，
    // 以及在该实体类上需要添加监听@EntityListeners(AuditingEntityListener.class)，
    // 当然启动时别忘记application启动类中加上注解@EnableJpaAuditing
}
