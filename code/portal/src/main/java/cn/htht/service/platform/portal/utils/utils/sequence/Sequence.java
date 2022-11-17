package cn.htht.service.platform.portal.utils.utils.sequence;

import cn.htht.service.platform.portal.user.service.UserAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/10/29
 */
public class Sequence {
    private static Sequence instance = new Sequence();
    private String sequenceCode = null;
    private Sequence(){}
    public static Sequence getInstance(){
        return instance;
    }

    public synchronized String getSequenceCode() {
        return sequenceCode;
    }

    public synchronized void setSequenceCode(String sequenceCode) {
        this.sequenceCode = sequenceCode;
    }

    public synchronized String addAndGetSequenceCode(){
        if (this.sequenceCode == null){
            return null;
        }
        this.sequenceCode = String.valueOf(Integer.parseInt(this.sequenceCode) + 1);
        return this.sequenceCode;
    }
}
