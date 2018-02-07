package com.chaimm.rcmd.analyzer;

import com.chaimm.rcmd.classifier.Classifier;
import com.chaimm.rcmd.dao.ArticleDAO;
import com.chaimm.rcmd.entity.Article;
import com.chaimm.rcmd.entity.Category;
import com.chaimm.rcmd.redis.RedisDAO;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author 大闲人柴毛毛
 * @date 2018/2/7 上午10:27
 *
 * @description 数据解析器的顶层父类
 */
public abstract class Analyzer {

    @Autowired
    protected ArticleDAO articleDAO;

    @Autowired
    protected RedisDAO redisDAO;

    @Autowired
    protected Classifier classifier;


    /**
     * 数据解析器
     * @param articleList 文章列表（此时文章中只有URL和title，但不排除有些解析器已经包含了文章的很多信息）
     */
    public void analysis(List<Article> articleList) {

        // 过滤掉已经存在的文章（根据标题过滤）
        articleList = filterExistArticle(articleList);

        // 获取文章详细信息
        articleList = batchAnalysisArticleDetail(articleList);

        // 计算权重
        articleList = calWeight(articleList);

        // 分类
        articleList = batchClassify(articleList);

        // 插入DB
//        insertDB(articleList);

        // 插入Redis
        insertRedis(articleList);
    }

    /**
     * 批量分类
     * @param articleList
     * @return
     */
    protected List<Article> batchClassify(List<Article> articleList) {
        if (!CollectionUtils.isEmpty(articleList)) {
            for (Article article : articleList) {
                List<Category> categoryList = classifier.classify(article.getTitle(), article.getTagList());
                article.setCategoryList(categoryList);
            }
        }
        return articleList;
    }

    /**
     * 过滤掉已经存在的文章
     * PS：根据标题过滤
     * @param articleList 文章列表
     * @return
     */
    protected List<Article> filterExistArticle(List<Article> articleList) {
        List<Article> articleListFiltered = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(articleList)) {
            for (Article article : articleList) {
                // 若是新文章，则添加
                if (isNewArticle(article)) {
                    articleListFiltered.add(article);
                }
            }
        }

        return articleListFiltered;
    }

    /**
     * 判断该文章是否是新文章
     * @param article
     * @return
     */
    protected boolean isNewArticle(Article article) {
        return redisDAO.hasArticle(article.getTitle());
    }

    protected abstract void insertRedis(List<Article> articleList);

    protected abstract void insertDB(List<Article> articleList);

    protected abstract List<Article> batchAnalysisArticleDetail(List<Article> articleList);

    /**
     * 文章权重计算器
     * @param articleList 文章列表
     * @return
     */
    protected List<Article> calWeight(List<Article> articleList) {
        // TODO 权重计算器尚未完成
        return articleList;
    }

}
