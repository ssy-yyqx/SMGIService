package cn.htht.service.platform.portal.common;

import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.constant.HttpStatus;
import cn.htht.service.platform.portal.utils.core.page.PageDomain;
import cn.htht.service.platform.portal.utils.core.page.TableDataInfo;
import cn.htht.service.platform.portal.utils.core.page.TableSupport;
import cn.htht.service.platform.portal.utils.utils.DateUtils;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.sql.SqlUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 *
 * @author htht
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected ResponseEntity toAjax(int rows) {
        return rows > 0 ? ResponseEntity.success() : ResponseEntity.error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected ResponseEntity toAjax(boolean result) {
        return result ? success() : error();
    }

    /**
     * 返回成功
     */
    public ResponseEntity success() {
        return ResponseEntity.success();
    }

    /**
     * 返回失败消息
     */
    public ResponseEntity error() {
        return ResponseEntity.error();
    }

    /**
     * 返回成功消息
     */
    public ResponseEntity success(String message) {
        return ResponseEntity.success(message);
    }

    /**
     * 返回失败消息
     */
    public ResponseEntity error(String message) {
        return ResponseEntity.error(message);
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StringUtils.format("redirect:{}", url);
    }
}
