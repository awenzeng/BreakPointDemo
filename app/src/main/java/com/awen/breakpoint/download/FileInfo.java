package com.awen.breakpoint.download;

import java.io.Serializable;

/**
 * 文件信息类
 */
public class FileInfo implements Serializable {
    private  int id;
    private String url;
    private String fileName;
    private long length;
    private long finish;

    public FileInfo() {
    }

    public FileInfo(int id, String url, String fileName, long length, long finish) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.finish = finish;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getFinish() {
        return finish;
    }

    public void setFinish(long finish) {
        this.finish = finish;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        sb.append(" id=").append(id);
        sb.append(", url='").append(url).append('\'');
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", length=").append(length);
        sb.append(", finish=").append(finish);
        sb.append('}');
        return sb.toString();
    }
}
