package com.MyWeb.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
@Log4j2
@Controller
public class CustomController implements ErrorController {
    public static final String ERROR_EXCEPTION = "jakarta.servlet.error.exception";
    public static final String ERROR_EXCEPTION_TYPE = "jakarta.servlet.error.exception_type";
    public static final String ERROR_MESSAGE = "jakarta.servlet.error.message";
    public static final String ERROR_REQUEST_URI = "jakarta.servlet.error.request_uri";
    public static final String ERROR_SERVLET_NAME = "jakarta.servlet.error.servlet_name";
    public static final String ERROR_STATUS_CODE = "jakarta.servlet.error.status_code";


    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model){
//        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
//        Throwable throwable = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
//        String errorMessage = throwable != null ? throwable.getMessage() : "Unknown error";
        log.info("Error 1번 : {} , 2번 : {}, 3번 : {} , 4번 : {} , 5번 : {}, 6번 : {} ",
                request.getAttribute(ERROR_EXCEPTION),
                request.getAttribute(ERROR_EXCEPTION_TYPE),
                request.getAttribute(ERROR_MESSAGE),
                request.getAttribute(ERROR_REQUEST_URI),
                request.getAttribute(ERROR_SERVLET_NAME),
                request.getAttribute(ERROR_STATUS_CODE));
//        model.addAttribute("statusCode", statusCode);
//        model.addAttribute("errorMessage", errorMessage);

        return "error/403";
//        if (statusCode == 403) {
//            return "error/403";
//        } else if (statusCode == 404) {
//            return "error/404";
//        } else if (statusCode == 500) {
//            return "error/500";
//        } else {
//            return "error/default";  // 그 외의 에러는 기본 에러 페이지로
//        }
    }
}
