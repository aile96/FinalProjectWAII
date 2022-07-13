package it.polito.em280048.registrationservice.interceptors

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.handler.MappedInterceptor


@Configuration
class AppConfig : WebMvcConfigurer {
    private val interceptor: RateLimiterInterceptor? = null

    override fun addInterceptors(registry: InterceptorRegistry) {

        if (interceptor != null) {
            registry.addInterceptor(interceptor)
                .addPathPatterns("/user/**")
        }
    }


    @Bean
    fun myInterceptor(): MappedInterceptor? {

        return MappedInterceptor(null, RateLimiterInterceptor())
    }

}





