package com.linln.admin.stockin.controller;

import com.linln.admin.stockin.domain.StockinOrderInfo;
import com.linln.admin.stockin.domain.StockinOrderInfoExcel;
import com.linln.admin.stockin.service.StockinOrderInfoService;
import com.linln.admin.stockin.validator.StockinOrderInfoValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.excel.ExcelUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/12/11
 */
@Controller
@RequestMapping("/stockin/stockinOrderInfo")
public class StockinOrderInfoController {

    @Autowired
    private StockinOrderInfoService stockinOrderInfoService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("stockin:stockinOrderInfo:index")
    public String index(Model model, StockinOrderInfo stockinOrderInfo) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<StockinOrderInfo> example = Example.of(stockinOrderInfo, matcher);
        Page<StockinOrderInfo> list = stockinOrderInfoService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/stockin/stockinOrderInfo/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("stockin:stockinOrderInfo:add")
    public String toAdd() {
        return "/stockin/stockinOrderInfo/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("stockin:stockinOrderInfo:edit")
    public String toEdit(@PathVariable("id") StockinOrderInfo stockinOrderInfo, Model model) {
        model.addAttribute("stockinOrderInfo", stockinOrderInfo);
        return "/stockin/stockinOrderInfo/add";
    }

    /**
     * 保存添加/修改的数据
     *
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"stockin:stockinOrderInfo:add", "stockin:stockinOrderInfo:edit"})
    @ResponseBody
    public ResultVo save(@Validated StockinOrderInfoValid valid, StockinOrderInfo stockinOrderInfo) {
        // 复制保留无需修改的数据
        if (stockinOrderInfo.getId() != null) {
            StockinOrderInfo beStockinOrderInfo = stockinOrderInfoService.getById(stockinOrderInfo.getId());
            EntityBeanUtil.copyProperties(beStockinOrderInfo, stockinOrderInfo);
        }

        // 保存数据
        stockinOrderInfoService.save(stockinOrderInfo);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("stockin:stockinOrderInfo:detail")
    public String toDetail(@PathVariable("id") StockinOrderInfo stockinOrderInfo, Model model) {
        model.addAttribute("stockinOrderInfo", stockinOrderInfo);
        return "/stockin/stockinOrderInfo/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("stockin:stockinOrderInfo:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (stockinOrderInfoService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    /**
     * 导入明细
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResultVo uploadImage(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {
        try {
            String orderNo = request.getParameter("orderNo");
            InputStream inputStream = multipartFile.getInputStream();
            List<StockinOrderInfoExcel> orderInfoExcels = ExcelUtil.importExcel(StockinOrderInfoExcel.class, inputStream);
            for (StockinOrderInfoExcel orderInfoExcel : orderInfoExcels) {
                StockinOrderInfo orderInfo = new StockinOrderInfo();
                BeanUtils.copyProperties(orderInfoExcel, orderInfo);
                orderInfo.setOrderNo(orderNo);
                stockinOrderInfoService.save(orderInfo);
            }
            return ResultVoUtil.success();
        } catch (Exception e) {
            return ResultVoUtil.error("导入库位失败");
        }
    }
}