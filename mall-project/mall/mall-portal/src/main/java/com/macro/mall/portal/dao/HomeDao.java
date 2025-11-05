package com.macro.mall.portal.dao;

import com.macro.mall.model.CmsSubject;
import com.macro.mall.model.PmsBrand;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.portal.domain.FlashPromotionProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 首页内容管理自定义Dao
 * Created by macro on 2019/1/28.
 */
@Mapper
public interface HomeDao {
    /**
     * 获取推荐品牌
     */
    @Select("SELECT * FROM pms_brand WHERE  show_status = 1 LIMIT #{offset}, #{limit}")
    List<PmsBrand> getRecommendBrandList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * //获取秒杀商品
     *
     * @Select("SELECT p. FROM sms_flash_promotion_product_relation r LEFT JOIN pms_product p ON r.product_id = p.id WHERE r.flash_promotion_id = #{flashPromotionId} AND r.flash_promotion_session_id = #{sessionId} LIMIT 0, #{limit}") List<FlashPromotionProduct> getFlashProductList(@Param("flashPromotionId") Long flashPromotionId, @Param("sessionId") Long sessionId);
     * <p>
     * 获取新品推荐
     */
    /**
     * 获取新品推荐
     */
    @Select("SELECT * FROM pms_product WHERE publish_status = 1 AND new_status = 1 ORDER BY id DESC LIMIT #{offset}, #{limit}")
    List<PmsProduct> getNewProductList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 获取人气推荐
     */
    @Select("SELECT * FROM pms_product WHERE publish_status = 1 ORDER BY id DESC LIMIT #{offset}, #{limit}")
    List<PmsProduct> getHotProductList(@Param("offset") Integer offset, @Param("limit") Integer limit);


    /**
     * 获取推荐专题
     */
    @Select("SELECT * FROM cms_subject WHERE show_status = 1 ORDER BY id DESC LIMIT #{offset}, #{limit}")
    List<CmsSubject> getRecommendSubjectList(@Param("offset") Integer offset, @Param("limit") Integer limit);


    @Select({
            "<script>",
            "SELECT p.id, p.name, p.price, p.pic,",
            "fp.flash_promotion_price AS flashPromotionPrice,",
            "fp.flash_promotion_count AS flashPromotionCount,",
            "fp.flash_promotion_limit AS flashPromotionLimit",
            "FROM sms_flash_promotion_product_relation fp",
            "JOIN pms_product p ON fp.product_id = p.id",
            "WHERE fp.flash_promotion_id = #{flashPromotionId}",
            "AND fp.flash_promotion_session_id = #{flashPromotionSessionId}",
            "AND p.publish_status = 1 AND p.delete_status = 0",
            "</script>"
    })
    List<FlashPromotionProduct> getFlashProductList(@Param("flashPromotionId") Long flashPromotionId,
                                                    @Param("flashPromotionSessionId") Long flashPromotionSessionId);

}

