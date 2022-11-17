package cn.htht.service.platform.portal.user.mapper;

import cn.htht.service.platform.portal.common.BasicMapper;
import cn.htht.service.platform.portal.entity.user.UserAppointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserAppointmentMapper extends BasicMapper<UserAppointment> {

    List<UserAppointment> getAppointmentList(@Param("keyword") String keyword, @Param("status") Integer status, @Param("userId") String userId);

    int cancelAppointment(@Param("appointmentId") String appointmentId);

    UserAppointment getAppointmentByaAppointmentId(String appointmentId);

    int updatePassState(@Param("appointmentId") String appointmentId, @Param("detail")String detail);

    int addAppointment(UserAppointment userAppointment);

    String getMaxSequenceCode();

    int updateNotPassState(@Param("appointmentId") String appointmentId,@Param("detail") String detail);
}
