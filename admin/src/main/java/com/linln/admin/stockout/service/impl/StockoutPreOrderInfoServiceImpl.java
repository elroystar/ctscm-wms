package com.linln.admin.stockout.service.impl;

import com.linln.admin.stock.domain.StockOrderInfo;
import com.linln.admin.stock.repository.StockOrderInfoRepository;
import com.linln.admin.stock.service.StockOrderInfoService;
import com.linln.admin.stockout.domain.StockoutOrder;
import com.linln.admin.stockout.domain.StockoutOrderInfo;
import com.linln.admin.stockout.service.StockoutOrderInfoService;
import com.linln.admin.stockout.service.StockoutOrderService;
import com.linln.admin.stockout.service.StockoutPreOrderInfoService;
import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 小懒虫
 * @date 2020/12/25
 */
@Service
public class StockoutPreOrderInfoServiceImpl implements StockoutPreOrderInfoService {

    @Autowired
    private StockOrderInfoRepository stockoutPreOrderInfoRepository;

    @Autowired
    private StockOrderInfoService stockOrderInfoService;

    @Autowired
    private StockoutOrderInfoService stockoutOrderInfoService;

    @Autowired
    private StockoutOrderService stockoutOrderService;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 根据ID查询数据
     *
     * @param id 主键ID
     */
    @Override
    @Transactional
    public StockOrderInfo getById(Long id) {
        return stockoutPreOrderInfoRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     *
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<StockOrderInfo> getPageList(Example<StockOrderInfo> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return stockoutPreOrderInfoRepository.findAll(example, page);
    }

    /**
     * 保存数据
     *
     * @param stockoutPreOrderInfo 实体对象
     */
    @Override
    public StockOrderInfo save(StockOrderInfo stockoutPreOrderInfo) {
        return stockoutPreOrderInfoRepository.save(stockoutPreOrderInfo);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return stockoutPreOrderInfoRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

    @Override
    @Transactional
    public synchronized ResultVo outQtyInput(String outType, String outValue, String outQtyInput) {
        Integer outQty = Integer.parseInt(outQtyInput);
        List<StockOrderInfo> stockOrderInfos;
        switch (outType) {
            case "model":
                stockOrderInfos = stockOrderInfoService.getByModel(outValue);
                break;
            case "sn":
                stockOrderInfos = stockOrderInfoService.getBySn(outValue);
                break;
            case "dn":
                stockOrderInfos = stockOrderInfoService.getByDn(outValue);
                break;
            case "po":
                stockOrderInfos = stockOrderInfoService.getByPo(outValue);
                break;
            case "so":
                stockOrderInfos = stockOrderInfoService.getBySo(outValue);
                break;
            case "contractNo":
                stockOrderInfos = stockOrderInfoService.getByContractNo(outValue);
                break;
            case "internalNo":
                stockOrderInfos = stockOrderInfoService.getByInternalNo(outValue);
                break;
            default:
                throw new RuntimeException("出库条件为空");
        }
        Integer qty = stockOrderInfos.stream().collect(Collectors.summingInt(StockOrderInfo::getQty));
        if (outQty > qty) {
            throw new RuntimeException("出库失败，库存不足");
        }
        for (StockOrderInfo stockOrderInfo : stockOrderInfos) {
            if (outQty > 0) {
                StockoutOrderInfo stockoutOrderInfo = new StockoutOrderInfo();
                Integer tempQty = stockOrderInfo.getQty();
                if (tempQty <= outQty) {
                    outQty = outQty - tempQty;
                    stockOrderInfoService.deleteById(stockOrderInfo.getId());
                    outQty(stockOrderInfo, stockoutOrderInfo, tempQty);
                } else {
                    Integer surQty = tempQty - outQty;
                    stockOrderInfoService.updateQtyById(surQty, stockOrderInfo.getId());
                    stockoutOrderInfo.setStockId(Long.toString(stockOrderInfo.getId()));
                    outQty(stockOrderInfo, stockoutOrderInfo, outQty);
                    break;
                }
            }

        }

        return ResultVoUtil.success("出库成功");
    }

    private void outQty(StockOrderInfo stockOrderInfo, StockoutOrderInfo stockoutOrderInfo, Integer qty) {
        stockoutOrderInfo.setOrderNo(stockOrderInfo.getOrderNo());
        stockoutOrderInfo.setRegionId(stockOrderInfo.getRegionId());
        stockoutOrderInfo.setLocationId(stockOrderInfo.getLocationId());
        stockoutOrderInfo.setModel(stockOrderInfo.getModel());
        stockoutOrderInfo.setSn(stockOrderInfo.getSn());
        stockoutOrderInfo.setDn(stockOrderInfo.getDn());
        stockoutOrderInfo.setPo(stockOrderInfo.getPo());
        stockoutOrderInfo.setSo(stockOrderInfo.getSo());
        stockoutOrderInfo.setContractNo(stockOrderInfo.getContractNo());
        stockoutOrderInfo.setProductDate(stockOrderInfo.getProductDate());
        stockoutOrderInfo.setWeight(stockOrderInfo.getWeight());
        stockoutOrderInfo.setVolume(stockOrderInfo.getVolume());
        stockoutOrderInfo.setGoodsName(stockOrderInfo.getGoodsName());
        stockoutOrderInfo.setProductType(stockOrderInfo.getProductType());
        stockoutOrderInfo.setSupplier(stockOrderInfo.getSupplier());
        stockoutOrderInfo.setLocationNo(stockOrderInfo.getLocationNo());
        stockoutOrderInfo.setInternalNo(stockOrderInfo.getInternalNo());
        stockoutOrderInfo.setRemark(stockOrderInfo.getRemark());
        // 获取当前用户信息
        User subject = ShiroUtil.getSubject();
        stockoutOrderInfo.setCreateBy(subject);
        stockoutOrderInfo.setUpdateBy(subject);
        stockoutOrderInfo.setStatus(StatusEnum.FREEZED.getCode());
        stockoutOrderInfo.setQty(qty);
        // 入库
        stockoutOrderInfoService.save(stockoutOrderInfo);
    }

    @Override
    @Transactional
    public synchronized ResultVo outQtyTd(String outQtyTdArray) {
        String[] split = outQtyTdArray.split("&");
        for (String outQtyTd : split) {
            String[] split1 = outQtyTd.split(",");
            String orderId = split1[0];
            Integer outQty = Integer.parseInt(split1[1]);
            StockOrderInfo stockOrderInfo = stockOrderInfoService.getById(Long.parseLong(orderId));
            Integer qty = stockOrderInfo.getQty();
            if (outQty > qty) {
                throw new RuntimeException("出库失败，库存不足");
            }
            StockoutOrderInfo stockoutOrderInfo = new StockoutOrderInfo();
            Integer surQty = qty - outQty;
            // 出库后库存为0，删除该库存
            if (surQty == 0) {
                stockOrderInfoService.deleteById(stockOrderInfo.getId());
            } else if (surQty > 0) {
                stockOrderInfoService.updateQtyById(surQty, Long.parseLong(orderId));
                stockoutOrderInfo.setStockId(orderId);
            }
            outQty(stockOrderInfo, stockoutOrderInfo, outQty);
        }
        return ResultVoUtil.success("出库成功");
    }

    @Override
    public ResultVo revoke(String idArray) {
        String[] split = idArray.split("&");
        for (String id : split) {
            StockoutOrderInfo stockoutOrderInfo = stockoutOrderInfoService.getById(Long.parseLong(id));
            String stockId = stockoutOrderInfo.getStockId();
            if (StringUtils.isNotBlank(stockId)) {
                // 库存有剩余加回去
                StockOrderInfo stockOrderInfo = stockOrderInfoService.getById(Long.parseLong(stockId));
                Integer qty = stockOrderInfo.getQty();
                qty = qty + stockoutOrderInfo.getQty();
                stockOrderInfoService.updateQtyById(qty, Long.parseLong(stockId));
            } else {
                // 库存无剩余
                StockOrderInfo stockOrderInfo = new StockOrderInfo();
                stockOrderInfo.setOrderNo(stockoutOrderInfo.getOrderNo());
                stockOrderInfo.setRegionId(stockoutOrderInfo.getRegionId());
                stockOrderInfo.setLocationId(stockoutOrderInfo.getLocationId());
                stockOrderInfo.setModel(stockoutOrderInfo.getModel());
                stockOrderInfo.setSn(stockoutOrderInfo.getSn());
                stockOrderInfo.setQty(stockoutOrderInfo.getQty());
                stockOrderInfo.setDn(stockoutOrderInfo.getDn());
                stockOrderInfo.setPo(stockoutOrderInfo.getPo());
                stockOrderInfo.setSo(stockoutOrderInfo.getSo());
                stockOrderInfo.setContractNo(stockoutOrderInfo.getContractNo());
                stockOrderInfo.setProductDate(stockoutOrderInfo.getProductDate());
                stockOrderInfo.setWeight(stockoutOrderInfo.getWeight());
                stockOrderInfo.setVolume(stockoutOrderInfo.getVolume());
                stockOrderInfo.setGoodsName(stockoutOrderInfo.getGoodsName());
                stockOrderInfo.setProductType(stockoutOrderInfo.getProductType());
                stockOrderInfo.setSupplier(stockoutOrderInfo.getSupplier());
                stockOrderInfo.setLocationNo(stockoutOrderInfo.getLocationNo());
                stockOrderInfo.setInternalNo(stockoutOrderInfo.getInternalNo());
                stockOrderInfo.setRemark(stockoutOrderInfo.getRemark());
                stockOrderInfo.setCreateDate(stockoutOrderInfo.getCreateDate());
                stockOrderInfo.setUpdateDate(stockoutOrderInfo.getUpdateDate());
                stockOrderInfo.setCreateBy(stockoutOrderInfo.getCreateBy());
                stockOrderInfo.setUpdateBy(ShiroUtil.getSubject());
                stockOrderInfoService.save(stockOrderInfo);
            }
            // 删除预出库记录
            stockoutOrderInfoService.deleteById(stockoutOrderInfo.getId());
        }
        return ResultVoUtil.success("撤回成功");
    }

    @Override
    public synchronized ResultVo sureStock(String idArray) {
        String date = dateTimeFormatter.format(LocalDateTime.now());
        Integer countNow = stockoutOrderService.getCountNow();
        Integer num = ++countNow;
        String format = String.format("%04d", num);
        String outNo = MessageFormat.format("{0}{1}-{2}", "C", date, format);
        String[] split = idArray.split("&");
        for (String id : split) {
            stockoutOrderInfoService.updateOutNoById(outNo, Long.parseLong(id));
        }
        StockoutOrder stockoutOrder = new StockoutOrder();
        stockoutOrder.setCreateBy(ShiroUtil.getSubject());
        stockoutOrder.setOrderNo(outNo);
        stockoutOrder.setCreateDate(new Date());
        stockoutOrder.setUpdateDate(new Date());
        stockoutOrder.setUpdateBy(ShiroUtil.getSubject());
        stockoutOrderService.save(stockoutOrder);
        return ResultVoUtil.success("出库成功");
    }
}