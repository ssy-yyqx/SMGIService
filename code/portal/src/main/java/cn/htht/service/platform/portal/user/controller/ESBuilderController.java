package cn.htht.service.platform.portal.user.controller;

import cn.htht.service.platform.portal.common.bean.ResponseEntity;
import cn.htht.service.platform.portal.constant.ModuleConstant;
import cn.htht.service.platform.portal.entity.dto.ContentDto;
import cn.htht.service.platform.portal.entity.manager.Content;
import cn.htht.service.platform.portal.entity.manager.Module;
import cn.htht.service.platform.portal.entity.user.Builder;
import cn.htht.service.platform.portal.entity.user.DocEntity;
import cn.htht.service.platform.portal.entity.user.HotWord;
import cn.htht.service.platform.portal.entity.vo.ContentVo;
import cn.htht.service.platform.portal.entity.vo.ServiceDataVo;
import cn.htht.service.platform.portal.manager.service.ContentService;
import cn.htht.service.platform.portal.manager.service.ModuleService;
import cn.htht.service.platform.portal.user.mapper.BuilderMapper;
import cn.htht.service.platform.portal.user.mapper.DocEntityMapper;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import cn.htht.service.platform.portal.utils.utils.elasticsearch.EsConstant;
import cn.htht.service.platform.portal.utils.utils.elasticsearch.EsUtiil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName ESBuilderController
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Slf4j
@RestController
@Api(tags = "ES模糊搜索")
@RequestMapping("/es")
public class ESBuilderController {

    @Autowired
    BuilderMapper builderDao;

    @Autowired
    DocEntityMapper docEntityMapper;

    @Autowired
    ModuleService moduleService;

    @Autowired
    ContentService contentService;

