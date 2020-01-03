package com.example.demo.common.config;

import com.example.demo.data.StateMachineEvent;
import com.example.demo.data.model.Events;
import com.example.demo.data.model.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine//启动状态机功能
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<States, Events> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 状态机的初始状态和所有状态
     *
     * @param states
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states.withStates().initial(States.UNPAID).states(EnumSet.allOf(States.class));
    }

    /**
     * 状态之间的转移规则
     *
     * @param transitions
     * @throws Exception
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                .withExternal()
                .source(States.UNPAID).target(States.WAITING_FOR_RECEIVE)//状态来源和目标状态
                .event(Events.PAY)//触发事件

                .and()

                .withExternal()
                .source(States.WAITING_FOR_RECEIVE).target(States.DONE)
                .event(Events.RECEIVE)

//                .and()
//
//                .withExternal()
//                .source(States.PUBLISH_DONE).target(States.DRAFT)
//                .event(Events.ROLLBACK);
        ;
    }

//    @Override
//    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
//        config.withConfiguration().listener(listener());
//    }
//
    /**
     * 状态迁移监听 {@link StateMachineEvent}注解实现
     *
     * @return
     */
//    @Bean
//    public StateMachineListener<States, Events> listener() {
//        return new StateMachineListenerAdapter<States, Events>() {
//            @Override
//            public void transition(Transition<States, Events> transition) {
////                super.transition(transition);
//
//                if (transition.getTarget().getId() == States.UNPAID) {
//                    logger.info("订单创建，待支付");
//                    return;
//                }
//                if (transition.getSource().getId() == States.UNPAID
//                        && transition.getTarget().getId() == States.WAITING_FOR_RECEIVE) {
//                    logger.info("订单完成支付，待收货");
//                    return;
//                }
//                if (transition.getSource().getId() == States.WAITING_FOR_RECEIVE
//                        && transition.getTarget().getId() == States.DONE) {
//                    logger.info("订单完成收货，订单完成");
//                    return;
//                }
//            }
//        };
//    }
}
