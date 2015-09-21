package com.unimelb.serena;

/**
 * Created by xialeizhou on 9/18/15.
 */

import java.io.IOException;
import java.net.NetworkInterface;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import static com.unimelb.serena.Utils.getCurrTime;
import static com.unimelb.serena.Utils.string2bigint;
import static com.unimelb.serena.Utils.string2int;

@Controller
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class Mp3IoTApplication extends SpringBootServletInitializer {
    private static final String Usage = "Usage: ";
    private CnnMysql mysql;

    private static final Logger LOG = LoggerFactory
            .getLogger(Mp3IoTApplication.class);

    private static Class<Mp3IoTApplication> applicationClass = Mp3IoTApplication.class;

    @Override
    protected SpringApplicationBuilder configure( SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    @RequestMapping("/")
    @ResponseBody
    String home() {
        String Usage = "<h2>Yaw Query Service</h2>";
        Usage += "Usage: </br>" +
                "[1] http://host:port/query_yaw_pic?x=***&y=****&z=***&flag=***</br>" +
                "[2] http://host:port/query_yaw_statis?x=***&y=****&z=***&flag=***";
        return Usage;
    }

    /**
     * Query temperature.
     * @param stime
     * @param etime
     * @return
     */
    @RequestMapping(value = "/serena/temperature/get/{stime}/{etime}", method = RequestMethod.POST)
    @ResponseBody
    public List<String> get_temperature(
            @PathVariable String stime,
            @PathVariable String etime
            ) {
        List<String> list = null;
        mysql = new CnnMysql();
        LOG.info("start get temperature");
        try {
            list = mysql.readTemperature(string2bigint(stime), string2bigint(etime));
            // Sort list to display
            Collections.reverse(list);

            LOG.info("records number: "+ list.size());
        } catch (SQLException e) {
            e.printStackTrace();
        };
        LOG.info("stime:" + stime + ", etime:" + etime);
        return list;
    }

    /**
     * Update Accelerometer.
     * @param submit_time
     * @param value
     * @return
     */
    @RequestMapping(value = "/serena/accelerometer/update/{submit_time}/{value}", method = RequestMethod.POST)
    @ResponseBody
    public String put_accelerometer (
            @PathVariable("submit_time") String submit_time,
            @PathVariable("value") String value
    ) {
        Boolean state = false;
        String msg = null;
        mysql = new CnnMysql();
        LOG.info("start get accelerometer");
        try {
            state = mysql.writeAccelerometer(string2bigint(getCurrTime()), string2int(value));
            LOG.info("update state:" + state);
        } catch (SQLException e) {
            e.printStackTrace();
        };
        if (state == true)
            msg = "SUCCESS";
        else
            msg = "FAILED";
        return msg;
    }

    /**
     *
     * @param submit_time
     * @param value
     * @return
     */
    @RequestMapping(value = "/serena/temperature/update/{submit_time}/{value}", method = RequestMethod.POST)
    @ResponseBody
    public String put_temperature(
            @PathVariable("submit_time") String submit_time,
            @PathVariable("value") String value
    ) {
        Boolean state = false;
        String msg = null;
        mysql = new CnnMysql();
        LOG.info("start get temperature");
        try {
            state = mysql.writeTemperature(string2bigint(getCurrTime()), string2int(value));
            LOG.info("update state:" + state);
        } catch (SQLException e) {
            e.printStackTrace();
        };
        if (state == true)
            msg = "SUCCESS";
        else
            msg = "FAILED";
        return msg;
    }

    @RequestMapping(value = "/serena/accelerometer/get/{stime}/{etime}", method = RequestMethod.POST)
    @ResponseBody
    public List<String> get_accelerometer(
            @PathVariable("stime") String stime,
            @PathVariable("etime") String etime
    ) {
        List<String> list = null;
        mysql = new CnnMysql();
        LOG.info("start get temperature");
        try {
            list = mysql.readAccelerometer(string2bigint(stime), string2bigint(etime));
            // Sort list to display
            Collections.reverse(list);
            LOG.info("records number: "+ list.size());
        } catch (SQLException e) {
            e.printStackTrace();
        };
        LOG.info("stime:" + stime + ", etime:" + etime);
        return list;

    }

    public static void main(String[] args) {
        SpringApplication.run(Mp3IoTApplication.class, args);
    }
}
