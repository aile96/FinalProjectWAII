@file:Suppress("DEPRECATION")

package it.polito.em280048.registrationservice.config


import it.polito.em280048.registrationservice.errors.AuthEntryPointJwt
import it.polito.em280048.registrationservice.security.JWTAuthenticationFilter
import it.polito.em280048.registrationservice.security.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Suppress("DEPRECATION")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    val userService: UserDetailsServiceImpl,
    val authenticationFilter: JWTAuthenticationFilter,
    private val unauthorizedHandler: AuthEntryPointJwt
    ): WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder())
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }

    //permit to ignore what happen in the filter but in this case is useless because the filter has Component annotation
    /*override fun configure(web: WebSecurity?) {
        web!!.ignoring().antMatchers("/user/ **")
    }*/

    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .authorizeRequests().antMatchers("/user/**").permitAll().antMatchers("/admin/**").authenticated().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}