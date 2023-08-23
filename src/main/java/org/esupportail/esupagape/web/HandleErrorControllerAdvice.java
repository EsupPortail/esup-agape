package org.esupportail.esupagape.web;


import org.esupportail.esupagape.web.viewentity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;

@ControllerAdvice
public class HandleErrorControllerAdvice {

    public static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exception,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        logger.warn(exception.getMessage());
        redirectAttributes.addFlashAttribute("message", new Message("danger", "Fichier trop volumineux"));
        return "redirect:" + request.getHeader("referer");
    }
}
