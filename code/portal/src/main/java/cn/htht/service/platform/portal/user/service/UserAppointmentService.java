package cn.htht.service.platform.portal.user.service;

import cn.htht.service.platform.portal.common.BaseService;
import cn.htht.service.platform.portal.entity.user.UserAppointment;
import com.github.pagehelper.PageInfo;

public interface UserAppointmentService extends BaseService<UserAppointment> {
    /**
     * 用户获取预约列表
     * */
    PageInfo<UserAppointment> getAppointmentList(String keyword, Integer status, String userId, Integer pageSize, Integer pageNumber);
    /**
     * 用户撤销预约列表
     * */
    int cancelAppointment(String appointmentId);
    /**
     * 按ID获取预约信息
     * */
    UserAppointment getAppointmentByaAppointmentId(String appointmentId);
    /**
     * 预约申请
     * */
    int addAppointment(UserAppointment userAppointment);

    /**
     * 获取最新sequenceCode
     * */
    String getMaxSequenceCode();

    int updatePassState(String appointmentId, String detail);

    int updateNotPassState(String appointmentId,String detail);

}
