package cn.htht.service.platform.portal.component;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/11/11
 */

import com.github.pagehelper.BoundSqlInterceptor.Chain;
import com.github.pagehelper.BoundSqlInterceptor.Type;
import com.github.pagehelper.Dialect;
import com.github.pagehelper.PageException;
import com.github.pagehelper.cache.Cache;
import com.github.pagehelper.cache.CacheFactory;
import com.github.pagehelper.util.MSUtils;
import com.github.pagehelper.util.StringUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Intercepts({@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
), @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
)})
public class PageInterceptor implements Interceptor {
    private volatile Dialect dialect;
    private String countSuffix = "_COUNT";
    protected Cache<String, MappedStatement> msCountMap = null;
    private String default_dialect_class = "com.github.pagehelper.PageHelper";

    public PageInterceptor() {
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement)args[0];
            Object parameter = args[1];
            RowBounds rowBounds = (RowBounds)args[2];
            ResultHandler resultHandler = (ResultHandler)args[3];
            Executor executor = (Executor)invocation.getTarget();
            CacheKey cacheKey;
            BoundSql boundSql;
            if (args.length == 4) {
                boundSql = ms.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
            } else {
                cacheKey = (CacheKey)args[4];
                boundSql = (BoundSql)args[5];
            }

            this.checkDialectExists();
            if (this.dialect instanceof Chain) {
                boundSql = ((Chain)this.dialect).doBoundSql(Type.ORIGINAL, boundSql, cacheKey);
            }

            List resultList;
            if (!this.dialect.skip(ms, parameter, rowBounds)) {
                if (this.dialect.beforeCount(ms, parameter, rowBounds)) {
                    String sqlTemp = boundSql.getSql();
                    Long count;
                    if (sqlTemp.contains(PageExecutorUtil.PageSqlResolver.LIMIT_SIGN)){
                        StringBuilder sqlBuilder = new StringBuilder(sqlTemp);
                        StringBuilder beforeSign = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.indexOf(PageExecutorUtil.PageSqlResolver.LIMIT_SIGN)));
                        // 剩余的
                        StringBuilder rest = new StringBuilder(
                                sqlBuilder.substring(sqlBuilder.indexOf(PageExecutorUtil.PageSqlResolver.LIMIT_SIGN), sqlBuilder.length()));
                        // 确定标记前sql语句括号对应关系
                        int bracketsCount = 0;
                        for (int i = 0; i < beforeSign.length(); i++) {
                            if (beforeSign.indexOf("?", i) != -1) {
                                i = beforeSign.indexOf("?", i);
                                bracketsCount++;
                            }
                        }
                        String[] beforeSqlTempArray = beforeSign.toString().split("\\(");
                        String beforeSqlTemp = beforeSqlTempArray[beforeSqlTempArray.length - bracketsCount - 1];
                        sqlTemp = "(" + beforeSign.substring(beforeSign.indexOf(beforeSqlTempArray[beforeSqlTempArray.length - bracketsCount]), beforeSign.length());

                        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
                        // 统计标记之后还有多少参数
                        int previousCount = 0;
                        int afterCount = 0;
                        for (int i = 0; i < beforeSqlTemp.length(); i++) {
                            if (beforeSqlTemp.indexOf("?", i) != -1) {
                                i = beforeSqlTemp.indexOf("?", i);
                                previousCount++;
                            }
                        }
                        for (int i = 0; i < rest.length(); i++) {
                            if (rest.indexOf("?", i) != -1) {
                                i = rest.indexOf("?", i);
                                afterCount++;
                            }
                        }
                        // 获取参数列表
                        List<ParameterMapping> tempParameterMappingList = new ArrayList<>(boundSql.getParameterMappings());
                        //是否需要调整
                        if (previousCount > 0) {
                            for (int i = 1; i <= previousCount; i++){
                                ParameterMapping parameterMapping = tempParameterMappingList.remove(0);
                            }
                        }
                        if (afterCount > 0) {
                            for (int i = 1; i <= afterCount; i++){
                                ParameterMapping parameterMapping = tempParameterMappingList.remove(tempParameterMappingList.size() - 1);
                            }
                        }

                        BoundSql boundSqlTemp = new BoundSql(ms.getConfiguration(), sqlTemp, tempParameterMappingList, parameter);
                        count = this.count(executor, ms, parameter, rowBounds, (ResultHandler)null, boundSqlTemp);
                    } else {
                        count = this.count(executor, ms, parameter, rowBounds, (ResultHandler)null, boundSql);
                    }
                    if (!this.dialect.afterCount(count, parameter, rowBounds)) {
                        Object var12 = this.dialect.afterPage(new ArrayList(), parameter, rowBounds);
                        return var12;
                    }
                }

                resultList = PageExecutorUtil.pageQuery(this.dialect, executor, ms, parameter, rowBounds, resultHandler, boundSql, cacheKey);
            } else {
                resultList = executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
            }

            Object var16 = this.dialect.afterPage(resultList, parameter, rowBounds);
            return var16;
        } finally {
            if (this.dialect != null) {
                this.dialect.afterAll();
            }

        }
    }

    private void checkDialectExists() {
        if (this.dialect == null) {
            synchronized(this.default_dialect_class) {
                if (this.dialect == null) {
                    this.setProperties(new Properties());
                }
            }
        }

    }

    private Long count(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        String countMsId = ms.getId() + this.countSuffix;
        MappedStatement countMs = PageExecutorUtil.getExistedMappedStatement(ms.getConfiguration(), countMsId);
        Long count;
        if (countMs != null) {
            count = PageExecutorUtil.executeManualCount(executor, countMs, parameter, boundSql, resultHandler);
        } else {
            if (this.msCountMap != null) {
                countMs = (MappedStatement)this.msCountMap.get(countMsId);
            }

            if (countMs == null) {
                countMs = MSUtils.newCountMappedStatement(ms, countMsId);
                if (this.msCountMap != null) {
                    this.msCountMap.put(countMsId, countMs);
                }
            }

            count = PageExecutorUtil.executeAutoCount(this.dialect, executor, countMs, parameter, boundSql, rowBounds, resultHandler);
        }

        return count;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.msCountMap = CacheFactory.createCache(properties.getProperty("msCountCache"), "ms", properties);
        String dialectClass = properties.getProperty("dialect");
        if (StringUtil.isEmpty(dialectClass)) {
            dialectClass = this.default_dialect_class;
        }

        try {
            Class<?> aClass = Class.forName(dialectClass);
            this.dialect = (Dialect)aClass.newInstance();
        } catch (Exception var4) {
            throw new PageException(var4);
        }

        this.dialect.setProperties(properties);
        String countSuffix = properties.getProperty("countSuffix");
        if (StringUtil.isNotEmpty(countSuffix)) {
            this.countSuffix = countSuffix;
        }

    }
}

