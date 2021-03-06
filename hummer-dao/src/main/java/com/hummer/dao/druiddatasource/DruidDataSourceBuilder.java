package com.hummer.dao.druiddatasource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hummer.common.ErrorCodeConsts;
import com.hummer.common.SysConstant;
import com.hummer.common.exceptions.SysException;
import com.hummer.core.PropertiesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.alibaba.druid.util.JdbcConstants.MYSQL_DRIVER;

/**
 * this class wrapper druid builder data source feature.
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/26 17:35
 **/
public class DruidDataSourceBuilder {
    private DruidDataSourceBuilder() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DruidDataSource.class);

    /**
     * builder druid data source instance .see also <a href='https://github.com/alibaba/druid'>https://github.com/alibaba/druid</a>
     *
     * @param ds druid data source configuration map
     * @return {@link com.alibaba.druid.pool.DruidDataSource}
     * @author liguo
     * @date 2019/6/26 17:36
     * @version 1.0.0
     **/
    public static DruidDataSource buildDataSource(Map<String, Object> ds) {
        long start = System.currentTimeMillis();
        try  {
            DruidDataSource druidDataSource = new DruidDataSource();
            String driverClassName = (String) ds.get("driverClassName");
            druidDataSource.setDriverClassName(driverClassName);
            druidDataSource.setUrl((String) ds.get("url"));
            druidDataSource.setUsername((String) ds.get("username"));
            druidDataSource.setPassword((String) ds.get("password"));

            String initialSize = (String) ds.get("initialSize");
            if (initialSize != null) {
                druidDataSource.setInitialSize(Integer.parseInt(initialSize));
            }

            String connectionProperties = (String) ds.get("connectionProperties");
            if (connectionProperties != null) {
                druidDataSource.setConnectionProperties(connectionProperties);
            }

            String maxActive = (String) ds.get("maxActive");
            if (maxActive != null) {
                druidDataSource.setMaxActive(Integer.parseInt(maxActive));
            }

            String minIdle = (String) ds.get("minIdle");
            if (minIdle != null) {
                druidDataSource.setMinIdle(Integer.parseInt(minIdle));
            }

            String maxWait = (String) ds.get("maxWait");
            if (maxWait != null) {
                druidDataSource.setMaxWait(Long.parseLong(maxWait));
            }

            String timeBetweenEvictionRunsMillis = (String) ds.get("timeBetweenEvictionRunsMillis");
            if (timeBetweenEvictionRunsMillis != null) {
                druidDataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(timeBetweenEvictionRunsMillis));
            }

            String connectionInitSqls = (String) ds.get("connectionInitSqls");
            if (connectionInitSqls != null) {
                List<String> initSqls = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(connectionInitSqls);
                druidDataSource.setConnectionInitSqls(initSqls);
            }

            String minEvictableIdleTimeMillis = (String) ds.get("minEvictableIdleTimeMillis");
            if (minEvictableIdleTimeMillis != null) {
                druidDataSource.setMinEvictableIdleTimeMillis(Long.parseLong(minEvictableIdleTimeMillis));
            }

            String maxEvictableIdleTimeMillis = (String) ds.get("maxEvictableIdleTimeMillis");
            if (maxEvictableIdleTimeMillis != null) {
                druidDataSource.setMaxEvictableIdleTimeMillis(Long.parseLong(maxEvictableIdleTimeMillis));
            }
            druidDataSource.setTestWhileIdle(PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_TESTWHILEIDLE
                    , Boolean.class, Boolean.TRUE));
            druidDataSource.setTestOnBorrow(PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_TESTONBORROW
                    , Boolean.class, Boolean.TRUE));
            druidDataSource.setTestOnReturn(PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_TESTONRETURN
                    , Boolean.class, Boolean.FALSE));

            if (!MYSQL_DRIVER.equals(driverClassName)) {
                druidDataSource.setPoolPreparedStatements(true);
                String maxPoolPreparedStatementPerConnectionSize =
                        (String) ds.get("maxPoolPreparedStatementPerConnectionSize");
                if (maxPoolPreparedStatementPerConnectionSize != null) {
                    druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(
                            Integer.parseInt(maxPoolPreparedStatementPerConnectionSize));
                }
            }
            String queryTimeOutVal=(String)ds.get("queryTimeout");
            if(!Strings.isNullOrEmpty(queryTimeOutVal)) {
                druidDataSource.setQueryTimeout(Integer.parseInt(queryTimeOutVal));
            }
            druidDataSource.setValidationQuery("select 1");
            druidDataSource.setValidationQueryTimeout(PropertiesContainer.valueOf(
                    SysConstant.DaoConstant.JDBC_VALIDATIONQUERYTIMEOUT
                    , Integer.class, 5));
            //timeout remove connections
            druidDataSource.setRemoveAbandoned(PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_REMOVEABANDONED
                    , Boolean.class, Boolean.TRUE));
            //time out 30s
            druidDataSource.setRemoveAbandonedTimeout(PropertiesContainer.valueOf(
                    SysConstant.DaoConstant.JDBC_REMOVEABANDONEDTIMEOUT
                    , Integer.class, 30));
            //close connection output error logs
            druidDataSource.setLogAbandoned(PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_LOGABANDONED
                    , Boolean.class, Boolean.TRUE));

            List<Filter> filters = Lists.newArrayList(statFilter());

            if (PropertiesContainer.valueOf(SysConstant.DaoConstant.JDBC_CHECK_SQL
                    , Boolean.class, Boolean.TRUE)) {
                filters.add(wallFilter());
            }
            druidDataSource.setProxyFilters(filters);
            LOGGER.info("builder data source done cost {} ms "
                    , System.currentTimeMillis() - start);
            return druidDataSource;
        } catch (Exception ex) {
            LOGGER.info("builder data source exception cost {} ms ", System.currentTimeMillis() - start
                    , ex);
            throw new SysException(ErrorCodeConsts.DAO_LOAD_FAIL, ex.getMessage(), ex);
        }
    }

    private static WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        wallConfig.setSelectWhereAlwayTrueCheck(false);
        wallConfig.setSelectHavingAlwayTrueCheck(false);
        wallConfig.setSelectUnionCheck(PropertiesContainer.valueOf(SysConstant.DaoConstant.DRUID_SELECT_UNION_CHECK
                , Boolean.class, Boolean.TRUE));
        wallFilter.setConfig(wallConfig);
        return wallFilter;
    }

    private static StatFilter statFilter() {
        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(PropertiesContainer.valueOf(SysConstant.DaoConstant.DRUID_SHOW_TIMEOUT
                , Long.class, 3000L));
        statFilter.setLogSlowSql(PropertiesContainer.valueOf(SysConstant.DaoConstant.DRUID_SHOW_SQL
                , Boolean.class, Boolean.TRUE));
        statFilter.setMergeSql(PropertiesContainer.valueOf(SysConstant.DaoConstant.DRUID_MERGE_SQL
                , Boolean.class, Boolean.FALSE));
        return statFilter;
    }
}
