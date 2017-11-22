package com.mymall.service;

import com.github.pagehelper.PageInfo;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.Product;
import com.mymall.vo.ProductDetailVo;

/**
 * Created by lamZe on 2017/11/21.<br>
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> manageProductDetial(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);

    ServerResponse<ProductDetailVo> getProductDetial(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int PageSize,String orderBy);
}
