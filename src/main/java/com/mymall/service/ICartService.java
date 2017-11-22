package com.mymall.service;

import com.mymall.common.ServerResponse;
import com.mymall.vo.CartVo;

/**
 * Created by lamZe on 2017/11/22.<br>
 */
public interface ICartService {

    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count);

    ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> selectOrUnSelectAll(Integer userId,Integer checked);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer checked,Integer productId);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
