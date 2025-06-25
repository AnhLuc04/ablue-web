package com.ablueit.ecommerce.config;

import com.ablueit.ecommerce.model.StoreInterceptor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @NonFinal
//    @Value("${spring.web.resources.images}")
//    String SAVED_IMAGE_PATH;
//    @Autowired
//    private StoreInterceptor storeInterceptor;
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/images/**")
//                .addResourceLocations("file:" + SAVED_IMAGE_PATH);
//    }
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(storeInterceptor).addPathPatterns("/**");
//    }
//
//
//}




@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private StoreInterceptor storeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(storeInterceptor)
                .addPathPatterns("/products", "/cart/**", "/checkout/**");
    }
}
