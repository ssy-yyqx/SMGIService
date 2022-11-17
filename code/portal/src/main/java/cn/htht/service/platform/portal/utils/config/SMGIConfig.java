package cn.htht.service.platform.portal.utils.config;

import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author htht
 */
@Component
@ConfigurationProperties(prefix = "smgi")
public class SMGIConfig {
    /**
     * 项目名称
     */
    private String name;

    /**
     * 版本
     */
    private String version;

    /**
     * 版权年份
     */
    private String copyrightYear;


    /**
     * 上传路径
     */
    private static String profile;

    /**
     * 获取地址开关
     */
    private static boolean addressEnabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCopyrightYear() {
        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear) {
        this.copyrightYear = copyrightYear;
    }

    public static String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        SMGIConfig.profile = profile;
    }

    public static boolean isAddressEnabled() {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled) {
        SMGIConfig.addressEnabled = addressEnabled;
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getProfile() + "/avatar";
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath() {
        return getProfile() + "/download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return getProfile() + "/upload";
    }

    /**
     * 获取上传路径
     */
    public static String getBlobFilePath() {
        return getProfile() + "/blob";
    }

    /**
     * 获取一个新的随机上传路径
     */
    public static String createUploadPath() {
        return getProfile() + "/upload/" + IdUtils.fastUUID();
    }

    /**
     * word 临时文件夹
     */
    public static String getFixedModelTempPath() {
        return getProfile() + "/download/tempWord/";
    }
}
