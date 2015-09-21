package com.unimelb.serena;

/**
 * Created by xialeizhou on 9/21/15.
 */
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.unimelb.serena.Utils.getCurrTime;
import static com.unimelb.serena.Utils.string2bigint;
import static java.lang.Thread.sleep;

public class  CnnMysql{
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    // Mysql database
//    private static final String DB_URL = "jdbc:mysql://127.0.0.1/mp3_app_data";
    private static final String DB_URL = "jdbc:mysql://49.213.15.196/mp3_app_data";

    //  Database credentials
    private static final String USER = "serena";
    private static final String PASS = "WYYpll08040408";
    private static final String TBL_TEMP = "tbl_temperature";
    private static final String TBL_ACC = "tbl_accelerometer";
    private static final String DB = "app_data";

    private  Connection mysql = null;
    private Statement stmt = null;

    public CnnMysql() {

       /* Auto commit updated data.*/
        getMysqlConnection(false);
    }

    public void getMysqlConnection(Boolean autoCommit) {
        try {
            Class.forName(JDBC_DRIVER);
            if (mysql == null) {
                mysql = DriverManager.getConnection(DB_URL, USER, PASS);
                mysql.setAutoCommit(autoCommit);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public ResultSet query(String sql) throws SQLException {
        getMysqlConnection(false);
        ResultSet rs = null;
        try {
            stmt = mysql.createStatement();
            rs = stmt.executeQuery(sql);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * @param sql
     * @return
     */
    public Boolean execute(String sql) {
        getMysqlConnection(false);
        try {
            stmt = mysql.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *
     * @param stime
     * @param etime
     * @return
     * @throws SQLException
     */
    public List<String> readAccelerometer(BigInteger stime, BigInteger etime) throws SQLException {
        HashMap<BigInteger, Integer> tempData = null;
        List<String> list = new ArrayList<String>();

        String sql = "SELECT * FROM " + this.TBL_ACC + " where submit_time >= " + stime + " and submit_time <= " + etime;
        System.out.println("begin to query " + sql);
        ResultSet rs = query(sql);
        if (rs.wasNull()) return null;
        while(rs.next()) {
            BigInteger time = new BigInteger(rs.getString("submit_time"));
            Integer value = rs.getInt("value");
            String recordPair = time + "#" + value;
            list.add(recordPair);
        }
        return list;
    }

    /**
     *
     * @param stime
     * @param etime
     * @return
     * @throws SQLException
     */
    public List<String> readTemperature(BigInteger stime, BigInteger etime) throws SQLException {
        HashMap<BigInteger, Integer> tempData = null;
        List<String> list = new ArrayList<String>();

        String sql = "SELECT * FROM " + this.TBL_TEMP + " where submit_time >= " + stime + " and submit_time <= " + etime;
        System.out.println("begin to query " + sql);
        ResultSet rs = query(sql);
        if (rs.wasNull()) return null;
        while(rs.next()) {
            BigInteger time = new BigInteger(rs.getString("submit_time"));
            Integer value = rs.getInt("value");
            String recordPair = time + "#" + value;
            list.add(recordPair);
        }
        return list;
    }

    /**
     * Insert a key value pair to mysql database.
     * @param submitTime
     * @param tmpValue
     * @return
     * @throws SQLException
     */
    public Boolean writeTemperature(BigInteger submitTime, int tmpValue) throws SQLException {
        PreparedStatement pstmt = null;
        boolean success = false;
        StringBuffer sql = new StringBuffer("INSERT INTO " + this.TBL_TEMP + "(submit_time, value) values ");
        sql.append(" (?, ?)");
        try {
            pstmt = mysql.prepareStatement(sql.toString());
            pstmt.setBigDecimal(1, new BigDecimal(submitTime));
            pstmt.setInt(2, tmpValue);
            pstmt.executeUpdate();
            mysql.commit();
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * Insert a key value pair to mysql database.
     * @param submitTime
     * @param accValue
     * @return
     * @throws SQLException
     */
    public Boolean writeAccelerometer(BigInteger submitTime, int accValue) throws SQLException {
        PreparedStatement pstmt = null;
        boolean success = false;
        StringBuffer sql = new StringBuffer("INSERT INTO " + this.TBL_ACC + "(submit_time, value) values ");
        sql.append(" (?, ?)");
        try {
            pstmt = mysql.prepareStatement(sql.toString());
            pstmt.setBigDecimal(1, new BigDecimal(submitTime));
            pstmt.setInt(2, accValue);
            pstmt.executeUpdate();
            mysql.commit();
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     *
     * @param rs
     * @throws SQLException
     */
    public void readData(ResultSet rs) throws SQLException {
        try {
            while(rs.next()) {
                //Retrieve by column name
             /*step1: read data from usb,physical device */
                String name = rs.getString("name");
                String tel = rs.getString("tel");
                System.out.println("name:"+name);
                System.out.println("tel:"+tel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * close Mysql connecton.
     */
    public void close() {
        try {
            if(stmt != null) {
                stmt.close();
            }
            if (mysql != null) {
                mysql.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//
//    public static void main(String[] args) throws SQLException, InterruptedException {
//        CnnMysql cnnMysql = new CnnMysql();
//        BigInteger stime = string2bigint("20150920223319");
//        BigInteger etime = string2bigint("20150921230000");
////        cnnMysql.writeTemperature(string2bigint("201509211908"), 54);
////        System.out.println(getCurrTime());
////        cnnMysql.writeTemperature(string2bigint(getCurrTime()), 58);
////        for (int i = 0; i < 300; i++) {
////            cnnMysql.writeTemperature(string2bigint(getCurrTime()), 59);
////            sleep(1000); // for debug
////        }
//        List<String> relist = cnnMysql.readTemperature(stime, etime);
//        System.out.println(relist.size());
//        cnnMysql.close();
//    }//end main

}//end
