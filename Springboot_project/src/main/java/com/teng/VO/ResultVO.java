package com.teng.VO;

import lombok.Data;

@Data  // 用@Data注释，自动生成getter,setter等繁琐代码。
public class ResultVO <T>{
    private Integer code;  // 标记状态
    private T data;  // 设为泛型T，类型随传输进的数据确定
}
