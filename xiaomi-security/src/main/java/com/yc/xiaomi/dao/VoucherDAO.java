package com.yc.xiaomi.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yc.bean.Voucher;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface VoucherDAO extends BaseMapper<Voucher> {

    //查询所有优惠券信息
    @Select("select * from tb_voucher")
    List<Voucher> selectAll();

    //减少优惠券
    @Select("update tb_voucher set stock=stock-1 where id=#{id};")
    Voucher DeclineVoucherStock(Integer id);
}
