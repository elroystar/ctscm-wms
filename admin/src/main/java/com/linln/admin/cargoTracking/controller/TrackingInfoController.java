package com.linln.admin.cargoTracking.controller;

import com.linln.admin.cargoTracking.domain.TrackingInfo;
import com.linln.admin.cargoTracking.service.TrackingInfoService;
import com.linln.admin.cargoTracking.validator.TrackingInfoValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.domain.User;
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

/**
 * @author 小懒虫
 * @date 2021/06/24
 */
@Controller
@RequestMapping("/cargoTracking/trackingInfo")
public class TrackingInfoController {

    @Autowired
    private TrackingInfoService trackingInfoService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("cargoTracking:trackingInfo:index")
    public String index(Model model, TrackingInfo trackingInfo) {

        // 获取自己账号下的数据
        User subject = ShiroUtil.getSubject();
        subject.setDept(null);
        trackingInfo.setCreateBy(subject);

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<TrackingInfo> example = Example.of(trackingInfo, matcher);
        Page<TrackingInfo> list = trackingInfoService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/cargoTracking/trackingInfo/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("cargoTracking:trackingInfo:add")
    public String toAdd() {
        return "/cargoTracking/trackingInfo/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("cargoTracking:trackingInfo:edit")
    public String toEdit(@PathVariable("id") TrackingInfo trackingInfo, Model model) {
        model.addAttribute("trackingInfo", trackingInfo);
        return "/cargoTracking/trackingInfo/add";
    }

    /**
     * 保存添加/修改的数据
     *
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"cargoTracking:trackingInfo:add", "cargoTracking:trackingInfo:edit"})
    @ResponseBody
    public ResultVo save(@Validated TrackingInfoValid valid, TrackingInfo trackingInfo) {
        // 复制保留无需修改的数据
        if (trackingInfo.getId() != null) {
            TrackingInfo beTrackingInfo = trackingInfoService.getById(trackingInfo.getId());
            EntityBeanUtil.copyProperties(beTrackingInfo, trackingInfo);
        }

        // 保存数据
        trackingInfoService.save(trackingInfo);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("cargoTracking:trackingInfo:detail")
    public String toDetail(@PathVariable("id") TrackingInfo trackingInfo, Model model) {
        model.addAttribute("trackingInfo", trackingInfo);
        return "/cargoTracking/trackingInfo/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("cargoTracking:trackingInfo:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (trackingInfoService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}