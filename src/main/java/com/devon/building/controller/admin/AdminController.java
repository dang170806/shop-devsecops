package com.devon.building.controller.admin;

import com.devon.building.form.ProductForm;
import com.devon.building.model.OrderDetailInfo;
import com.devon.building.model.OrderInfo;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.OrderRepository;
import com.devon.building.repository.ProductRepository;
import com.devon.building.repository.entity.BuildingEntity;
import com.devon.building.validator.ProductFormValidator;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.logging.Logger;

@Controller
@Transactional
@AllArgsConstructor
public class AdminController {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final ProductFormValidator productFormValidator;

    private final Logger logger = Logger.getLogger(AdminController.class.getName());

    @InitBinder
    public void myInitBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }
        logger.warning("target is " + target);

        if (target.getClass() == ProductForm.class) {
            dataBinder.setValidator(productFormValidator);
        }
    }

    // GET: Show Login Page
    @GetMapping(value = {"/admin/login"})
    public String login(Model model) {
        return "login";
    }

    @GetMapping(value = {"/admin/accountInfo"})
    public String accountInfo(Model model) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("userDetails", userDetails);
        return "accountInfo";
    }

    @GetMapping(value = {"/admin/orderList"})
    public String orderList(Model model, //
                            @RequestParam(value = "page", defaultValue = "1") String pageStr) {
        int page = 1;
        try {
            page = Integer.parseInt(pageStr);
        } catch (Exception e) {
        }
        final int MAX_RESULT = 5;
        final int MAX_NAVIGATION_PAGE = 10;

        PaginationResult<OrderInfo> paginationResult //
                = orderRepository.listOrderInfo(page, MAX_RESULT, MAX_NAVIGATION_PAGE);

        model.addAttribute("paginationResult", paginationResult);
        return "orderList";
    }

    // GET: Show product.
    @GetMapping(value = {"/admin/building"})
    public String product(Model model, @RequestParam(value = "id", defaultValue = "") Long id) {
        ProductForm productForm = null;

        if (id != null) {
            BuildingEntity building = productRepository.findProduct(id);
            if (building != null) {
                productForm = new ProductForm(building);
            }
        }
        if (productForm == null) {
            productForm = new ProductForm();
            productForm.setNewProduct(true);
        }
        model.addAttribute("productForm", productForm);
        return "product";
    }

    // POST: Save product
    @PostMapping(value = {"/admin/building"})
    public String productSave(Model model, //
                              @ModelAttribute("productForm") @Validated ProductForm productForm, //
                              BindingResult result, //
                              final RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "product";
        }
        try {
            productRepository.save(productForm);
        } catch (Exception e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            String message = rootCause.getMessage();
            model.addAttribute("errorMessage", message);
            // Show product form.
            return "product";
        }

        return "redirect:/productList";
    }

    @GetMapping(value = {"/admin/order"})
    public String orderView(Model model, @RequestParam("orderId") String orderId) {
        OrderInfo orderInfo = null;
        if (orderId != null) {
            orderInfo = this.orderRepository.getOrderInfo(orderId);
        }
        if (orderInfo == null) {
            return "redirect:/admin/orderList";
        }
        List<OrderDetailInfo> details = this.orderRepository.listOrderDetailInfos(orderId);
        orderInfo.setDetails(details);

        model.addAttribute("orderInfo", orderInfo);

        return "order";
    }

}
