package com.mymall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mymall.common.Const;
import com.mymall.common.ResponseCode;
import com.mymall.common.ServerResponse;
import com.mymall.dao.CartMapper;
import com.mymall.dao.ProductMapper;
import com.mymall.pojo.Cart;
import com.mymall.pojo.Product;
import com.mymall.service.ICartService;
import com.mymall.util.BigDecimalUtil;
import com.mymall.util.PropertiesUtil;
import com.mymall.vo.CartProductVo;
import com.mymall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * Created by lamZe on 2017/11/22.<br>
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;


    //添加商品到购物车
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count) {
        if(productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if(cart == null) {
            //该产品不在购物车中，需要新增
            Cart cartItem = new Cart();
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setQuantity(count);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else {
            //产品已存在。数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    //封装购物车VO
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        //获取改用户的物品列表
        List<Cart> cartList = cartMapper.selectCartsByUserId(userId);
        ArrayList<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");
        //将cart列表编程cartProductVo列表 给前端
        if(CollectionUtils.isNotEmpty(cartList)) {
            for(Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());
                //填充商品详情
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()) {
                        //库存充足
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity().doubleValue()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if(cartItem.getChecked() == Const.Cart.CHECKED) {
                    //如果已经勾选，增加整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        if(userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) ==0;
    }


    //更新购物车对应商品得数量
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count) {
        if(productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if(cart !=null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    //删除购物车商品
    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    //列出购物车所有商品
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    //全选或反选
    public ServerResponse<CartVo> selectOrUnSelectAll(Integer userId,Integer checked) {
        cartMapper.checkedOrUnCheckedProduct(userId,null,checked);
        return this.list(userId);
    }

    //勾选或不勾选商品
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer checked,Integer productId) {
        cartMapper.checkedOrUnCheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    //获取购物车物品总数
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if(userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));

    }
}
