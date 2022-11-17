package cn.netty.common.entity;

import java.io.InputStream;
import java.io.Serializable;

/**
 * @ClassName Expand
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class TransferFileStream implements Serializable {

    private InputStream stream;

    private Integer chunkNumber;

    private String identifier;

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public void setChunkNumber(Integer chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public Integer getChunkNumber() {
        return chunkNumber;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}