package com.example.demo.data.model;

import com.example.demo.common.validation.First;
import com.example.demo.utils.PatternUtils;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class RegisterUser {
    @NotNull(message = "{error.register.username}", groups = First.class)
    @NotBlank(message = "{error.register.username}", groups = First.class)
    @Pattern(regexp = PatternUtils.MOBILE_NUMBER_PATTERN, message = "{error.register.username.phone}", groups = First.class)
    String username;

    @NotNull(message = "{error.register.password}")
    @NotBlank(message = "{error.register.password}")
    @Length(min = 6, max = 12, message = "{error.register.password}")
    String password;
}
