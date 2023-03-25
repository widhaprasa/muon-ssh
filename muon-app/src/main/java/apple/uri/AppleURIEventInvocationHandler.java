package apple.uri;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;

class AppleURIEventInvocationHandler implements InvocationHandler {

    private final AppleURIHandler handler;

    public AppleURIEventInvocationHandler(AppleURIHandler handler) {
        this.handler = handler;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("openURI")) {
            try {
                Class openURIEventClass = Class.forName("java.awt.desktop.OpenURIEvent");
                Method getURLMethod = openURIEventClass.getMethod("getURI");
                URI uri = (URI) getURLMethod.invoke(args[0]);
                handler.handle(uri);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return proxy;
    }
}