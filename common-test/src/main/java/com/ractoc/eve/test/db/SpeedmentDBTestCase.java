package com.ractoc.eve.test.db;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.dbunit.Assertion;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlConnection;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Superclass for running testcases on speedment enabled classes using DBUnit.
 * <p>
 * There are two methods which should be executed from the @BeforeAll and @AfterAll in the implementing Unit test class:
 *
 * <ul>
 * <li>createDatabase : This method creates the temporary database based on the supplied information</li>
 * <li>destroyDatabase: This method stops the temporary database.</li>
 * </ul>
 *
 * @author ractoc
 */
public abstract class SpeedmentDBTestCase {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    protected static final String USERNAME = "root";
    protected static final String PASSWORD = "root";
    private static DB db;
    private static String dbSchema;
    private static String initialDataSetFile;
    private IDatabaseTester databaseTester;
    private IDatabaseConnection connection;

    protected static String dbUrl;

    /**
     * Creates and starts a new, temporary, MariaDB Database based on the supplied information.
     * <p>
     * This method should be called from the @BeforeAll method in the actual testcase.
     *
     * @param dbName         The title of the database instance. This needs to be the same as the title in the speedment.json file.
     * @param portNumber     The port number on which to connect to the database. This needs to be the same as the title in the speedment.json file.
     * @param schemaScript   The script containing the SQL to setup the database.
     * @param initialDataset The initial dataset to load at the start of each testcase.
     * @throws ManagedProcessException An indication something went wrong while creating the database.
     */
    protected static void createDatabase(String dbName, int portNumber, String schemaScript, String initialDataset) throws Exception {
        SpeedmentDBTestCase.dbSchema = dbName;
        SpeedmentDBTestCase.initialDataSetFile = initialDataset;

        DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
        configBuilder.setPort(portNumber);
        // make sure the temporary datafiles are created in the target folder so they
        // are cleaned each time a mvn clean is performed
        configBuilder.setDataDir("target/test/db");
        db = DB.newEmbeddedDB(configBuilder.build());
        db.start();
        db.createDB(dbName);
        db.source(schemaScript);

        dbUrl = configBuilder.getURL(dbName);
    }

    protected static void createDatabase(String dbName, String dbUrl, String schemaScript, String initialDataset) throws Exception {
        SpeedmentDBTestCase.dbUrl = dbUrl;
        SpeedmentDBTestCase.dbSchema = dbName;
        SpeedmentDBTestCase.initialDataSetFile = initialDataset;

        executeScript(schemaScript);
    }

    protected static void executeScript(String scriptFile) throws Exception {
        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(SpeedmentDBTestCase.dbUrl, USERNAME, PASSWORD);
        ScriptUtils.executeSqlScript(connection, new EncodedResource(new ClassPathResource(scriptFile), StandardCharsets.UTF_8));
        connection.close();
    }

    /**
     * Stops the temporary MariaDB database.
     * <p>
     * This method should be called from the @AfterAll method in the testcase.
     *
     * @throws ManagedProcessException An indication something went wrong while stopping the database.
     */
    protected static void destroyDatabase() throws ManagedProcessException {
        if (db != null) {
            db.stop();
        }
    }

    @BeforeEach
    public void setUpDbUnit() throws Exception {
        cleanlyInsert(getDataSet(initialDataSetFile));
    }

    @AfterEach
    public void tearDown() throws Exception {
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.onTearDown();
    }

    /**
     * Returns the internal database connections as used by dbUnit.
     * This connection can be used when asserting the actual state of a table with the expected state of a table.
     *
     * @return The internal database connection as used by dbUnit.
     */
    protected IDatabaseConnection getConnection() {
        return connection;
    }

    /**
     * Get a dataset based on the supplied FlatXml datafile. The FlatXml dataformat can be found on the dbUnit website.
     * In this FlatXml file, all table names should be in the same case as they are in the actual database. Column names are case insensitive.
     *
     * @param filename Name of the FlatXml file. This file should be located onthe classpath, for example src/test/resources
     * @return The DataSet representing the FlatXml file.
     * @throws Exception Something went wrong while reading  the FlatXml file.
     */
    protected IDataSet getDataSet(String filename) throws Exception {
        System.out.println("loading dataset " + filename);
        ReplacementDataSet dataSet = new ReplacementDataSet(
                new FlatXmlDataSetBuilder().build(getClass().getClassLoader().getResourceAsStream(filename)));
        dataSet.addReplacementObject("[NULL]", null);
        return dataSet;
    }

    private void cleanlyInsert(IDataSet dataSet) throws Exception {
        System.out.println("CONNECTING TO DATABASE: " + dbUrl);
        databaseTester = new MySqlDatabaseTester(JDBC_DRIVER,
                dbUrl, USERNAME, PASSWORD, dbSchema);
        databaseTester.setSetUpOperation(DatabaseOperation.INSERT);
        databaseTester.setTearDownOperation(DatabaseOperation.TRUNCATE_TABLE);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
        connection = databaseTester.getConnection();
    }

    protected int tableRowCount(String tableName) throws Exception {
        IDataSet databaseDataSet = getConnection().createDataSet();
        ITable actualTable = databaseDataSet.getTable(tableName);
        return actualTable.getRowCount();
    }

    protected void compareTable(String dataset, String tableName) throws Exception {
        IDataSet databaseDataSet = getConnection().createDataSet();
        ITable actualTable = databaseDataSet.getTable(tableName);

        IDataSet expectedDataSet = getDataSet(dataset);
        ITable expectedTable = expectedDataSet.getTable(tableName);

        ITable filteredTable = DefaultColumnFilter.includedColumnsTable(actualTable,
                expectedTable.getTableMetaData().getColumns());

        Assertion.assertEquals(new SortedTable(expectedTable),
                new SortedTable(filteredTable, expectedTable.getTableMetaData()));
    }

    /**
     * Internal JdbDatabaseTester implementation specific for MySql. This is required since MySQL uses a non-default database connection.
     *
     * @author ractoc
     */
    private class MySqlDatabaseTester extends JdbcDatabaseTester {

        public MySqlDatabaseTester(String driverClass, String connectionUrl) throws ClassNotFoundException {
            super(driverClass, connectionUrl);
        }

        public MySqlDatabaseTester(String driverClass, String connectionUrl, String username, String password)
                throws ClassNotFoundException {
            super(driverClass, connectionUrl, username, password);
        }

        public MySqlDatabaseTester(String driverClass, String connectionUrl, String username, String password,
                                   String schema) throws ClassNotFoundException {
            super(driverClass, connectionUrl, username, password, schema);
        }

        /**
         * Converts the default database connection to a MySQL database connection.
         */
        @Override
        public IDatabaseConnection getConnection() throws Exception {
            IDatabaseConnection dbConn = super.getConnection();
            IDatabaseConnection mySqlConn = new MySqlConnection(dbConn.getConnection(), getSchema());
            return mySqlConn;
        }
    }
}
