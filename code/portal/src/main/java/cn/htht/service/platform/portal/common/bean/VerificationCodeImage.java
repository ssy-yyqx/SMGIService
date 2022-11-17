package cn.htht.service.platform.portal.common.bean;

/**
 * @ClassName VerificationCodeImage
 * @Description 验证码实体类
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class VerificationCodeImage {
    /**
     * 验证码
     */
    private String code;
    /**
     * 图片二进制码
     */
    private String image;
    /**
     * 是否生成成功
     */
    private boolean success = false;

    public VerificationCodeImage(String code, Object data, boolean success) {
        this.code = code;
        this.image = image;
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
