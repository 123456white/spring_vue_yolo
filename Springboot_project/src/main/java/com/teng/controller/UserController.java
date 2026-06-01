package com.teng.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teng.VO.ResultVO;
import com.teng.form.LoginForm;
import com.teng.service.UdpSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.web.bind.annotation.RequestMapping;

import com.teng.service.UserService;
import com.teng.entity.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2026-06-01
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UdpSenderService udpSenderService;
    private List<String> receivedMessages = new ArrayList<>();

    @Resource
    private UserService userService;

    @GetMapping("/login")  //是查询语句，增加注释前端调用时可以根据路径调用方法
    public ResultVO login(LoginForm loginForm){  //将ResultVO作为login()的返回值类型,loginForm为前端传过来的数据
        ResultVO resultVO = this.userService.login(loginForm);  //调用了service曾的login方法,对前端数据loginForm判断，并返回resultVO
        return resultVO;  //返回给前端使用
    }


    @GetMapping
    public List<User> findAll(){
        return userService.list();
    }

    @PostMapping("/saveorupdate")
    public boolean save(@RequestBody User user){
        return userService.saveOrUpdate(user);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id){
        return userService.removeById(id);
    }

    @PostMapping("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids){
        return userService.removeByIds(ids);
    }

    @GetMapping("/{id}")
    public User findOne(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @GetMapping("/page")
    public Page<User> findPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam (defaultValue = "") String username,
                               @RequestParam (defaultValue = "") String email,
                               @RequestParam (defaultValue = "") String phone){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if(!"".equals(username)){
            queryWrapper.like("username",username);
        }
        if(!"".equals(email)){
            queryWrapper.like("email",email);
        }
        if(!"".equals(phone)){
            queryWrapper.like("phone",phone);
        }
        return userService.page(new Page<>(pageNum, pageSize),queryWrapper);
    }

    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        //从数据库查询出所有的数据
        List<User> list = userService.list();
        // 通过工具类创建writer写出到磁盘路径
        // ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/用户信息。xlsx");
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 自定标题别名
        writer.addHeaderAlias("username","用户名");
        writer.addHeaderAlias("password","密码");
        writer.addHeaderAlias("phone","电话");
        writer.addHeaderAlias("email","邮件");

        //一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list,true);

        //设置浏览器相应格式
        response.setContentType("application/vnd.openxmlformats-officedocument.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out,true);
        out.close();
        writer.close();
    }

    /**
     * excel 导入接口
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Boolean imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<User> list = reader.readAll(User.class);
        System.out.println(list);
        userService.saveBatch(list);
        return true;
    }

    /**
     * esp8266 消息发送接口
     */
    @GetMapping("/sendudpmessage")
    public String sendUdpMessage() {
        udpSenderService.sendUdpMessage("这是一条来自Spring Boot的UDP消息");
        return "UDP消息已发送";
    }
    /**
     * esp8266 消息接收接口
     */
//    public void addReceivedMessage(String message) {
//        receivedMessages.add(message);
//    }

    @GetMapping("/messages")
    public List<String> getReceivedMessages() {
        return receivedMessages;
    }


}

