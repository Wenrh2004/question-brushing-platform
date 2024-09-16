package com.questionbrushingplatform.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.questionbrushingplatform.common.constant.MessageConstant;
import com.questionbrushingplatform.common.exception.BaseException;
import com.questionbrushingplatform.common.result.Result;
import com.questionbrushingplatform.pojo.dto.LoginAndRegisterDTO;
import com.questionbrushingplatform.pojo.dto.UserAddDTO;
import com.questionbrushingplatform.pojo.entity.User;
import com.questionbrushingplatform.pojo.vo.LoginVO;
import com.questionbrushingplatform.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * @author 永
 */
@RestController
@RequestMapping("/web")
@Slf4j
@Api(tags = "登录注册接口")
public class WebController {

    @Autowired
    private UserService userService;


    /**
     * 登录
     * @param loginAndRegisterDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录")
    public Result<LoginVO> login(@RequestBody LoginAndRegisterDTO loginAndRegisterDTO) {

        User user=userService.login(loginAndRegisterDTO);
        StpUtil.login(user.getId());

        LoginVO loginVO = LoginVO.builder()
                .id(user.getId())
                .userAccount(user.getUserAccount())
                .userAvatar(user.getUserAvatar())
                .userName(user.getUserName())
                .userProfile(user.getUserProfile())
                .userRole(user.getUserRole())
                .userPassword(StrUtil.hide(user.getUserPassword(), 1, 3))
                .createdTime(user.getCreatedTime())
                .editTime(user.getEditTime())
                .updateTime(user.getUpdateTime())
                .build();

        return Result.success(loginVO);
    }


    /**
     * 注册
     * @param loginAndRegisterDTO
     * @return
     */
    @PostMapping("/register")
    @ApiOperation(value = "注册")
    public Result<String> register(@RequestBody LoginAndRegisterDTO loginAndRegisterDTO) {
        //检查一下是否填写了账号和密码
        if (StrUtil.isBlank(loginAndRegisterDTO.getUserAccount())||StrUtil.isBlank(loginAndRegisterDTO.getUserPassword())){
            throw new BaseException(MessageConstant.ERROR_ACCOUNT_AND_PASSWORD);
        }
        UserAddDTO userAddDTO = new UserAddDTO();
        BeanUtils.copyProperties(loginAndRegisterDTO, userAddDTO);
        userService.add(userAddDTO);
        return Result.success(MessageConstant.USER_REGISTER_SUCCESS);
    }

    /**
     * 退出登录
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "退出登录")
    public Result<String> logout() {
        StpUtil.logout();
        return Result.success(MessageConstant.USER_LOGOUT_SUCCESS);
    }





}
