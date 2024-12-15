package cn.itmtx.ezcache.operator.map;

import cn.itmtx.ezcache.common.utils.OsUtil;

import java.io.File;

public class MapCacheUtils {

    private static String getSavePath(String namespace) {
        String path = "/tmp/autoload-cache/";
        if (null != namespace && namespace.trim().length() > 0) {
            path += namespace.trim() + "/";
        }
        if (OsUtil.getInstance().isLinux()) {
            return path;
        }
        return "C:" + path;
    }

    public static File getSaveFile(String namespace) {
        String path = getSavePath(namespace);
        File savePath = new File(path);
        if (!savePath.exists()) {
            savePath.mkdirs();
        }
        return new File(path + "map.cache");
    }
}
