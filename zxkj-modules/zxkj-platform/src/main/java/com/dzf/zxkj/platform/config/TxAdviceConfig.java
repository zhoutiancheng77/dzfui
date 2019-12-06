package com.dzf.zxkj.platform.config;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Configuration
public class TxAdviceConfig {
    private static final int TX_METHOD_TIMEOUT = 5;
    private static final String AOP_POINTCUT_EXPRESSION = "execution (* com.dzf.zxkj.platform.service..*.*(..))";
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Bean
    public TransactionInterceptor txAdvice() {
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        /*只读事务，不做更新操作*/
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
//        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS );
        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(
                Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        requiredTx.setTimeout(TX_METHOD_TIMEOUT);
        Map<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("add*", requiredTx);
        txMap.put("save*", requiredTx);
        txMap.put("modify*", requiredTx);
        txMap.put("update*", requiredTx);
        txMap.put("edit*", requiredTx);
        txMap.put("delete*", requiredTx);
        txMap.put("remove*", requiredTx);
        txMap.put("repair*", requiredTx);
        txMap.put("deleteAndRepair*", requiredTx);
        txMap.put("process*", requiredTx);
        txMap.put("clear*", requiredTx);
        txMap.put("create*", requiredTx);
        txMap.put("rollback*", requiredTx);
        txMap.put("upload*", requiredTx);
        txMap.put("cancel*", requiredTx);
        txMap.put("init*", requiredTx);
        txMap.put("new*", requiredTx);

        txMap.put("get*", readOnlyTx);
        txMap.put("find*", readOnlyTx);
        txMap.put("load*", readOnlyTx);
        txMap.put("search*", readOnlyTx);
        txMap.put("datagrid*", readOnlyTx);
        txMap.put("query*", readOnlyTx);
        txMap.put("*", readOnlyTx);
        source.setNameMap( txMap );
        TransactionInterceptor txAdvice = new TransactionInterceptor(transactionManager, source);
        return txAdvice;
    }
    @Bean
    public Advisor txAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, txAdvice());
    }
}
