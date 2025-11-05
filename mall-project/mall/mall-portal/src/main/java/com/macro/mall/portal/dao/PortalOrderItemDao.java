package com.macro.mall.portal.dao;

import com.macro.mall.model.OmsOrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单商品信息管理自定义Dao
 * Created by macro on 2018/9/3.
 */
@Mapper
public interface PortalOrderItemDao {
    /**
     * 批量插入
     */
    @Insert({
            "<script>",
            "INSERT INTO oms_order_item(order_id,product_id,product_name,product_sku_id,product_price,product_quantity,product_pic)",
            "VALUES",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.orderId},#{item.productId},#{item.productName},#{item.productSkuId},#{item.productPrice},#{item.productQuantity},#{item.productPic})",
            "</foreach>",
            "</script>"
    })
    int insertList(@Param("list") List<OmsOrderItem> list);
}
