package com.ablueit.ecommerce.service;

import com.ablueit.ecommerce.model.User;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

public interface SellerService {

    String getDetails(Model model, Principal principal);

    String updateProfile(User seller, Principal principal, Model model);

    void deleteStaff(Long id);

}
