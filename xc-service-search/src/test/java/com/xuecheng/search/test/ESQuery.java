package com.xuecheng.search.test;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by hotwater on 2018/7/8.
 */


@SpringBootTest
@RunWith(SpringRunner.class)
public class ESQuery {
    /**
     * 此处的测试类主要涉及到关于ES的查询测试
     ES的查询主要分为：下属查询均具有分页查询
     1.MatchAllQuery   //全匹配查询
     2.TermQuery      //最小单元匹配查询---精准匹配  仅支持一个字段
     3.MatchQuery    //仅支持一个字段
     4.MultiQuery    //支持多字段匹配
     5.布尔查询      //利用布尔查询进行可以将上述的单字段匹配查询和多字段匹配查询进行综合
     6.过滤查询      在查询的基础之上进行结果过滤，一般用于多重查询即综合查询时候，比如布尔查询经常会将单字段的查询替换为过滤查询，有利于提升效率
     7.排序查询     //指定字段进行排序，另外可以改变他们字段的权重值以改变他们的排序
     8.boost提升查询
     */

    @Autowired
    private RestHighLevelClient  restHighLevelClient;


    /**
     * 1.MatchAllQuery
     *          特点：会查询指定类型下的全部文档
     */
    @Test
    public  void  testMatchAllQuery() {

        //1.创建查询请求对象
        SearchRequest  searchRequest=new SearchRequest();
        //2.指定文档类型即type
        searchRequest.indices("xc_course");
        searchRequest.types("doc");
        //3.构建查询资源构造器---本质是对于查询的配置指定
        SearchSourceBuilder  searchSourceBuilder  = new SearchSourceBuilder();
        //3.1指定查询两个字段数据即_source数据源的数据只采用两个
        searchSourceBuilder.fetchSource(new String []{"name","description"},new String []{});
        //3.2指定分页起始索引0开始
       // searchSourceBuilder.from(0);
        //3.3指定分页页面数据量
        //searchSourceBuilder.size(1);
        //3.4指定查询的方式：即查询的策略，全匹配查询-----此种全查询的模式适用于首页默认查询的时候
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //4.0查询配置与查询请求对象绑定
        searchRequest.source(searchSourceBuilder);
        //5.0将上述的查询对象置于restHighLevelClient
        try {
            //5.1执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //5.2查询结果解析---拿到第一层的hits数据
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.err.println("MatchAllQuery查询出来的数据总数量："+totalHits);
            //5.3查询结果解析---拿到第二层的hits数据---内部是真正的doc数据
            SearchHit[] searchHitsHits = searchHits.getHits();
            //5.4遍历每一个数据文档中的内容
            for (SearchHit hits:searchHitsHits) {
                //5.5此处还可以进行几种形式的遍历集合，
                //第一个就是利用将查询的数据进行转换为map对象然后进行处理
                //第二个就是可以利用api获取所有的字段名称，然后进行遍历也是可以的，不过下面这个即第一种就可以
                Map<String, Object> sourceAsMap = hits.getSourceAsMap();
                System.err.println("\t\t\t"+"遍历真正的数据");
                for (String key :sourceAsMap.keySet()) {
                    System.err.println(key+"--------"+sourceAsMap.get(key));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 /**
     * 2.TermQuery
  *                 特点：Term Query为精确查询，在搜索时会整体匹配关键字，不再将关键字分词。
     */
    @Test
    public  void  testTermQuery() {

        //1.创建查询请求对象
        SearchRequest  searchRequest=new SearchRequest();
        //2.指定文档类型即type
        searchRequest.indices("xc_course");
        searchRequest.types("doc");
        //3.构建查询资源构造器---本质是对于查询的配置指定
        SearchSourceBuilder  searchSourceBuilder  = new SearchSourceBuilder();
        //3.1指定查询两个字段数据即_source数据源的数据只采用两个
        searchSourceBuilder.fetchSource(new String []{"name","description"},new String []{});
        //3.2指定分页起始索引0开始
       // searchSourceBuilder.from(0);
        //3.3指定分页页面数据量
        //searchSourceBuilder.size(1);
        //3.4指定查询的方式：即查询的策略，全匹配查询-----此种全查询的模式适用于首页默认查询的时候
//        searchSourceBuilder.query(QueryBuilders.termQuery("name","开发"));

        //3.4指定查询方式，还可以进行id查询的多个匹配查询-----但是注意与上述的方法是有区别的，此处是termsQuery匹配多个id，上面的是termQuery匹配一个查询
        String [] ids=new String[]{"1","2","3"};//构建一个三个id值的数组
        //需要将数据转换为list集合方可达到效果
        List<String> idlist =Arrays.asList(ids);
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",idlist));//此处必须是集合才可以哟
        //4.0查询配置与查询请求对象绑定
        searchRequest.source(searchSourceBuilder);
        //5.0将上述的查询对象置于restHighLevelClient
        try {
            //5.1执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //5.2查询结果解析---拿到第一层的hits数据
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.err.println("TermQuery查询出来的数据总数量："+totalHits);
            //5.3查询结果解析---拿到第二层的hits数据---内部是真正的doc数据
            SearchHit[] searchHitsHits = searchHits.getHits();
            //5.4遍历每一个数据文档中的内容
            for (SearchHit hits:searchHitsHits) {
                //5.5此处还可以进行几种形式的遍历集合，
                //第一个就是利用将查询的数据进行转换为map对象然后进行处理
                //第二个就是可以利用api获取所有的字段名称，然后进行遍历也是可以的，不过下面这个即第一种就可以
                Map<String, Object> sourceAsMap = hits.getSourceAsMap();
                System.err.println("\t\t\t"+"遍历真正的数据");
                for (String key :sourceAsMap.keySet()) {
                    System.err.println(key+"--------"+sourceAsMap.get(key));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/**
     * 3.MatchQuery
  *                 特点： 1. match Query即全文检索，它的搜索方式是先将搜索字符串分词，再使用各各词条从索引中搜索。
                           2. match query与Term query区别是match query在搜索前先将搜索关键字分词，再拿各各词语去索引中搜索。
                postman查询：
                                 {
                                 "query": {
                                 "match" : {
                                 "description" : {
                                 "query" : "spring开发",
                                 "operator" : "or"
                                 }
                                 }
                                 }
                                 }
                           3.  query：搜索的关键字，对于英文关键字如果有多个单词则中间要用半角逗号分隔，而对于中文关键字中间可以用逗号分隔也可以不用。
                           4.  operator：or 表示 只要有一个词在文档中出现则就符合条件，and表示每个词都在文档中出现则才符合条件。
                           5.  设置"minimum_should_match": "80%"表示，三个词在文档的匹配占比为80%，即3*0.8=2.4，向下取整得2，表
                                示至少有两个词在文档中要匹配成功。
     */
    @Test
    public  void  testMatchQuery() {

        //1.创建查询请求对象
        SearchRequest  searchRequest=new SearchRequest();
        //2.指定文档类型即type
        searchRequest.indices("xc_course");
        searchRequest.types("doc");
        //3.构建查询资源构造器---本质是对于查询的配置指定
        SearchSourceBuilder  searchSourceBuilder  = new SearchSourceBuilder();
        //3.1指定查询两个字段数据即_source数据源的数据只采用两个
        searchSourceBuilder.fetchSource(new String []{"name","description"},new String []{});
        //3.2指定分页起始索引0开始
       // searchSourceBuilder.from(0);
        //3.3指定分页页面数据量
        //searchSourceBuilder.size(1);
        //3.4指定查询的方式：即查询的策略，匹配查询策略，MatchQuery会对查询关键字进行分词之后进行查询，将查询的结果集汇总起来
            //此种查询是可以指定拆分后的字段分别查询后是按照什么方式汇总，是去交集还是并集，这里就是operator指定了and表示需要全部匹配，or表示匹配一个即可
//        searchSourceBuilder.query(QueryBuilders.matchQuery("name","框架基础").operator(Operator.OR));
        //3.4指定查询的方式：即查询的策略，匹配查询策略，MatchQuery会对查询关键字进行分词之后进行查询，将查询的结果集汇总起来
        //这里matchQuery还提供给了匹配度的查询，也就是说我们的查询关键字拆分之后比如拆分成3个，那么我们就可以指定查询的匹配度，比如：匹配度70%那么就是至少匹配两个
        //计算公式为：2/3=66.7%≈70%   那么就是如此了，下面进行测试匹配度查询
//        searchSourceBuilder.query(QueryBuilders.matchQuery("name","spring开发基础").operator(Operator.OR).minimumShouldMatch("80%"));
        searchSourceBuilder.query(QueryBuilders.matchQuery("name","spring开发基础").minimumShouldMatch("80%"));//与上述查询结果一样
        //4.0查询配置与查询请求对象绑定
        searchRequest.source(searchSourceBuilder);
        //5.0将上述的查询对象置于restHighLevelClient
        try {
            //5.1执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //5.2查询结果解析---拿到第一层的hits数据
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.err.println("MatchQuery查询出来的数据总数量："+totalHits);
            //5.3查询结果解析---拿到第二层的hits数据---内部是真正的doc数据
            SearchHit[] searchHitsHits = searchHits.getHits();
            //5.4遍历每一个数据文档中的内容
            for (SearchHit hits:searchHitsHits) {
                //5.5此处还可以进行几种形式的遍历集合，
                //第一个就是利用将查询的数据进行转换为map对象然后进行处理
                //第二个就是可以利用api获取所有的字段名称，然后进行遍历也是可以的，不过下面这个即第一种就可以
                Map<String, Object> sourceAsMap = hits.getSourceAsMap();
                System.err.println("\t\t\t"+"遍历真正的数据");
                for (String key :sourceAsMap.keySet()) {
                    System.err.println(key+"--------"+sourceAsMap.get(key));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * MultiQuery查询：
     *                特点：
     *                  1.上边学习的termQuery和matchQuery一次只能匹配一个Field，本节学习multiQuery，一次可以匹配多个字段。
     *                  2.单项匹配是在一个field中去匹配，多项匹配是拿关键字去多个Field中匹配。
     *                  3.匹配多个字段时可以提升字段的boost（权重）来提高得分
     *                  注意提升boost的写法：
     *                  postman:    {
                                     "query": {
                                     "multi_match" : {
                                     "query" : "spring框架",
                                     "minimum_should_match": "50%",
                                     "fields": [ "name^10", "description" ]
                                     }
                                     }
                                     }
     */
    @Test
    public  void  testMultiQuery() {

        //1.创建查询请求对象
        SearchRequest  searchRequest=new SearchRequest();
        //2.指定文档类型即type
        searchRequest.indices("xc_course");
        searchRequest.types("doc");
        //3.构建查询资源构造器---本质是对于查询的配置指定
        SearchSourceBuilder  searchSourceBuilder  = new SearchSourceBuilder();
        //3.1指定查询两个字段数据即_source数据源的数据只采用两个
        searchSourceBuilder.fetchSource(new String []{"name","description"},new String []{});
        //3.2指定分页起始索引0开始
       // searchSourceBuilder.from(0);
        //3.3指定分页页面数据量
        //searchSourceBuilder.size(1);
        //3.4指定查询的方式：即查询的策略，匹配查询策略，MatchQuery会对查询关键字进行分词之后进行查询，将查询的结果集汇总起来
        //当我们进行multiQuery查询的时候，我们可以指定多个字段进行查询，比如我们查询的 字段是为了页面展示，但是我们应该优先让展示的
        //进行靠前，但是可能在得分情况下并不靠前，比如下面的案例，实际上我们更侧重于展示name为spring css 的数据文档，但是由于在搜索字段中
        //有一个description字段中出现上述的spring和css的频率较高，因此就会出现得分高，因此就会造成页面展示的效果不理想，可能name和spring css没有什么直接关系
        //因此此处我们可以借助于ES提供的字段加权提升分值如下所示，我觉得分析清楚了。。。当然此处也可以指定最小匹配度进行查询，至于分页与请求对象有关，不再赘述。
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css", "name", "description").minimumShouldMatch("10%"));
//        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("Spring css", "name", "description").minimumShouldMatch("10%").field("name",10));
        //4.0查询配置与查询请求对象绑定
        searchRequest.source(searchSourceBuilder);
        //5.0将上述的查询对象置于restHighLevelClient
        try {
            //5.1执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //5.2查询结果解析---拿到第一层的hits数据
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.err.println("MultiQuery查询出来的数据总数量："+totalHits);
            //5.3查询结果解析---拿到第二层的hits数据---内部是真正的doc数据
            SearchHit[] searchHitsHits = searchHits.getHits();
            //5.4遍历每一个数据文档中的内容
            for (SearchHit hits:searchHitsHits) {
                //5.5此处还可以进行几种形式的遍历集合，
                //第一个就是利用将查询的数据进行转换为map对象然后进行处理
                //第二个就是可以利用api获取所有的字段名称，然后进行遍历也是可以的，不过下面这个即第一种就可以
                //拿到得分：
                float hitsScore = hits.getScore();
                System.err.println("得分：：：：：：：hitsScore:"+hitsScore);
                Map<String, Object> sourceAsMap = hits.getSourceAsMap();
                System.err.println("\t\t\t"+"遍历真正的数据");
                for (String key :sourceAsMap.keySet()) {
                    System.err.println(key+"--------"+sourceAsMap.get(key));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *        布尔查询：
                         布尔查询对应于Lucene的BooleanQuery查询，实现将多个查询组合起来。
                         三个参数：
                         must：文档必须匹配must所包括的查询条件，相当于 “AND” should：文档应该匹配should所包括的查询条件其
                         中的一个或多个，相当于 "OR" must_not：文档不能匹配must_not所包括的该查询条件，相当于“NOT”
                         分别使用must、should、must_not测试下边的查询：
                    此处的布尔查询实际上是实现了过滤功能，但是是在查询的时候实现过滤极大的效率低下，因此我们可以引入过滤查询
                    过滤查询实在查询数据的基础之上进行的查询，因此没有store的数据的计算，会降低资源的消耗，节省时间提升效率，
                    下面就进行过滤查询的测试
     */
    @Test
    public  void  testBooleanQuery() {

        //1.创建查询请求对象
        SearchRequest  searchRequest=new SearchRequest();
        //2.指定文档类型即type
        searchRequest.indices("xc_course");
        searchRequest.types("doc");
        //3.构建查询资源构造器---本质是对于查询的配置指定
        SearchSourceBuilder  searchSourceBuilder  = new SearchSourceBuilder();
        //3.1指定查询两个字段数据即_source数据源的数据只采用两个
        searchSourceBuilder.fetchSource(new String []{"name","description"},new String []{});
        //3.2指定分页起始索引0开始
       // searchSourceBuilder.from(0);
        //3.3指定分页页面数据量
        //searchSourceBuilder.size(1);
        //3.4指定查询的方式：即查询的策略，匹配查询策略，BooleanQuery可以实现单字段查询和多字段查询的综合
       //3.4.1构建多字段查询
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description").minimumShouldMatch("10%");
        //3.4.2构建单字段查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "spring");
        //3.4.3构建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //3.4.4将上述构建的多字段查询和单字段查询绑定到布尔查询对象上
        boolQueryBuilder.must(termQueryBuilder);
        boolQueryBuilder.must(multiMatchQueryBuilder);
        //3.4.5将布尔查询注入到查询资源构建对象上
        searchSourceBuilder.query(boolQueryBuilder);
        //4.0查询配置与查询请求对象绑定
        searchRequest.source(searchSourceBuilder);
        //5.0将上述的查询对象置于restHighLevelClient
        try {
            //5.1执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //5.2查询结果解析---拿到第一层的hits数据
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.err.println("BooleanQuery查询出来的数据总数量："+totalHits);
            //5.3查询结果解析---拿到第二层的hits数据---内部是真正的doc数据
            SearchHit[] searchHitsHits = searchHits.getHits();
            //5.4遍历每一个数据文档中的内容
            for (SearchHit hits:searchHitsHits) {
                //5.5此处还可以进行几种形式的遍历集合，
                //第一个就是利用将查询的数据进行转换为map对象然后进行处理
                //第二个就是可以利用api获取所有的字段名称，然后进行遍历也是可以的，不过下面这个即第一种就可以
                //拿到得分：
                float hitsScore = hits.getScore();
                System.err.println("得分：：：：：：：hitsScore:"+hitsScore);
                Map<String, Object> sourceAsMap = hits.getSourceAsMap();
                System.err.println("\t\t\t"+"遍历真正的数据");
                for (String key :sourceAsMap.keySet()) {
                    System.err.println(key+"--------"+sourceAsMap.get(key));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *              过滤查询：
     *                          过虑是针对搜索的结果进行过虑，过虑器主要判断的是文档是否匹配，不去计算和判断文档的匹配度得分，所以过
                                虑器性能比查询要高，且方便缓存，推荐尽量使用过虑器去实现查询或者过虑器和查询共同使用。

                过滤查询依赖于布尔查询-----重点
     */
  @Test
    public  void  testFilterQuery() {

        //1.创建查询请求对象
        SearchRequest  searchRequest=new SearchRequest();
        //2.指定文档类型即type
        searchRequest.indices("xc_course");
        searchRequest.types("doc");
        //3.构建查询资源构造器---本质是对于查询的配置指定
        SearchSourceBuilder  searchSourceBuilder  = new SearchSourceBuilder();
        //3.1指定查询两个字段数据即_source数据源的数据只采用两个
        searchSourceBuilder.fetchSource(new String []{"name","price","description"},new String []{});
        //3.2指定分页起始索引0开始
       // searchSourceBuilder.from(0);
        //3.3指定分页页面数据量
        //searchSourceBuilder.size(1);
        //3.4指定查询的方式：即查询的策略，匹配查询策略，BooleanQuery可以实现单字段查询和多字段查询的综合
       //3.4.1构建多字段查询
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description").minimumShouldMatch("10%");
        //3.4.2构建单字段查询
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "spring");//此处条件筛选转换为过滤查询上
        //3.4.3构建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //3.4.4将上述构建的多字段查询和单字段查询绑定到布尔查询对象上
//        boolQueryBuilder.must(termQueryBuilder);//此处条件筛选转换为过滤查询上
        boolQueryBuilder.must(multiMatchQueryBuilder);

        //追加验证进行过滤查询
            boolQueryBuilder.filter(QueryBuilders.termQuery("name","spring"));
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte("0.0").lte("100.0"));//price在[5.0,10.0]区间


        //3.4.5将布尔查询注入到查询资源构建对象上
        searchSourceBuilder.query(boolQueryBuilder);
        //4.0查询配置与查询请求对象绑定
        searchRequest.source(searchSourceBuilder);
        //5.0将上述的查询对象置于restHighLevelClient
        try {
            //5.1执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //5.2查询结果解析---拿到第一层的hits数据
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.err.println("FilterQuery查询出来的数据总数量："+totalHits);
            //5.3查询结果解析---拿到第二层的hits数据---内部是真正的doc数据
            SearchHit[] searchHitsHits = searchHits.getHits();
            //5.4遍历每一个数据文档中的内容
            for (SearchHit hits:searchHitsHits) {
                //5.5此处还可以进行几种形式的遍历集合，
                //第一个就是利用将查询的数据进行转换为map对象然后进行处理
                //第二个就是可以利用api获取所有的字段名称，然后进行遍历也是可以的，不过下面这个即第一种就可以
                //拿到得分：
                float hitsScore = hits.getScore();
                System.err.println("得分：：：：：：：hitsScore:"+hitsScore);
                Map<String, Object> sourceAsMap = hits.getSourceAsMap();
                System.err.println("\t\t\t"+"遍历真正的数据");
                for (String key :sourceAsMap.keySet()) {
                    System.err.println(key+"--------"+sourceAsMap.get(key));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 排序
     */
    @Test
    public  void  testSortQuery() {
        //1.创建查询请求对象
        SearchRequest  searchRequest=new SearchRequest();
        //2.指定文档类型即type
        searchRequest.indices("xc_course");
        searchRequest.types("doc");
        //3.构建查询资源构造器---本质是对于查询的配置指定
        SearchSourceBuilder  searchSourceBuilder  = new SearchSourceBuilder();
        //3.1指定查询两个字段数据即_source数据源的数据只采用两个
        searchSourceBuilder.fetchSource(new String []{"name","price","studymodel","description"},new String []{});
        //3.2指定分页起始索引0开始
       // searchSourceBuilder.from(0);
        //3.3指定分页页面数据量
        //searchSourceBuilder.size(1);
        //3.4指定查询的方式：即查询的策略，匹配查询策略，BooleanQuery可以实现单字段查询和多字段查询的综合
       //3.4.1构建多字段查询
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description").minimumShouldMatch("10%");
        //3.4.2构建单字段查询
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "spring");//此处条件筛选转换为过滤查询上
        //3.4.3构建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //3.4.4将上述构建的多字段查询和单字段查询绑定到布尔查询对象上
//        boolQueryBuilder.must(termQueryBuilder);//此处条件筛选转换为过滤查询上
        boolQueryBuilder.must(multiMatchQueryBuilder);

        //追加验证进行过滤查询
//            boolQueryBuilder.filter(QueryBuilders.termQuery("name","spring"));
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte("0.0").lte("100.0"));//price在[0.0,100.0]区间
        //其中一种配置方法：
        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        //另外一种配置方法:
//        searchSourceBuilder.sort(new FieldSortBuilder("studymodel").order(SortOrder.DESC));
        //3.4.5将布尔查询注入到查询资源构建对象上
        searchSourceBuilder.query(boolQueryBuilder);
        //4.0查询配置与查询请求对象绑定
        searchRequest.source(searchSourceBuilder);
        //5.0将上述的查询对象置于restHighLevelClient
        try {
            //5.1执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //5.2查询结果解析---拿到第一层的hits数据
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.err.println("FilterQuery查询出来的数据总数量："+totalHits);
            //5.3查询结果解析---拿到第二层的hits数据---内部是真正的doc数据
            SearchHit[] searchHitsHits = searchHits.getHits();
            //5.4遍历每一个数据文档中的内容
            for (SearchHit hits:searchHitsHits) {
                //5.5此处还可以进行几种形式的遍历集合，
                //第一个就是利用将查询的数据进行转换为map对象然后进行处理
                //第二个就是可以利用api获取所有的字段名称，然后进行遍历也是可以的，不过下面这个即第一种就可以
                //拿到得分：
                float hitsScore = hits.getScore();
                System.err.println("得分：：：：：：：hitsScore:"+hitsScore);
                Map<String, Object> sourceAsMap = hits.getSourceAsMap();
                System.err.println("\t\t\t"+"遍历真正的数据");
                for (String key :sourceAsMap.keySet()) {
                    System.err.println(key+"--------"+sourceAsMap.get(key));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 /**
     *              高亮显示:
  *
     */
    @Test
    public  void  testHighLightQuery() {
        //1.创建查询请求对象
        SearchRequest  searchRequest=new SearchRequest();
        //2.指定文档类型即type
        searchRequest.indices("xc_course");
        searchRequest.types("doc");
        //3.构建查询资源构造器---本质是对于查询的配置指定
        SearchSourceBuilder  searchSourceBuilder  = new SearchSourceBuilder();
        //3.1指定查询两个字段数据即_source数据源的数据只采用两个
        searchSourceBuilder.fetchSource(new String []{"name","price","studymodel","description"},new String []{});
        //3.2指定分页起始索引0开始
       // searchSourceBuilder.from(0);
        //3.3指定分页页面数据量
        //searchSourceBuilder.size(1);
        //3.4指定查询的方式：即查询的策略，匹配查询策略，BooleanQuery可以实现单字段查询和多字段查询的综合
       //3.4.1构建多字段查询
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description").minimumShouldMatch("10%");
        //3.4.2构建单字段查询
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "spring");//此处条件筛选转换为过滤查询上
        //3.4.3构建布尔查询对象-----此处的错误代码---引发了很长时间的错误，此处是  BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();//错误代码要警惕哟哟哟
        //3.4.4将上述构建的多字段查询和单字段查询绑定到布尔查询对象上
//        boolQueryBuilder.must(termQueryBuilder);//此处条件筛选转换为过滤查询上
        boolQueryBuilder.must(multiMatchQueryBuilder);

        //追加验证进行过滤查询
//            boolQueryBuilder.filter(QueryBuilders.termQuery("name","spring"));
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte("0.0").lte("100.0"));//price在[0.0,100.0]区间
        //其中一种配置方法：
        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        //另外一种配置方法:
//        searchSourceBuilder.sort(new FieldSortBuilder("studymodel").order(SortOrder.DESC));


        //追加配置高亮显示
        //创建高亮构建者对象
        HighlightBuilder  highlightBuilder  = new HighlightBuilder();
        //设置高亮显示的前后缀
        highlightBuilder.preTags("<div  color='red'  >");
        highlightBuilder.postTags("</div>");
        //设置高亮字段方式1
        highlightBuilder.field("name");
        //设置高亮字段方式2
        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
        //将高亮配置在资源配置对象上
        searchSourceBuilder.highlighter(highlightBuilder);

        //3.4.5将布尔查询注入到查询资源构建对象上
        searchSourceBuilder.query(boolQueryBuilder);
        //4.0查询配置与查询请求对象绑定
        searchRequest.source(searchSourceBuilder);
        //5.0将上述的查询对象置于restHighLevelClient
        try {
            //5.1执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //5.2查询结果解析---拿到第一层的hits数据
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.err.println("FilterQuery查询出来的数据总数量："+totalHits);
            //5.3查询结果解析---拿到第二层的hits数据---内部是真正的doc数据
            SearchHit[] searchHitsHits = searchHits.getHits();

            //查询高亮部分


            //5.4遍历每一个数据文档中的内容
            for (SearchHit hits:searchHitsHits) {

                //5.5此处还可以进行几种形式的遍历集合，
                //第一个就是利用将查询的数据进行转换为map对象然后进行处理
                //第二个就是可以利用api获取所有的字段名称，然后进行遍历也是可以的，不过下面这个即第一种就可以
                //拿到得分：
                float hitsScore = hits.getScore();
                System.err.println("得分：：：：：：：hitsScore:"+hitsScore);
                Map<String, Object> sourceAsMap = hits.getSourceAsMap();
                System.err.println("\t\t\t"+"遍历真正的数据");
                for (String key :sourceAsMap.keySet()) {
                    System.err.println(key+"--------"+sourceAsMap.get(key));
                }

                //此处处理高亮部分：
                Map<String, HighlightField> highlightFields =hits.getHighlightFields();
                if(highlightFields!=null) {
                    for (String hkey : highlightFields.keySet()) {
                        HighlightField highlightField = highlightFields.get(hkey);
                        if(highlightField!=null){
                            Text[] fragments = highlightField.getFragments();
                            StringBuilder  newvalue=new StringBuilder();
                            for (Text txt:fragments
                                 ) {
                                newvalue.append(txt);
                            }
                            System.err.println(hkey+"高亮部分："+newvalue);
                        }
                    }
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
