package com.cloud.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.cloud.oms.mapper.OmsOrderMapper;
import com.cloud.oms.domain.OmsOrder;
import com.cloud.oms.service.IOmsOrderService;

/**
 * 订单Service业务层处理
 * 
 * @author cloud
 * @date 2026-03-15
 */
@Service
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements IOmsOrderService {

}
