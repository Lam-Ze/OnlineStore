package com.mymall.controller.backend;

import com.mymall.common.Const;
import com.mymall.common.ServerResponse;
import com.mymall.pojo.Category;
import com.mymall.pojo.User;
import com.mymall.service.ICategoryService;
import com.mymall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by lamZe on 2017/11/21.<br>
 */

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    IUserService iUserService;

    @Autowired
    ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //校验管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //添加目录
            return iCategoryService.addCategory(parentId,categoryName);
        }else {
            return ServerResponse.createByErrorMessage("不是管理员，无权限操作");
        }
    }

    @RequestMapping(value = "set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,String categoryName,int categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //校验管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //更新目录
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else {
            return ServerResponse.createByErrorMessage("不是管理员，无权限操作");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session, @RequestParam(value="categoryId",defaultValue = "0")int categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //校验管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //获取目录
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("不是管理员，无权限操作");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue = "0") int categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //校验管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //查询当前节点及其递归子节点
            return iCategoryService.selectCategoryAndChildenById(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("不是管理员，无权限操作");
        }
    }

}
