package com.mymall.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mymall.common.Const;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.dao.CategoryMapper;
import com.mymall.dao.ProductMapper;
import com.mymall.pojo.Category;
import com.mymall.pojo.Order;
import com.mymall.pojo.Product;
import com.mymall.service.ICategoryService;
import com.mymall.service.IProductService;
import com.mymall.util.DateTimeUtil;
import com.mymall.util.PropertiesUtil;
import com.mymall.vo.ProductDetailVo;
import com.mymall.vo.ProductListVo;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lamZe on 2017/11/21.<br>
 */

@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 增加或更新产品信息
     * @param product
     * @return
     */
    public ServerResponse saveOrUpdateProduct(Product product) {
        if(product !=null) {
            if(StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
            if(product.getId() !=null) {
                int rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount >0) {
                    return ServerResponse.createBySuccessMessage("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("更新产品失败");
            }else {
                int rowCount = productMapper.insert(product);
                if(rowCount >0) {
                    return ServerResponse.createBySuccessMessage("新增产品成功");
                }
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }

        return ServerResponse.createByErrorMessage("增加或更新产品参数不正确");
    }

    /**
     * 设置产品状态
     * @param productId
     * @param status
     * @return
     */
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if(productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误");
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount >0) {
            return ServerResponse.createBySuccessMessage("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    /**
     * 后台-获取产品细节
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> manageProductDetial(Integer productId) {
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    /**
     * 将product装配成productDetialVo
     * @param product
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());


        //软编码设置图片服务器地址
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.lamze.cn/"));


        //获取
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null) {
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //处理日期
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;

    }

    /**
     * 使用PageHelper封装分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize) {
        //startPage-start
        //填充sql查询
        //pageHelper-收尾
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectList();

        ArrayList<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : products) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);



    }

    /**
     * product装配成productListVo
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setId(product.getId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.lamze.cn/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setName(product.getName());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());

        return productListVo;
    }

    /**
     * 根据productName、productId搜索产品
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if(StringUtils.isNotBlank(productName)) {
          productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> products = productMapper.selectByNameAndProductId(productName, productId);
        ArrayList<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : products) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        PageInfo<ProductListVo> productListVoPageInfo = new PageInfo<>(productListVoList);
        return ServerResponse.createBySuccess(productListVoPageInfo);

    }

    /**
     * 获取产品细节
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> getProductDetial(Integer productId) {
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        if(product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }


        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }


    /**
     * 根据keyword、categoryId获取PageInfo  使用PageHelper
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param PageSize
     * @param orderBy
     * @return
     */
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int PageSize,String orderBy) {
        if(StringUtils.isBlank(keyword) &&categoryId ==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        List<Integer> categoryIdList = new ArrayList<>();
        if(categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)) {
                //没有该分类，并且没有关键字，返回空结果集
                PageHelper.startPage(pageNum,PageSize);
                List<ProductListVo> productListVos = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVos);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum,PageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)) {
            if(Const.ProductListOrderBy.Price_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> products = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);

        List<ProductListVo> productListVos = Lists.newArrayList();
        for(Product product :products) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVos.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);

        return ServerResponse.createBySuccess(pageInfo);

    }

}
