package com.teng.service;

import com.teng.VO.ResultVO;
import com.teng.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teng.form.LoginForm;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2026-06-01
 */
public interface UserService extends IService<User> {
    public ResultVO login(LoginForm loginForm);
}
