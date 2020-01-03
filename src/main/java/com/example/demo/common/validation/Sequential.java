package com.example.demo.common.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({First.class, Second.class, Default.class})
public interface Sequential {
}
