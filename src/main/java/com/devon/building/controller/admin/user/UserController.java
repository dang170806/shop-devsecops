package com.devon.building.controller.admin.user;

import com.devon.building.constant.SystemConstant;
import com.devon.building.model.dto.UserDTO;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.UserService;
import com.devon.building.utils.MessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/admin/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    private final MessageUtils messageUtils;

    @GetMapping("/list")
    public ModelAndView userList(@RequestParam(value = "page", defaultValue = "1") String pageStr, @RequestParam(value = "key", required = false) String key, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/user/userList");
        int page = 1;
        try {
            page = Integer.parseInt(pageStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int MAX_RESULT = 5;
        final int MAX_NAVIGATION_PAGE = 10;
        PaginationResult<UserEntity> paginationResult = userService.listUserInfo(key, page, MAX_RESULT, MAX_NAVIGATION_PAGE);
        modelAndView.addObject("model", paginationResult);
        initMessageResponse(modelAndView, request);
        return modelAndView;
    }

    private void initMessageResponse(ModelAndView mav, HttpServletRequest request) {
        String message = request.getParameter("message");
        if (StringUtils.isNotEmpty(message)) {
            Map<String, String> messageMap = messageUtils.getMessage(message);
            mav.addObject(SystemConstant.ALERT, messageMap.get(SystemConstant.ALERT));
            mav.addObject(SystemConstant.MESSAGE_RESPONSE, messageMap.get(SystemConstant.MESSAGE_RESPONSE));
        }
    }

    @GetMapping("/{userName}")
    public ModelAndView getUser(@PathVariable String userName, HttpServletRequest request) {
        ModelAndView model = new ModelAndView("admin/user/userEdit");
        UserDTO userDTO = null;
        if (!userName.trim().isEmpty()) {
            UserEntity user = userRepository.findAllByUserNameAndActiveTrue(userName);
            if (user != null) {
                userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setUserName(user.getUserName());
                userDTO.setFullName(user.getFullName());
                userDTO.setRoleCode(user.getUserRole());
                userDTO.initRoles();
            }
        }
        model.addObject("user", userDTO);
        initMessageResponse(model, request);
        return model;

    }

    @GetMapping()
    public ModelAndView addUser(UserDTO user, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/user/userEdit");
        user.initRoles();
        initMessageResponse(modelAndView, request);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/userImage")
    public void productImage(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(value = "userName", defaultValue = "") String userName) throws IOException {
        UserEntity user = null;
        if (userName != null && !userName.isBlank()) {
            user = userRepository.findAllByUserNameAndActiveTrue(userName);
        }
        if (user != null && user.getImage() != null) {
            response.setContentType("image/jpeg");
            response.setContentType("image/png");
            response.getOutputStream().write(user.getImage());
        }
        response.getOutputStream().close();
    }

    @GetMapping("/change-password/{id}")
    public ModelAndView resetPassword(@PathVariable Long id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/user/change-password");
        UserDTO user = null;
        if (id != null) {
            UserEntity userEntity = userRepository.findById(id).orElseThrow();
            user = new UserDTO();
            user.setId(userEntity.getId());
            user.setUserName(userEntity.getUserName());
            user.setFullName(userEntity.getFullName());
            user.setRoleCode(userEntity.getUserRole());
            user.initRoles();
        }
        initMessageResponse(modelAndView, request);
        modelAndView.addObject("user", user);
        return modelAndView;
    }
}
