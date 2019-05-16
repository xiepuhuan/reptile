package com.xiepuhuan.reptile.consumer.impl;

import com.alibaba.fastjson.JSON;
import com.xiepuhuan.reptile.consumer.Consumer;
import com.xiepuhuan.reptile.model.Result;
import com.xiepuhuan.reptile.utils.ArgUtils;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * @author xiepuhuan
 */
public class JsonFileConsumer implements Consumer {

    public static final String DEFAULT_FILE_NAME = "json_file_consumer.json";

    private final File file;

    private final Writer writer;

    public JsonFileConsumer(File file) throws IOException {
        ArgUtils.notNull(file, "file");
        if (!file.exists()) {
            file.createNewFile();
        }
        this.file = file;
        writer = Files.newBufferedWriter(file.toPath(), Charset.defaultCharset());
    }

    public JsonFileConsumer() throws IOException {
        this(new File(DEFAULT_FILE_NAME));
    }

    public JsonFileConsumer(String filePath) throws IOException {
        this(new File(filePath));
    }

    @Override
    public void consume(Result result) throws IOException {
        writer.append(JSON.toJSONString(result));
    }
}
