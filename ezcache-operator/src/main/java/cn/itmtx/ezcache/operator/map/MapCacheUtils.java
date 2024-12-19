package cn.itmtx.ezcache.operator.map;

import java.io.File;

public class MapCacheUtils {

    private static String getSavePath(String namespace) {
        String path = "/temp/ez-cache/";
        if (null != namespace && !namespace.trim().isEmpty()) {
            // File.separator，它会自动根据运行环境返回正确的路径分隔符
            path += namespace.trim() + File.separator;
        }
        return path;
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
