package com.linln.admin.warehouse.controller;

import com.linln.admin.warehouse.domain.WarehouseLocation;
import com.linln.admin.warehouse.domain.WarehouseRegion;
import com.linln.admin.warehouse.service.WarehouseLocationService;
import com.linln.admin.warehouse.service.WarehouseRegionService;
import com.linln.admin.warehouse.validator.WarehouseRegionValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.domain.Region;
import com.linln.modules.system.domain.Role;
import com.linln.modules.system.domain.User;
import com.linln.modules.system.service.RegionService;
import org.apache.commons.compress.utils.Lists;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author star
 * @date 2020/11/18
 */
@Controller
@RequestMapping("/warehouse/warehouseRegion")
public class WarehouseRegionController {

    @Autowired
    private WarehouseRegionService warehouseRegionService;

    @Autowired
    private WarehouseLocationService warehouseLocationService;

    @Autowired
    private RegionService regionService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("warehouse:warehouseRegion:index")
    public String index(Model model, WarehouseRegion warehouseRegion) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", match -> match.contains());

        // 获取数据列表
        Example<WarehouseRegion> example = Example.of(warehouseRegion, matcher);
        Page<WarehouseRegion> list = warehouseRegionService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/warehouse/warehouseRegion/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("warehouse:warehouseRegion:add")
    public String toAdd() {
        return "/warehouse/warehouseRegion/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("warehouse:warehouseRegion:edit")
    public String toEdit(@PathVariable("id") WarehouseRegion warehouseRegion, Model model) {
        model.addAttribute("warehouseRegion", warehouseRegion);
        return "/warehouse/warehouseRegion/add";
    }

    /**
     * 保存添加/修改的数据
     *
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"warehouse:warehouseRegion:add", "warehouse:warehouseRegion:edit"})
    @ResponseBody
    public ResultVo save(@Validated WarehouseRegionValid valid, WarehouseRegion warehouseRegion) {
        // 复制保留无需修改的数据
        if (warehouseRegion.getId() != null) {
            WarehouseRegion beWarehouseRegion = warehouseRegionService.getById(warehouseRegion.getId());
            EntityBeanUtil.copyProperties(beWarehouseRegion, warehouseRegion);
        }

        // 保存数据
        warehouseRegionService.save(warehouseRegion);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("warehouse:warehouseRegion:detail")
    public String toDetail(@PathVariable("id") WarehouseRegion warehouseRegion, Model model) {

        WarehouseLocation warehouseLocation = new WarehouseLocation();
        warehouseLocation.setRegionId(warehouseRegion.getId());
        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();
        // 获取数据列表
        Example<WarehouseLocation> example = Example.of(warehouseLocation, matcher);
        Page<WarehouseLocation> list = warehouseLocationService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        model.addAttribute("regionId", warehouseRegion.getId());
        return "/warehouse/warehouseLocation/index";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("warehouse:warehouseRegion:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (warehouseRegionService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    /**
     * 获取用户隶属库区
     */
    @GetMapping("/getUserRegion")
    @ResponseBody
    public List<WarehouseRegion> getUserRegion() {
        List<WarehouseRegion> warehouseRegions = Lists.newArrayList();
        List<String> roles = ShiroUtil.getSubjectRoles().stream().map(Role::getName).collect(Collectors.toList());
        // 如果是一级库管员，返回所有库区
        if (roles.contains("first-keeper")) {
            warehouseRegions = warehouseRegionService.findAll();
        } else {
            User user = ShiroUtil.getSubject();
            List<Region> region = regionService.getRegionByUserId(Long.toString(user.getId()));
            for (Region r : region) {
                WarehouseRegion byId = warehouseRegionService.getById(r.getRegionId());
                warehouseRegions.add(byId);
            }
        }
        return warehouseRegions;
    }
}