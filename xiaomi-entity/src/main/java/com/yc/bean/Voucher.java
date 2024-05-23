package com.yc.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_voucher")
public class Voucher {
    @TableId(type = IdType.AUTO)
    private Integer id;
    //优惠券额度
    private Integer deno;
    private Integer condition;
    private Integer stock;

}
