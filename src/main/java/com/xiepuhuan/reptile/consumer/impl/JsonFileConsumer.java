package com.xiepuhuan.reptile.consumer.impl;

import com.alibaba.fastjson.JSON;
import com.xiepuhuan.reptile.model.Result;
import com.xiepuhuan.reptile.utils.ArgUtils;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiepuhuan
 */
public class JsonFileConsumer extends AbstractCloseableBufferConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileConsumer.class);

    public static final String DEFAULT_FILE_NAME = "json_file_consumer.json";

    private final File file;

    private final Writer writer;

    /**
     * @param file 包含输出的文件路径
     * @param flushInterval 刷新缓冲区的写入次数间隔, 默认为1表示每次写入1次就刷新
     * @throws IOException　创建文件出错抛出异常
     */
    public JsonFileConsumer(File file, int flushInterval) throws IOException {
        super(flushInterval);
        ArgUtils.notNull(file, "file");
        if (!file.exists()) {
            file.createNewFile();
        }
        this.file = file;
        this.writer = Files.newBufferedWriter(file.toPath(), Charset.defaultCharset());
    }

    public JsonFileConsumer() throws IOException {
        this(new File(DEFAULT_FILE_NAME), DEFAULT_FLUSH_INTERVAL);
    }

    public JsonFileConsumer(String filePath) throws IOException {
        this(new File(filePath), DEFAULT_FLUSH_INTERVAL);
    }

    public JsonFileConsumer(String filePath, int flushInterval) throws IOException {
        this(new File(filePath), flushInterval);
    }

    @Override
    protected void flush(Result[] buffer, int position) throws IOException {
        for (int i = 0; i < position; ++i) {
            try {
                writer.append(JSON.toJSONString(buffer[i].getResults())).append("xiepuhuan").append("\n");
            } catch (IOException e) {
                LOGGER.warn("Failed to append buffer: {}", e.getMessage());
            }
        }
        writer.flush();
    }

    @Override
    public synchronized void close() throws IOException {
        flush();
        writer.close();
    }
}
