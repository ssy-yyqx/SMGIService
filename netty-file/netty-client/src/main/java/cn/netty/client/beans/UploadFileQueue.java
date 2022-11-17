package cn.netty.client.beans;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @ClassName UploadFileQueue
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class UploadFileQueue {

    private static final ConcurrentHashMap<String, File> fileQueue = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, String> fileResult = new ConcurrentHashMap<>();

    private UploadFileQueue() {}

    private static class SingletonHolder {
        private static UploadFileQueue instance = new UploadFileQueue();
    }

    public static UploadFileQueue getInstance() {
        return SingletonHolder.instance;
    }

    public static void addFile(String key, File file) {
        fileQueue.put(key, file);
    }

    public File getFile(String key) {
        return fileQueue.remove(key);
    }

    public void addResult(String fileKey, String fileId) {
        fileResult.put(fileKey, fileId);
    }

    public String uploaded (String fileKey) {
        if (fileResult.containsKey(fileKey)) {
            return fileResult.get(fileKey);
        } else {
            return null;
        }
    }

    public static int queueSize() {
        return fileQueue.size();
    }
}
