package com.dormancycat.logan.logansyrius.service;

import com.dormancycat.logan.logansyrius.enums.ResultEnum;

import java.io.InputStream;

/**
 * @since 2019-11-08 16:01
 */
public interface LoganLogFileService {

    ResultEnum write(InputStream inputStream, String fileName);
}
