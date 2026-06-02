package com.teng.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teng.VO.ResultVO;
import com.teng.form.LoginForm;
import com.teng.service.UdpSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
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

    String uniqueFileName; //用于上传图片的名称
    String resultfilename; //用于预测结果图片名称
    /**
     * 图片上传接口，从web传来测试图片
     *
     * @param file 前端上传的图片文件
     * @return 上传结果信息
     */
    @PostMapping("/uploadImage")
    public String uploadImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        try {
            // 定义图片保存路径，可以根据实际情况调整
            String uploadDir = "D:\\project\\spring_vue_yolo\\yolo_web\\web";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 获取原始文件名
            uniqueFileName =file.getOriginalFilename();
//            System.out.println("上传的图片文件名: " + uniqueFileName);
            File uploadFile = new File(uploadDir + File.separator + uniqueFileName);
            file.transferTo(uploadFile);
            return "图片上传成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "图片上传失败";
        }
    }

    /**
     * 图片回传接口
     * @return 回传预测结果图片
     */
    @GetMapping("/getPredictedImage")
    public ResponseEntity<byte[]> getPredictedImage() throws IOException {
        try {
            String pythonPath = "D:\\Conda\\envs\\yolo\\python.exe";
            String[] pythonArgs = new String[]{pythonPath, "D:\\project\\spring_vue_yolo\\yolo_web\\serve.py", uniqueFileName};
            ProcessBuilder pb = new ProcessBuilder(pythonArgs);
            pb.redirectErrorStream(true);
            Process proc = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = in.readLine())!= null) {
                resultfilename = line;
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
            System.out.println("Python script has finished execution.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        String imagePath = "D:\\project\\spring_vue_yolo\\yolo_web\\result\\"+resultfilename;
        File file = new File(imagePath);
        Path path = file.toPath();
        byte[] imageBytes = Files.readAllBytes(path);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("attachment", resultfilename);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

//    /**
//     * esp8266 消息发送接口
//     */
//    @GetMapping("/sendudpmessage")
//    public String sendUdpMessage() {
//        udpSenderService.sendUdpMessage("这是一条来自Spring Boot的UDP消息");
//        return "UDP消息已发送";
//    }
//    /**
//     * esp8266 消息接收接口
//     */
////    public void addReceivedMessage(String message) {
////        receivedMessages.add(message);
////    }
//
//    @GetMapping("/messages")
//    public List<String> getReceivedMessages() {
//        return receivedMessages;
//    }


}

