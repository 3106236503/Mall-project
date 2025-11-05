package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.model.OmsCartItem;
import com.macro.mall.model.PmsProductFullReduction;
import com.macro.mall.model.PmsProductLadder;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.portal.dao.PortalProductDao;
import com.macro.mall.portal.domain.CartPromotionItem;
import com.macro.mall.portal.domain.PromotionProduct;
import com.macro.mall.portal.service.OmsPromotionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by macro on 2018/8/27.
 * 促销管理Service实现类
 */
@Service
public class OmsPromotionServiceImpl implements OmsPromotionService {
    @Autowired
    private PortalProductDao portalProductDao;

    @Autowired
    private PmsSkuStockMapper skuStockMapper;


    @Override
    public List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList) {
        //1.先根据productId对CartItem进行分组，以spu为单位进行计算优惠
        Map<Long, List<OmsCartItem>> productCartMap = groupCartItemBySpu(cartItemList);
        //2.查询所有商品的优惠相关信息
        List<PromotionProduct> promotionProductList = getPromotionProductList(cartItemList);
        //3.根据商品促销类型计算商品促销优惠价格
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        for (Map.Entry<Long, List<OmsCartItem>> entry : productCartMap.entrySet()) {
            Long productId = entry.getKey();
            PromotionProduct promotionProduct = getPromotionProductById(productId, promotionProductList);
            List<OmsCartItem> itemList = entry.getValue();
            Integer promotionType = promotionProduct.getPromotionType();
            if (promotionType == 1) {
                // 单品促销
                for (OmsCartItem item : itemList) {
                    CartPromotionItem cartPromotionItem = new CartPromotionItem();
                    BeanUtils.copyProperties(item, cartPromotionItem);
                    cartPromotionItem.setPromotionMessage("单品促销");

                    // 商品原价-促销价
                    PmsSkuStock skuStock = getOriginalPrice(promotionProduct, item.getProductSkuId());

                    // 增加空值检查
                    if (skuStock == null) {
                        // 可以选择跳过当前项或者设置默认值
                        cartPromotionItem.setPrice(BigDecimal.ZERO);
                        cartPromotionItem.setReduceAmount(BigDecimal.ZERO);
                        cartPromotionItem.setRealStock(0);
                        cartPromotionItem.setIntegration(0);
                        cartPromotionItem.setGrowth(0);
                    } else {
                        BigDecimal originalPrice = skuStock.getPrice();
                        // 单品促销使用原价
                        cartPromotionItem.setPrice(originalPrice);
                        cartPromotionItem.setReduceAmount(originalPrice.subtract(skuStock.getPromotionPrice()));
                        cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                    }

                    cartPromotionItemList.add(cartPromotionItem);
                }
            } else if (promotionType == 3) {
                //打折优惠
                int count = getCartItemCount(itemList);
                PmsProductLadder ladder = getProductLadder(count, promotionProduct.getProductLadderList());
                if (ladder != null) {
                    for (OmsCartItem item : itemList) {
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        BeanUtils.copyProperties(item, cartPromotionItem);
                        String message = getLadderPromotionMessage(ladder);
                        cartPromotionItem.setPromotionMessage(message);
                        //商品原价-折扣*商品原价
                        PmsSkuStock skuStock = getOriginalPrice(promotionProduct, item.getProductSkuId());
                        BigDecimal originalPrice = skuStock.getPrice();
                        BigDecimal reduceAmount = originalPrice.subtract(ladder.getDiscount().multiply(originalPrice));
                        cartPromotionItem.setReduceAmount(reduceAmount);
                        cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        cartPromotionItemList.add(cartPromotionItem);
                    }
                } else {
                    handleNoReduce(cartPromotionItemList, itemList, promotionProduct);
                }
            } else if (promotionType == 4) {
                //满减
                BigDecimal totalAmount = getCartItemAmount(itemList, promotionProductList);
                PmsProductFullReduction fullReduction = getProductFullReduction(totalAmount, promotionProduct.getProductFullReductionList());
                if (fullReduction != null) {
                    // 在满减优惠计算部分（约第70-90行），找到设置库存的代码并修复：
                    for (OmsCartItem item : itemList) {
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        BeanUtils.copyProperties(item, cartPromotionItem);
                        cartPromotionItem.setPromotionMessage(getFullReductionPromotionMessage(fullReduction));
                        //满减价格计算
                        BigDecimal reduceAmount = fullReduction.getReducePrice();
                        cartPromotionItem.setReduceAmount(reduceAmount);

                        // 修复：添加健壮的库存设置逻辑
                        PmsSkuStock skuStock = getOriginalPrice(promotionProduct, item.getProductSkuId());
                        if (skuStock != null) {
                            cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
                        } else {
                            // 如果skuStock为null，尝试从数据库查询库存
                            PmsSkuStock dbSkuStock = skuStockMapper.selectByPrimaryKey(item.getProductSkuId());
                            if (dbSkuStock != null) {
                                cartPromotionItem.setRealStock(dbSkuStock.getStock() - dbSkuStock.getLockStock());
                            } else {
                                cartPromotionItem.setRealStock(0);
                            }
                        }

                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        cartPromotionItemList.add(cartPromotionItem);
                    }
                } else {
                    handleNoReduce(cartPromotionItemList, itemList, promotionProduct);
                }
            } else {
                //无优惠
                handleNoReduce(cartPromotionItemList, itemList, promotionProduct);
            }
        }
        return cartPromotionItemList;
    }

    /**
     * 查询所有商品的优惠相关信息
     */
    private List<PromotionProduct> getPromotionProductList(List<OmsCartItem> cartItemList) {
        List<Long> productIdList = new ArrayList<>();
        for (OmsCartItem cartItem : cartItemList) {
            productIdList.add(cartItem.getProductId());
        }
        return portalProductDao.getPromotionProductList(productIdList);
    }

    /**
     * 以spu为单位对购物车中商品进行分组
     */
    private Map<Long, List<OmsCartItem>> groupCartItemBySpu(List<OmsCartItem> cartItemList) {
        Map<Long, List<OmsCartItem>> productCartMap = new TreeMap<>();
        for (OmsCartItem cartItem : cartItemList) {
            List<OmsCartItem> productCartItemList = productCartMap.get(cartItem.getProductId());
            if (productCartItemList == null) {
                productCartItemList = new ArrayList<>();
                productCartItemList.add(cartItem);
                productCartMap.put(cartItem.getProductId(), productCartItemList);
            } else {
                productCartItemList.add(cartItem);
            }
        }
        return productCartMap;
    }

    /**
     * 获取满减促销消息
     */
    private String getFullReductionPromotionMessage(PmsProductFullReduction fullReduction) {
        StringBuilder sb = new StringBuilder();
        sb.append("满减优惠：");
        sb.append("满");
        sb.append(fullReduction.getFullPrice());
        sb.append("元，");
        sb.append("减");
        sb.append(fullReduction.getReducePrice());
        sb.append("元");
        return sb.toString();
    }

    /**
     * 对没满足优惠条件的商品进行处理
     */

    private void handleNoReduce(List<CartPromotionItem> cartPromotionItemList, List<OmsCartItem> itemList, PromotionProduct promotionProduct) {
        for (OmsCartItem item : itemList) {
            CartPromotionItem cartPromotionItem = new CartPromotionItem();
            BeanUtils.copyProperties(item, cartPromotionItem);
            cartPromotionItem.setPromotionMessage("无优惠");
            cartPromotionItem.setReduceAmount(new BigDecimal(0));

            // 修复：添加更健壮的库存设置逻辑
            PmsSkuStock skuStock = getOriginalPrice(promotionProduct, item.getProductSkuId());
            if (skuStock != null) {
                cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
            } else {
                // 如果skuStock为null，尝试从数据库查询库存
                PmsSkuStock dbSkuStock = skuStockMapper.selectByPrimaryKey(item.getProductSkuId());
                if (dbSkuStock != null) {
                    cartPromotionItem.setRealStock(dbSkuStock.getStock() - dbSkuStock.getLockStock());
                } else {
                    // 如果数据库中也查不到，设置默认库存为0
                    cartPromotionItem.setRealStock(0);
                }
            }

            cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
            cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
            cartPromotionItemList.add(cartPromotionItem);
        }
    }

    private PmsProductFullReduction getProductFullReduction(BigDecimal totalAmount, List<PmsProductFullReduction> fullReductionList) {
        // 添加空值检查
        if (fullReductionList == null || fullReductionList.isEmpty()) {
            return null;
        }

        //按条件从高到低排序
        fullReductionList.sort((o1, o2) -> o2.getFullPrice().subtract(o1.getFullPrice()).intValue());

        for (PmsProductFullReduction fullReduction : fullReductionList) {
            if (totalAmount.subtract(fullReduction.getFullPrice()).intValue() >= 0) {
                return fullReduction;
            }
        }
        return null;
    }


    /**
     * 获取打折优惠的促销信息
     */
    private String getLadderPromotionMessage(PmsProductLadder ladder) {
        StringBuilder sb = new StringBuilder();
        sb.append("打折优惠：");
        sb.append("满");
        sb.append(ladder.getCount());
        sb.append("件，");
        sb.append("打");
        sb.append(ladder.getDiscount().multiply(new BigDecimal(10)));
        sb.append("折");
        return sb.toString();
    }

    /**
     * 根据购买商品数量获取满足条件的打折优惠策略
     */
    private PmsProductLadder getProductLadder(int count, List<PmsProductLadder> productLadderList) {
        // 添加空值检查
        if (productLadderList == null || productLadderList.isEmpty()) {
            return null;
        }

        //按数量从大到小排序
        productLadderList.sort((o1, o2) -> o2.getCount() - o1.getCount());

        for (PmsProductLadder productLadder : productLadderList) {
            if (count >= productLadder.getCount()) {
                return productLadder;
            }
        }
        return null;
    }


    /**
     * 获取购物车中指定商品的数量
     */
    private int getCartItemCount(List<OmsCartItem> itemList) {
        int count = 0;
        for (OmsCartItem item : itemList) {
            count += item.getQuantity();
        }
        return count;
    }

    /**
     * 获取购物车中指定商品的总价
     */
    private BigDecimal getCartItemAmount(List<OmsCartItem> itemList, List<PromotionProduct> promotionProductList) {
        BigDecimal amount = new BigDecimal(0);
        for (OmsCartItem item : itemList) {
            //计算出商品原价
            PromotionProduct promotionProduct = getPromotionProductById(item.getProductId(), promotionProductList);
            PmsSkuStock skuStock = getOriginalPrice(promotionProduct, item.getProductSkuId());
            if (skuStock != null && skuStock.getPrice() != null) {
                amount = amount.add(skuStock.getPrice().multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return amount;
    }

    /**
     * 获取商品的原价
     */
    /**
     * 获取商品的原价
     */
    private PmsSkuStock getOriginalPrice(PromotionProduct promotionProduct, Long productSkuId) {
        // 检查参数是否为空
        if (promotionProduct == null || productSkuId == null) {
            return null;
        }

        // 检查sku库存列表是否为空
        if (promotionProduct.getSkuStockList() == null) {
            return null;
        }

        // 遍历SKU库存列表查找匹配项
        for (PmsSkuStock skuStock : promotionProduct.getSkuStockList()) {
            // 检查当前skuStock是否为null
            if (skuStock != null && productSkuId.equals(skuStock.getId())) {
                return skuStock;
            }
        }
        return null;
    }


    /**
     * 根据商品id获取商品的促销信息
     */
    private PromotionProduct getPromotionProductById(Long productId, List<PromotionProduct> promotionProductList) {
        for (PromotionProduct promotionProduct : promotionProductList) {
            if (productId.equals(promotionProduct.getId())) {
                return promotionProduct;
            }
        }
        return null;
    }
}
