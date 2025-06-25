package com.ablueit.ecommerce.model;

import com.ablueit.ecommerce.repository.StoreRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
@Component
public class StoreInterceptor implements HandlerInterceptor {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreContext storeContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String host = request.getServerName(); // ví dụ: store1.local, giaydep.vn
        Optional<Store> storeOpt = storeRepository.findByDomain(host);

        if (storeOpt.isPresent()) {
            storeContext.setStore(storeOpt.get());
            return true;
        } else {
            response.sendRedirect("/error"); // hoặc return 404 JSON
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        storeContext.clear(); // clear sau request để tránh memory leak
    }
}
