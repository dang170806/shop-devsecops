package com.devon.building.controller.admin.customer;

import com.devon.building.converter.BuildingConverter;
import com.devon.building.converter.CustomerConverter;
import com.devon.building.constant.SystemConstant;
import com.devon.building.enums.CustomerStatus;
import com.devon.building.enums.District;
import com.devon.building.enums.RentType;
import com.devon.building.enums.Transaction;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.dto.BuildingResponseDTO;
import com.devon.building.model.dto.CustomerDTO;
import com.devon.building.model.dto.CustomerResponseDTO;
import com.devon.building.model.request.CustomerSearchRequest;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.UserRepository;
import com.devon.building.repository.entity.BuildingEntity;
import com.devon.building.repository.entity.CustomerEntity;
import com.devon.building.repository.entity.UserEntity;
import com.devon.building.service.BuildingService;
import com.devon.building.service.CustomerService;
import com.devon.building.service.TransactionService;
import com.devon.building.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;

@Controller
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final UserService userService;
    private final CustomerService customerService;
    private final CustomerConverter customerConverter;
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    @GetMapping("/list")
    public ModelAndView getAllCustomer(@ModelAttribute CustomerSearchRequest customerSearchRequest,
                                       @RequestParam(defaultValue = "1") int page,
                                       Authentication authentication) throws SQLException, IllegalAccessException {
        boolean staff = authentication.getAuthorities().stream()
                .anyMatch(authority -> SystemConstant.STAFF_ROLE.equals(authority.getAuthority()));
        if (staff) {
            UserEntity user = userRepository.findByUserName(authentication.getName());
            customerSearchRequest.setStaffId(user.getId());
        }
        ModelAndView modelAndView = new ModelAndView("admin/customer/customerList");
        if (!staff) {
            modelAndView.addObject("staffs",userService.loadStaff());
        }
        modelAndView.addObject("status", CustomerStatus.getStatusMap());
        modelAndView.addObject("customerSearchRequest", customerSearchRequest);
        int currentPage = Math.max(1, page);
        Pageable pageable = PageRequest.of(currentPage - 1, 5);
        Page<CustomerResponseDTO> customerPage = customerService.searchCustomers(customerSearchRequest, pageable);
        if (customerPage.getTotalPages() > 0 && currentPage > customerPage.getTotalPages()) {
            currentPage = customerPage.getTotalPages();
            pageable = PageRequest.of(currentPage - 1, 5);
            customerPage = customerService.searchCustomers(customerSearchRequest, pageable);
        }
        modelAndView.addObject("customers", customerPage.getContent());
        modelAndView.addObject("currentPage", currentPage);
        modelAndView.addObject("totalPages", customerPage.getTotalPages());
        modelAndView.addObject("totalItems", customerPage.getTotalElements());
        modelAndView.addObject("navigationPages", PaginationResult.buildNavigationPages(
                currentPage, customerPage.getTotalPages(), 10));
        return modelAndView;
    }
    @GetMapping("/edit")
    public ModelAndView getEditCustomer(){
        ModelAndView modelAndView = new ModelAndView("admin/customer/customerEdit");
        modelAndView.addObject("status", CustomerStatus.getStatusMap());
        modelAndView.addObject("customer", new CustomerDTO());
        return modelAndView;
    }
    @GetMapping("/{id}/update")
    public String getUpdateCustomer(@PathVariable Long id, Model model, Authentication authentication){
        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> SystemConstant.STAFF_ROLE.equals(authority.getAuthority()))) {
            UserEntity user = userRepository.findByUserName(authentication.getName());
            boolean isAssigned = user.getCustomers().stream()
                    .anyMatch(customer -> customer.getId().equals(id));
            if (!isAssigned) {
                return "404";
            }
        }

        CustomerEntity customerEntity = customerService.findById(id);
        CustomerDTO customerDTO = customerConverter.toCustomerDTO(customerEntity);
        model.addAttribute("customer", customerDTO);
        model.addAttribute("status", CustomerStatus.getStatusMap());
        model.addAttribute("transaction", Transaction.getTransactionMap());
        model.addAttribute("transactionCSKH", transactionService.getTransaction(id, "CSKH"));
        model.addAttribute("transactionDDX", transactionService.getTransaction(id, "DDX"));
        return "admin/customer/customerEdit";
    }
}
