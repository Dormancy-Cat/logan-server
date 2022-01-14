package com.dormancycat.logan.logansyrius.service.impl;

import com.dormancycat.logan.logansyrius.enums.ResultEnum;
import com.dormancycat.logan.logansyrius.parser.LoganProtocol;
import com.dormancycat.logan.logansyrius.util.FileUtil;
import com.dormancycat.logan.logansyrius.service.LoganLogFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Slf4j
@Service
public class LoganLogFileServiceDefaultImpl implements LoganLogFileService {


    @Override
    public ResultEnum write(InputStream inputStream, String fileName) {
        if (inputStream == null || StringUtils.isEmpty(fileName)) {
            return ResultEnum.ERROR_PARAM;
        }
        try {
            File file = FileUtil.createNewFile(fileName);
            if (file == null) {
                return ResultEnum.ERROR_LOG_PATH;
            }
            return new LoganProtocol(inputStream, file).process();
        } catch (Exception e) {
            log.error("", e);
        }
        return ResultEnum.EXCEPTION;
    }
}
