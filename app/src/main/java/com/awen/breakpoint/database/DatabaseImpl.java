package com.awen.breakpoint.database;


import com.awen.breakpoint.download.DownloadInfo;

import java.util.List;

/**
 * 数据访问接口
 */
public interface DatabaseImpl {
    void insert(DownloadInfo downloadInfo);

    void delete(String url, int download_id);

    void update(String url, int download_id, long progress);

    List<DownloadInfo> query(String url);

    boolean isExists(String url, int download_id);
}
