package com.cloud.cms.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.cloud.common.core.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品溯源对象 cms_product_trace
 * 
 * @author cloud
 * @date 2026-08-29
 */
@Data
public class CmsProductTrace implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 溯源ID */
    private String id;
    /** 商品ID */
    @Excel(name = "商品ID")
    private String productId;
    /** 商品名称 */
    @Excel(name = "商品名称")
    @TableField(exist = false)
    private String productName;
    /** 产地 */
    @Excel(name = "产地")
    private String origin;
    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNo;
    /** 生长记录 */
    @Excel(name = "生长记录")
    private String growRecord;
    /** 加工信息 */
    @Excel(name = "加工信息")
    private String processInfo;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
