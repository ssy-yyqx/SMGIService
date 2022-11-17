package cn.htht.service.platform.portal.user.service.impl;

import cn.htht.service.platform.portal.common.AbstractBaseService;
import cn.htht.service.platform.portal.entity.user.UserAppointment;
import cn.htht.service.platform.portal.manager.mapper.ModuleMapper;
import cn.htht.service.platform.portal.user.mapper.UserAppointmentMapper;
import cn.htht.service.platform.portal.user.service.UserAppointmentService;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.sequence.Sequence;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

/**
 * @ClassName UserOfflineAppointmentRecordServiceImpl
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Service
public class UserAppointmentServiceImpl extends AbstractBaseService<UserAppointment> implements UserAppointmentService {

    @Autowired
    private UserAppointmentMapper userAppointmentMapper;

    @Autowired
    private ModuleMapper moduleMapper;

    /**
     * 用户获取预约列表
     */
    @Override
    public PageInfo<UserAppointment> getAppointmentList(String keyword, Integer status, String userId, Integer pageSize, Integer pageNumber) {
        PageHelper.startPage(pageNumber, pageSize);
        List<UserAppointment> userAppointmentList = userAppointmentMapper.getAppointmentList(keyword, status, userId);
        return new PageInfo<>(userAppointmentList);
    }

    /**
     * 用户撤销预约列表
     */
    @Override
    public int cancelAppointment(String appointmentId) {
        return userAppointmentMapper.cancelAppointment(appointmentId);
    }

    /**
     * 按ID获取预约信息
     */
    @Override
    public UserAppointment getAppointmentByaAppointmentId(String appointmentId) {
        return userAppointmentMapper.getAppointmentByaAppointmentId(appointmentId);
    }

    @Override
    public int updatePassState(String appointmentId, String detail) {
        return userAppointmentMapper.updatePassState(appointmentId, detail);
    }

    @Override
    public int updateNotPassState(String appointmentId, String detail) {
        return userAppointmentMapper.updateNotPassState(appointmentId, detail);
    }

    /**
     * 预约申请
     */
    @Override
    public int addAppointment(UserAppointment userAppointment) {
        return userAppointmentMapper.addAppointment(userAppointment);
    }

    /**
     * 获取最新sequenceCode
     */
    @Override
    public synchronized String getMaxSequenceCode() {
        if (Sequence.getInstance().getSequenceCode() == null) {
            String sequenceCode = userAppointmentMapper.getMaxSequenceCode();
            if (StringUtils.isEmpty(sequenceCode)) {
                Calendar calendar = Calendar.getInstance();
                sequenceCode = "" + calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DATE) + "001";
                Sequence.getInstance().setSequenceCode(sequenceCode);
                return sequenceCode;
            }
            Sequence.getInstance().setSequenceCode(sequenceCode);
        }
        return Sequence.getInstance().addAndGetSequenceCode();
    }
}
