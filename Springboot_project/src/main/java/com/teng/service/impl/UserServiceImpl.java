package com.teng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.teng.VO.ResultVO;
import com.teng.entity.User;
import com.teng.form.LoginForm;
import com.teng.mapper.UserMapper;
import com.teng.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author admin
 * @since 2026-06-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;  //BeefApplication中也需要定向到数据

    @Override
    public ResultVO login(LoginForm loginForm) {  //login具体实现
        // 1、判断用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();  //mybatisplus语法，新建了一个动态拆卸你的语句，Users指的是数据表
        queryWrapper.eq("Username", loginForm.getUsername());  //比较字段，参数1：定位字段列，参数2：前端所传的数据字段
        User user = this.userMapper.selectOne(queryWrapper);  // 经过查询的到一个Users类,将Username匹配的那一行数据全部查询出来了。
        ResultVO resultVO = new ResultVO();  //用来存储查询的返回值
        if (user == null) {
            resultVO.setCode(-1);  //表示用户不存在
        } else {  //用户存在
            // 2、密码是否正确
            if (!user.getPassword().equals(loginForm.getPassword())) {  // 如果前端输入密码与数据库存储密码不匹配
                resultVO.setCode(-2);  //表示密码出错
            }
            else {  //正常登录
                if(user.getPassword().equals("admin")){
                    resultVO.setCode(0);  //管理员正常登录
                }
                else{
                    resultVO.setCode(1);  //普通用户正常登录
                }
                resultVO.setData(user);  //将查询到的值，放进resultVO的Data中
            }
        }
        return resultVO;
    }
}
