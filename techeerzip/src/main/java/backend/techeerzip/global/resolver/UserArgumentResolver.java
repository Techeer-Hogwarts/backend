package backend.techeerzip.global.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import backend.techeerzip.domain.auth.exception.InvalidJwtTokenException;
import backend.techeerzip.domain.auth.exception.MissingJwtTokenException;
import backend.techeerzip.domain.auth.jwt.CustomUserPrincipal;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserId.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new MissingJwtTokenException();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserPrincipal userPrincipal)) {
            throw new InvalidJwtTokenException();
        }

        return userPrincipal.getUserId();
    }
}
