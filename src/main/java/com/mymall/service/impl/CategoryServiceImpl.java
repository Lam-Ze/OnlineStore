package com.mymall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mymall.common.ServerResponse;
import com.mymall.dao.CategoryMapper;
import com.mymall.pojo.Category;
import com.mymall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by lamZe on 2017/11/21.<br>
 */

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{
    @Autowired
    CategoryMapper categoryMapper;

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public ServerResponse addCategory(Integer parentId,String categoryName) {
        if(parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if(rowCount >0) {
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName) {
        if(categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount >0) {
            return ServerResponse.createBySuccessMessage("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> categoris = categoryMapper.getCategoryByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoris)) {
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoris);
    }

    public ServerResponse<List<Integer>> selectCategoryAndChildenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null) {
            for(Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归算法算出子节点
    private Set<Category> findChildCategory(Set<Category> categories,Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null) {
            categories.add(category);
        }

        //结束条件
        List<Category> categoryList = categoryMapper.getCategoryByParentId(categoryId);
        for(Category categoryItem :categoryList) {
            findChildCategory(categories,categoryItem.getId());
        }
        return categories;
    }

}
