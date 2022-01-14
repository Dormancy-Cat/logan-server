package com.dormancycat.logan.logansyrius.controller;

import com.dormancycat.logan.logansyrius.enums.ResultEnum;
import com.dormancycat.logan.logansyrius.model.LoganTaskModel;
import com.dormancycat.logan.logansyrius.task.LoganTaskModelService;
import com.dormancycat.logan.logansyrius.util.FileUtil;
import com.dormancycat.logan.logansyrius.model.response.LoganResponse;
import com.dormancycat.logan.logansyrius.parser.RequestContextParser;
import com.dormancycat.logan.logansyrius.service.LoganLogFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 客户端日志上报接口
 *
 * @since 2019-10-09 17:56
 * @since logan-web 1.0
 */
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/logan")
public class LoganUploadController {

    @Resource
    private LoganLogFileService fileService;

    @Resource
    private LoganTaskModelService loganTaskModelService;

    /**
     * 一次上报一个任务一天日志
     */
    @PostMapping("/upload.json")
    @ResponseBody
    public LoganResponse<String> upload(HttpServletRequest request) throws IOException {
        LoganTaskModel model = RequestContextParser.parse(request);
        log.info("upload params is {}",model);
        ResultEnum result = fileService.write(request.getInputStream(), model.getLogFileName());
        if (ResultEnum.SUCCESS != result) {
            log.error("write log error {}",result.name());
            return LoganResponse.exception(result.name());
        }
        try {
            loganTaskModelService.submitTask(model);
        } catch (Exception e) {
            log.error("error ",e);
            return LoganResponse.exception(ResultEnum.EXCEPTION.name());
        }
        return LoganResponse.success(FileUtil.getDownloadUrl(request, model.getLogFileName()));


    }
}