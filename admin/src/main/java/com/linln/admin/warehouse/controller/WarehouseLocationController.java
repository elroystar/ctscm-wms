package com.linln.admin.warehouse.controller;

import com.linln.admin.warehouse.domain.WarehouseLocation;
import com.linln.admin.warehouse.domain.WarehouseLocationExcel;
import com.linln.admin.warehouse.domain.WarehouseRegion;
import com.linln.admin.warehouse.service.WarehouseLocationService;
import com.linln.admin.warehouse.service.WarehouseRegionService;
import com.linln.admin.warehouse.validator.WarehouseLocationValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.excel.ExcelUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * @author star
 * @date 2020/11/18
 */
@Controller
@RequestMapping("/warehouse/warehouseLocation")
public class WarehouseLocationController {

    @Autowired
    private WarehouseRegionService warehouseRegionService;

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("warehouse:warehouseLocation:index")
    public String index(Model model, WarehouseLocation warehouseLocation) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<WarehouseLocation> example = Example.of(warehouseLocation, matcher);
        Page<WarehouseLocation> list = warehouseLocationService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/warehouse/warehouseLocation/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("warehouse:warehouseLocation:add")
    public String toAdd() {
        return "/warehouse/warehouseLocation/add";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add/{regionId}")
    @RequiresPermissions("warehouse:warehouseLocation:add")
    public String toRegionAdd(@PathVariable("regionId") long regionId, Model model) {
        model.addAttribute("regionId", regionId);
        return "/warehouse/warehouseLocation/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("warehouse:warehouseLocation:edit")
    public String toEdit(@PathVariable("id") WarehouseLocation warehouseLocation, Model model) {
        model.addAttribute("warehouseLocation", warehouseLocation);
        return "/warehouse/warehouseLocation/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}/{regionId}")
    @RequiresPermissions("warehouse:warehouseLocation:edit")
    public String toRegionEdit(@PathVariable("id") WarehouseLocation warehouseLocation,
                               @PathVariable("regionId") long regionId,
                               Model model) {
        model.addAttribute("warehouseLocation", warehouseLocation);
        model.addAttribute("regionId", regionId);
        return "/warehouse/warehouseLocation/add";
    }

    /**
     * 保存添加/修改的数据
     *
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"warehouse:warehouseLocation:add", "warehouse:warehouseLocation:edit"})
    @ResponseBody
    public ResultVo save(@Validated WarehouseLocationValid valid, WarehouseLocation warehouseLocation) {
        // 复制保留无需修改的数据
        if (warehouseLocation.getId() != null) {
            WarehouseLocation beWarehouseLocation = warehouseLocationService.getById(warehouseLocation.getId());
            EntityBeanUtil.copyProperties(beWarehouseLocation, warehouseLocation);
        }
        Long regionId = warehouseLocation.getRegionId();
        WarehouseRegion warehouseRegion = warehouseRegionService.getById(regionId);
        warehouseLocation.setRegionName(warehouseRegion.getName());
        // 保存数据
        warehouseLocationService.save(warehouseLocation);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("warehouse:warehouseLocation:detail")
    public String toDetail(@PathVariable("id") WarehouseLocation warehouseLocation, Model model) {
        model.addAttribute("warehouseLocation", warehouseLocation);
        return "/warehouse/warehouseLocation/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("warehouse:warehouseLocation:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (warehouseLocationService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    /**
     * 导入库位
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResultVo uploadImage(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {
        try {
            String regionId = request.getParameter("regionId");
            long parseLong = Long.parseLong(regionId);
            WarehouseRegion warehouseRegion = warehouseRegionService.getById(parseLong);
            InputStream inputStream = multipartFile.getInputStream();
            List<WarehouseLocationExcel> excels = ExcelUtil.importExcel(WarehouseLocationExcel.class, inputStream);
            for (WarehouseLocationExcel excel : excels) {
                WarehouseLocation warehouseLocation = warehouseLocationService.checkCodeByRegionId(parseLong, excel.getCode());
                if (warehouseLocation == null) {
                    WarehouseLocation location = new WarehouseLocation();
                    location.setCode(excel.getCode());
                    location.setRegionName(warehouseRegion.getName());
                    location.setRemark(excel.getRemark());
                    location.setRegionId(parseLong);
                    warehouseLocationService.save(location);
                }
            }
            return ResultVoUtil.success();
        } catch (Exception e) {
            return ResultVoUtil.error("导入库位失败");
        }
    }
}