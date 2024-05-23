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
@TableName("tb_voucheriteminfo")
public class VoucherItemInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer vid;
    private Integer uid;
    private String start_date;
    private String end_date;

}
