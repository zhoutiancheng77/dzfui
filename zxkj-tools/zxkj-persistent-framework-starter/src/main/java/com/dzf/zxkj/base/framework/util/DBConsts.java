package com.dzf.zxkj.base.framework.util;

/**
 * 数据库类型
 */
public interface DBConsts {

    // 数据库类型
    int DB2 = 0;
    int ORACLE = 1;
    int SQLSERVER = 2;
    int SYBASE = 3;
    int INFORMIX = 4;
    int HSQL = 5;
    int OSCAR = 6;
    int POSTGRESQL = 7;
    int GBASE = 8;
    int UNKOWNDATABASE = -1;
    String DEFAULT_DATABASE_ID = "default_database";

    String ORACLE_NAME = "ORACLE";
    String MSSQL_NAME = "MSSQL";
    String DB2_NAME = "DB2";
    String HSQL_NAME = "HSQL";
    String SYBASE_NAME = "SYBASE";
    String INFORMIX_NAME = "INFORMIX";
    String OSCAR_NAME = "OSCAR";
    String POSTGRESQL_NAME = "POSTGRESQL";
    String GBASE_NAME = "GBASE";
    String UNKOWN_NAME = "UNKOWN";

    // JDBC驱动
    String JDBC_ODBC = "sun.jdbc.odbc.JdbcOdbcDriver";
    String JDBC_DB2_NET = "COM.ibm.db2.jdbc.net.DB2Driver";
    String JDBC_DB2_APP = "COM.ibm.db2.jdbc.app.DB2Driver";
    String JDBC_ORACLE = "oracle.jdbc.driver.OracleDriver";
    String JDBC_SYBASE = "com.sybase.jdbc.SybDriver";
    String JDBC_GBASE = "com.gbase.jdbc.Driver";
    String JDBC_OSCAR = "com.oscar.Driver";

    // ConnectionDriver
    String URL_PREFIX = "jdbc:ufsoft:jdbcDriver";
    int MAJOR_VERSION = 1;
    int MINOR_VERSION = 0;

    String JdbcOdbcBridgeName = "JDBC-ODBC Bridge";

    String JDBC_INFORMIX = "com.informix.jdbc.IfxDriver";
    String JDBC_SQLSERVER = "com.microsoft.jdbc.sqlserver.SQLServerDriver";

    // ------------------------------------------------------------------------
    // Sql语句类型
    // ------------------------------------------------------------------------

    int SQL_SELECT = 1;
    int SQL_INSERT = 2;
    int SQL_CREATE = 3;
    int SQL_DROP = 4;
    int SQL_DELETE = 5;
    int SQL_UPDATE = 6;
    int SQL_EXPLAIN = 7;

    // ------------------------------------------------------------------------
    // 函数列表,在SQL语句中只能用这些函数
    // ------------------------------------------------------------------------
    String[] functions = {"coalesce", "len", "left", "right",
            "substring", "lower", "upper", "ltrim", "rtrim", "sqrt", "abs",
            "square", "sign", "count", "max", "min", "sum", "avg", "cast"};

    //zpm改成空
    String NULL_WAVE = null;
}