    @Autowired
    ElasticsearchRestTemplate elasticsearchTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页模糊查询,类似mysql中like "%word%"的模糊匹配
     *
     * @param value
     * @return
     */
    @ApiOperation("ES模糊搜索")
    @RequestMapping(value = "/queryLike", method = RequestMethod.GET)
    public ResponseEntity queryByKeyWord(@RequestParam("value") String value) {
        //入库Redis
        setIntoRedis(value);
        if (!EsUtiil.isChinese(value)) {
            value = EsUtiil.replace(value);
        }
        if (EsUtiil.isEnglishAll(value) || EsUtiil.isNumberAll(value) || EsUtiil.isEngAndChar(value)) {
            value = value.toLowerCase();
        }
        WildcardQueryBuilder queryBuilder;

        if (EsUtiil.isContainChinese(value)) {
            queryBuilder = QueryBuilders.wildcardQuery(EsConstant.TITLE_KEY_WORD, EsConstant.REQEX_X + value + EsConstant.REQEX_X);
        } else {
            queryBuilder = QueryBuilders.wildcardQuery(EsConstant.TITLE_, EsConstant.REQEX_X + value + EsConstant.REQEX_X);
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(boolQueryBuilder.must(queryBuilder));
        functionScoreQueryBuilder.scoreMode(FunctionScoreQuery.ScoreMode.FIRST);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(functionScoreQueryBuilder).build();
        // .withPageable(PageRequest.of(startpage, pageSize)).withSort(SortBuilders.scoreSort().order(SortOrder.ASC)).build();

        log.info("DSL语句： " + searchQuery.getQuery().toString());
        IndexCoordinates indexCoordinateName = elasticsearchTemplate.getIndexCoordinatesFor(DocEntity.class);

        List<DocEntity> resultList = elasticsearchTemplate.queryForList(searchQuery, DocEntity.class, indexCoordinateName);
        Collections.sort(resultList, Comparator.comparing(DocEntity::getSysDate, Comparator.reverseOrder()));
        //取离当前时间最近的前三条
        if (resultList.size() > 3) {
            resultList = resultList.subList(0, 3);
        }
        Map<String, List<DocEntity>> map = resultList.stream().collect(
                Collectors.groupingBy(
                        doc -> doc.getColumnType()
                ));
        //System.out.println(JSONObject.toJSON(map));

        return ResponseEntity.success(JSONObject.toJSON(map));
    }

    /**
     * 入库Redis
     *
     * @param value
     */
    private void setIntoRedis(String value) {
        //以下是操作Redis
        Calendar calendar = Calendar.getInstance();
        //往后推一天
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        //往后推30天
        //calendar.add(Calendar.DAY_OF_YEAR, 30);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //晚上十二点与当前时间的毫秒差
        Long timeOut = (calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000;
        redisTemplate.expire("hotWord", timeOut, TimeUnit.SECONDS);
        // 加入排序set
        redisTemplate.opsForZSet().incrementScore("hotWord", value, 1);
    }

    /**
     * 获取热词前五位
     *
     * @return
     */
    @ApiOperation("词汇热搜")
    @GetMapping(value = "get/hotwordtopfive")
    public ResponseEntity getHotWord() {
        List<HotWord> hotWordList = getHotWords();
        if (hotWordList.size() > 5) {
            hotWordList = hotWordList.subList(0, 5);
        }
        return ResponseEntity.success(hotWordList);
    }

    @NotNull
    private List<HotWord> getHotWords() {
        List<HotWord> hotWordList = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Object>> typedTupleSet = redisTemplate.opsForZSet().reverseRangeByScoreWithScores("hotWord", 1, Integer.MAX_VALUE);
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTupleSet.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();
            String value = (String) typedTuple.getValue();
            int score = (int) Math.ceil(typedTuple.getScore());
            HotWord hotWord = new HotWord(score, value);
            hotWordList.add(hotWord);
        }
        return hotWordList;
    }

    /**
     * 获取热词前五位
     *
     * @return
     */
    @ApiOperation("关键字-词汇热搜")
    @GetMapping(value = "get/hotwordtopfive/{data}")
    public ResponseEntity getHotWord2(@PathVariable(value = "data") String data) {
        List<HotWord> hotWordList = getHotWords();
        //List<String> valueList = hotWordList.stream().map(HotWord -> HotWord.getValue()).collect(Collectors.toList());
        hotWordList = hotWordList.stream().filter(hotWord -> hotWord.getValue() != null && hotWord.getValue().indexOf(data) > -1).collect(Collectors.toList());

        if (hotWordList.size() > 5) {
            hotWordList = hotWordList.subList(0, 5);
        }
        return ResponseEntity.success(hotWordList);
    }

    /**
     * 方式1
     * <p>
     * 根据ID获取单个索引
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String get(Long id) {
        Optional<Builder> get = builderDao.findById(id);
        return get.get().toString();
    }

    @RequestMapping(value = "/getdoc", method = RequestMethod.GET)
    public String getDoc(String id) {
        Optional<DocEntity> get = docEntityMapper.findById(id);
        return get.get().toString();
    }

    /**
     * 方式1
     * <p>
     * 通过term进行全量完全匹配查询
     * 根据传入属性值，检索指定属性下是否有属性值完全匹配的
     * <p>
     * 例如：
     * name:中国人
     * 那么查询不会进行分词，就是按照  包含完整的  中国人  进行查询匹配
     * <p>
     * 此时ik中文分词 并没有起作用【此时是在@Field注解 指定的ik分词器】
     * 例如存入  张卫健  三个字，以ik_max_word 分词存入，查询也指定以ik查询，但是  以张卫健  查询 没有结果
     * 以   【张】   或   【卫】   或   【健】  查询 才有结果，说明分词是以默认分词器 进行分词 ，也就是一个中文汉字 进行一个分词的效果。
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/searchNameByTerm", method = RequestMethod.GET)
    public List<Builder> searchNameByTerm(String name) {
        TermQueryBuilder termBuilder = QueryBuilders.termQuery("buildName", name);
        Iterable<Builder> search = builderDao.search(termBuilder);
        List<Builder> list = new ArrayList<>();
        while (search.iterator().hasNext()) {
            list.add(search.iterator().next());
        }
        return list;
    }

    /**
     * @param builder
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public boolean delete(@RequestBody Builder builder) {
        builderDao.deleteById(builder.getId());
        return true;
    }

    /**
     *
     * 同步es数据库数据
     *
     * @return
     */
    @ApiOperation("ES数据同步")
    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    public ResponseEntity sync(@ApiParam(name = "secretKey", value = "密码") @RequestParam(name = "secretKey", required = false) String secretKey) {
        if (!secretKey.equals("syncSmgi47")) {
            return ResponseEntity.error("密码错误，无法同步");
        }
        // 获取所有内容doc
        List<DocEntity> contentList = contentService.selectAllContent();
        moduleService.setDocEntity(contentList);
        elasticsearchTemplate.save(contentList);
        return ResponseEntity.success("同步完成！");
    }

    /**
     * 获取栏目下的所有内容列表
     */
    @ApiOperation("查看更多")
    @PostMapping("/content/more")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "最顶层栏目id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "serviceTemplateType", value = "模板类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "pageSize", paramType = "query"),
            @ApiImplicitParam(name = "pageNumber", value = "pageNumber", paramType = "query")
    })
    public ResponseEntity getContentByModuleId(@RequestBody @ApiIgnore JSONObject jsonObject) {
        Module module = moduleService.selectById(jsonObject.getString("moduleId"));
        int pageSize = jsonObject.getInteger("pageSize");
        int pageNumber = jsonObject.getInteger("pageNumber");
        String serviceTemplateType = jsonObject.getString("serviceTemplateType");

        if (StringUtils.equals(serviceTemplateType, ModuleConstant.INFO_TEMPLATE_TYPE) || StringUtils.equals(serviceTemplateType, ModuleConstant.SINGLE_TEMPLATE_TYPE)) {
            PageInfo<ContentVo> contentList = moduleService.getContentListByModule(module, pageSize, pageNumber);
            return ResponseEntity.success(contentList);
        } else if(StringUtils.equals(serviceTemplateType, ModuleConstant.SERVICE_TEMPLATE_TYPE) || StringUtils.equals(serviceTemplateType, ModuleConstant.DATA_TEMPLATE_TYPE)){
            PageInfo<ServiceDataVo> contentList = moduleService.getServiceDataByModuleId(module, pageSize, pageNumber);
            return ResponseEntity.success(contentList);
        }
        return ResponseEntity.error("不存在业务相关的模板类型，请检查！");
    }
}
