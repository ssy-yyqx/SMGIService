package cn.htht.service.platform.portal.component.manager.factory;

import cn.htht.service.platform.portal.constant.Constants;
import cn.htht.service.platform.portal.entity.system.SystemLog;
import cn.htht.service.platform.portal.entity.system.SystemLoginLog;
import cn.htht.service.platform.portal.system.service.ISystemLogService;
import cn.htht.service.platform.portal.system.service.ISystemLoginLogService;
import cn.htht.service.platform.portal.utils.utils.LogUtils;
import cn.htht.service.platform.portal.utils.utils.ServletUtils;
import cn.htht.service.platform.portal.utils.utils.ip.AddressUtils;
import cn.htht.service.platform.portal.utils.utils.ip.IpUtils;
import cn.htht.service.platform.portal.utils.utils.spring.SpringUtils;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 *
 * @author htht
 */
public class AsyncFactory {
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息
     * @param args     列表T
     * @return 任务task
     */
    public static TimerTask recordLoginlog(final String username, final String status, final String message,
                                           final Object... args) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = IpUtils.getIpAddress(ServletUtils.getRequest());
        return new TimerTask() {
            @Override
            public void run() {
                String address = AddressUtils.getRealAddressByIP(ip);
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(address);
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                // 打印信息到日志
                sys_user_logger.info(s.toString(), args);
                // 获取客户端操作系统
                String os = userAgent.getOperatingSystem().getName();
                // 获取客户端浏览器
                String browser = userAgent.getBrowser().getName();

                // 封装对象
                SystemLoginLog loginLog = new SystemLoginLog();
                loginLog.setId(IdUtils.fastSimpleUUID());
                loginLog.setLoginUsername(username);
                loginLog.setLoginIp(ip);
                loginLog.setBrowser(browser);
                loginLog.setOs(os);
                loginLog.setMsg(message);
                if (Constants.LOGOUT.equals(status)) {
                    loginLog.setOperateType(Constants.LOGOUT_CODE);
                }
                // 日志状态
                if (Constants.LOGIN_SUCCESS.equals(status) || Constants.LOGOUT.equals(status)) {
                    loginLog.setStatus(Constants.SUCCESS_CODE);
                    loginLog.setOperateType(Constants.LOGIN_CODE);
                } else if (Constants.LOGIN_FAIL.equals(status)) {
                    loginLog.setStatus(Constants.FAIL_CODE);
                    loginLog.setOperateType(Constants.LOGIN_CODE);
                }
                // 插入数据
                SpringUtils.getBean(ISystemLoginLogService.class).insertLoginLog(loginLog);
            }
        };
    }

    /**
     * 操作日志记录
     *
     * @param systemLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordSystemLog(final SystemLog systemLog) {
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(ISystemLogService.class).insertSystemLog(systemLog);
            }
        };
    }
}
