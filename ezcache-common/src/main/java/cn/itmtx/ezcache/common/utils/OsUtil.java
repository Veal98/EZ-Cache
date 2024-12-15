package cn.itmtx.ezcache.common.utils;

public class OsUtil {

    /**
     * 单例
     */
    private static OsUtil instance = new OsUtil();

    /**
     * 是否是 linux 系统
     */
    private static boolean isLinux;


    static {
        String os = System.getProperty("os.name");
        String linux = "LINUX";
        if ((os != null) && (os.toUpperCase().indexOf(linux) > -1)) {
            isLinux = true;
        } else {
            isLinux = false;
        }
    }

    public static OsUtil getInstance() {
        return instance;
    }

    public boolean isLinux() {
        return isLinux;
    }
}
