package cn.itmtx.ezcache.notifier;

/**
 * TODO 通知模块
 */
public interface INotifier {

    /**
     * 发送邮件
     */
    void sendEmail();

    /**
     * 发送短信
     */
    void sendMessage();
}
