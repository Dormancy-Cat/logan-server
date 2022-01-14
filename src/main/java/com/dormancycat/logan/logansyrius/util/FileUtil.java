package com.dormancycat.logan.logansyrius.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * 类描述:文件处理工具类
 * * @since 2019-11-25 17:01
 */
@Component
public class FileUtil {

    private static String logFileRootPath;

    @Value("${logan.logfile.root-path:/}")
    public void setLogFileRootPath(String logFileRootPath) {
        FileUtil.logFileRootPath = logFileRootPath;
    }

    public static File createNewFile(String fileName) {
        File file = getFile(fileName);
        if (file == null) {
            return null;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            return null;
        }
        return file;
    }

    public static File getFile(String fileName) {
        String path = logFileRootPath + "logfile" + File.separator;
        File file = new File(path + fileName);
        if (!path.equals(file.getParentFile().getAbsolutePath()+ File.separator)) {
            return null;
        }
        return file;
    }

    public static String getDownloadUrl(HttpServletRequest request, String fileName) {
        if (StringUtils.isEmpty(fileName) || request == null) {
            return "";
        }
        return "/logan/downing?name=" + fileName;
    }
}

