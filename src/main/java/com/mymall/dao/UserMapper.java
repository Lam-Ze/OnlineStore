package com.mymall.dao;

import com.mymall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username); //检查是否存在该用户名

    int checkEmail(String email); //检查是否存在Email

    User selectLogin(@Param("username") String username, @Param("password") String password);//登录

    String selectQuestionByUsername(String username);//获得用户的问题

    int checkAnswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer); //校验问题的答案

    int updatePasswordByUsername(@Param("username") String username,@Param("passwordNew") String passwordNew); //更新用户密码

    int checkPassword(@Param("userid")int userid,@Param("passwordOld")String passwordOld); //查询是否密码正确

    int checkEmailByUserId(@Param("userid")int userid,@Param("email") String email);//查询email是否被占用
}