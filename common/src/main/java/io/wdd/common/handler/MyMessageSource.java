package io.wdd.common.handler;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Locale;

@Component
public class MyMessageSource {

    @Resource
    private MessageSource messageSource;

    public MyMessageSource() {
    }

    public String getMessage(String code, Object... params) {
        return this.getMessage(code, (Object[])null, params);
    }

    public String getMessageIgnoreMissMatch(String code, Object... params) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = this.messageSource.getMessage(code, (Object[])null, code, locale);
        return this.parse(message, true, params);
    }

    public String getMessage(String code, Object[] args, Object... params) {
        return this.getMessage(code, args, code, params);
    }

    public String getMessage(String code, Object[] args, String defaultMessage, Object... params) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = this.messageSource.getMessage(code, args, defaultMessage, locale);
        return this.parse(message, false, params);
    }

    private String parse(String s, boolean ingoreParamsMissMath, Object... params) {
        if (s == null) {
            return null;
        } else if (params == null) {
            return s;
        } else {
            String[] splits = s.split("\\{}", -1);
            if (splits.length != params.length + 1) {
                if (ingoreParamsMissMath) {
                    return s;
                } else {
                    throw new IllegalArgumentException("The number of parameters is inconsistent with the parameter value");
                }
            } else if (splits.length == 1) {
                return s;
            } else {
                StringBuilder stringBuilder = new StringBuilder();

                for(int i = 0; i < splits.length; ++i) {
                    String split = splits[i];
                    stringBuilder.append(split);
                    if (i < params.length) {
                        stringBuilder.append(params[i]);
                    }
                }

                return stringBuilder.toString();
            }
        }
    }
}
