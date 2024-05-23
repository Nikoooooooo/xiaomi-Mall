package com.yc.xiaomi.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yc.bean.VoucherItemInfo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface VoucherItemInfoDAO extends BaseMapper<VoucherItemInfo> {

    //获取所有已经                            TODO:此处的vid应该修改为从前端获取,但由于时间问题就简化处理
    @Select("select uid from tb_voucheriteminfo where vid=3")
    List<Integer> selectUid();
}
