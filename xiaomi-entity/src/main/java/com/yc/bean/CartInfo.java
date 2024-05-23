package com.yc.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("cartinfo")
public class CartInfo  implements Serializable {
    @TableId(type = IdType.AUTO)
    private String cno;
    private Integer uno;
    private Integer gno;
    private Integer num;

}
