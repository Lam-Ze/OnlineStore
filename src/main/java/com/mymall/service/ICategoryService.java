package com.mymall.service;

import com.mymall.common.ServerResponse;
import com.mymall.pojo.Category;

import java.util.List;
import java.util.Set;


/**
 * Created by lamZe on 2017/11/21.<br>
 */
public interface ICategoryService {

    ServerResponse addCategory(Integer parentId, String categoryName);

    ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildenById(Integer categoryId);

}
