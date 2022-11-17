package cn.htht.service.platform.portal.component;

import com.github.pagehelper.BoundSqlInterceptor;
import com.github.pagehelper.Dialect;
import com.github.pagehelper.util.ExecutorUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/11/11
 */
public class PageExecutorUtil extends ExecutorUtil {
    public static <E> List<E> pageQuery(Dialect dialect, Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql, CacheKey cacheKey) throws SQLException {
        if (!dialect.beforePage(ms, parameter, rowBounds)) {
            return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, boundSql);
        } else {
            parameter = dialect.processParameterObject(ms, parameter, boundSql, cacheKey);
            String pageSql = dialect.getPageSql(ms, boundSql, parameter, rowBounds, cacheKey);

            String changer = "";
            int limitParamCount = 0;
            if (pageSql.contains(PageSqlResolver.LIMIT_SIGN_TWO)) {
                changer = PageSqlResolver.LIMIT_SIGN_TWO;
                limitParamCount = 2;
            } else if (pageSql.contains(PageSqlResolver.LIMIT_PARAM_SIGLE)) {
                changer = PageSqlResolver.LIMIT_PARAM_SIGLE;
                limitParamCount = 1;
            }
            pageSql = PageSqlResolver.resolveLimit(pageSql, changer);
            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();

            // 调整参数列表偏移
            if (pageSql.contains(PageSqlResolver.LIMIT_SIGN)) {
                String sqlInCount = pageSql.substring(pageSql.indexOf(PageSqlResolver.LIMIT_SIGN));
                // 统计标记之后还有多少参数
                Integer count = 0;
                for (int i = 0; i < sqlInCount.length(); i++) {
                    if (sqlInCount.indexOf("?", i) != -1) {
                        i = sqlInCount.indexOf("?", i);
                        count++;
                    }
                }
                // 获取参数列表
                List<ParameterMapping> tempParameterMappingList = boundSql.getParameterMappings();
                int size = tempParameterMappingList.size();
                //是否需要调整
                if (count > 0) {
                    for (int i = 1; i <= limitParamCount; i++){
                        ParameterMapping parameterMapping = tempParameterMappingList.remove(size - 1);
                        tempParameterMappingList.add(size - limitParamCount - count, parameterMapping);
                    }
                }
                parameterMappingList = tempParameterMappingList;
            }
            BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql, parameterMappingList, parameter);


//            BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql, boundSql.getParameterMappings(), parameter);

            Map<String, Object> additionalParameters = getAdditionalParameter(boundSql);
            Iterator var12 = additionalParameters.keySet().iterator();

            while(var12.hasNext()) {
                String key = (String)var12.next();
                pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
            }

            if (dialect instanceof BoundSqlInterceptor.Chain) {
                pageBoundSql = ((BoundSqlInterceptor.Chain)dialect).doBoundSql(BoundSqlInterceptor.Type.PAGE_SQL, pageBoundSql, cacheKey);
            }

            return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, pageBoundSql);
        }
    }

    static class PageSqlResolver {

        public static final String LIMIT_SIGN = "AS PAGING";

        public static final String LIMIT_PARAM_SIGLE = "LIMIT ?";

        public static final String LIMIT_SIGN_TWO = "LIMIT ?, ?";

        public static String resolveLimit(String pageSql, String changer) {
            if (pageSql == null) {
                return null;
            }

            if (pageSql.contains(LIMIT_SIGN)) {// 如果需要特殊分页

                pageSql = pageSql.replace(changer, "");
                StringBuilder sqlBuilder = new StringBuilder(pageSql);

                StringBuilder beforeSign = new StringBuilder(sqlBuilder.substring(0, sqlBuilder.indexOf(LIMIT_SIGN)));// beforeSign
                // 剩余的
                StringBuilder rest = new StringBuilder(
                        sqlBuilder.substring(sqlBuilder.indexOf(LIMIT_SIGN), sqlBuilder.length()));

                beforeSign.insert(beforeSign.lastIndexOf(")"), String.format(" %s", changer));

                return beforeSign.append(rest).toString();
            } else {
                return pageSql;
            }
        }

    }
}
