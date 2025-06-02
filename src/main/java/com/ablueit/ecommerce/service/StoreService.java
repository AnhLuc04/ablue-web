package com.ablueit.ecommerce.service;


import com.ablueit.ecommerce.model.Store;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public interface StoreService {
    void deleteListStoreByEntity(List<Store> store);

    void deleteStoreById(Long id);

    Store getStoryById(Long id);

    String updateStore(Long id, Store store, Model model);

  ModelAndView showCategories(Long id, ModelAndView modelAndView);
}
