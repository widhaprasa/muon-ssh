package apple.uri;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AppleURIHandlerHelper {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void setOpenURIEventHandler(AppleURIHandler urlHandler) {
        try {
            Class applicationClass = Class.forName("com.apple.eawt.Application");
            Method getApplicationMethod = applicationClass.getDeclaredMethod("getApplication", (Class[]) null);
            Object application = getApplicationMethod.invoke(null, (Object[]) null);

            Class openURIHandlerClass = Class.forName("java.awt.desktop.OpenURIHandler", false, applicationClass.getClassLoader());
            Method setOpenURIHandlerMethod = applicationClass.getMethod("setOpenURIHandler", openURIHandlerClass);

            AppleURIEventInvocationHandler handler = new AppleURIEventInvocationHandler(urlHandler);
            Object openURIEvent = Proxy.newProxyInstance(openURIHandlerClass.getClassLoader(), new Class[]{openURIHandlerClass}, handler);
            setOpenURIHandlerMethod.invoke(application, openURIEvent);

        } catch (Exception ignored) {
        }
    }
}