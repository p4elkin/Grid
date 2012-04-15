package org.vaadin.addon.grid.rpc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.addon.grid.client.rpc.Method;
import org.vaadin.addon.grid.client.rpc.ClientSideProxy.Param;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.Resource;

/**
 * This is a server-side class that implements methods to invoke client-side
 * methods.
 * 
 * This hides all the bookkeeping needed to implement a higly-interactive Vaadin
 * widgets that communicate a lot with the associated server-side Component.
 * 
 * Using this class components can call registered handlers from the client-side
 * using methods {@link #call(String, Object...)} and
 * {@link #callOnce(String, Object...)} during the lifecycle of the widget.
 * 
 * When the client-side widget needs to be intialized the
 * {@link ServerSideHandler} is used.
 * 
 * @author Sami Ekblad / Vaadin
 * 
 */
@SuppressWarnings("serial")
public class ServerSideProxy implements Serializable {

    private static final String SERVER_CALL_PREFIX = "c_";

    private static final String SERVER_CALL_PARAM_PREFIX = "p_";

    private static final String SERVER_CALL_SEPARATOR = "_";

    private static final String SERVER_HAS_SENT_THE_INIT = "_si";

    private static final String CLIENT_INIT = "_init";

    private ServerSideHandler handler;

    private List<Object[]> clientCallQueue = new ArrayList<Object[]>();

    private Map<String, Method> methods = new HashMap<String, Method>();

    private Object[] clientInitParams;

    private boolean initSent;

    public ServerSideProxy(final ServerSideHandler handler) {
        this.handler = handler;
        initSent = false;
    }

    private void receiveFromClient(Map<String, Object> variables) {
        if (variables.containsKey(CLIENT_INIT) && clientInitParams == null) {
            initClientWidget(handler.initRequestFromClient());
        }

        // Other calls
        for (String n : variables.keySet()) {
            if (n.startsWith(SERVER_CALL_PREFIX)) {
                int length = SERVER_CALL_PREFIX.length();
                String cidStr = n.substring(length, n.indexOf(SERVER_CALL_SEPARATOR, length + 1));
                int cid = Integer.parseInt(cidStr);
                n = n.substring(SERVER_CALL_PREFIX.length() + ("" + cid).length() + 1);
                List<Object> params = new ArrayList<Object>();
                int i = 0;
                String pn = SERVER_CALL_PARAM_PREFIX + cid + SERVER_CALL_SEPARATOR + i;
                while (variables.containsKey(pn)) {
                    params.add(variables.get(pn));
                    pn = SERVER_CALL_PARAM_PREFIX + cid + SERVER_CALL_SEPARATOR + (++i);
                }

                Method m = methods.get(n);
                if (m != null) {
                    m.invoke(n, params.toArray());
                } else {
                    handler.callFromClient(n, params.toArray());
                }

            }
        }
    }

    public void register(String methodName, Method method) {
        methods.put(methodName, method);
    }

    public void call(String method, Object... params) {
        Object[] call = new Object[params.length + 1];
        call[0] = method;
        for (int i = 0; i < params.length; i++) {
            call[i + 1] = params[i];
        }
        synchronized (clientCallQueue) {
            clientCallQueue.add(call);
        }
        handler.requestRepaint();
    }
    
    public void callOnce(String method, Object... params) {
        cancelCalls(method);
        Object[] call = new Object[params.length + 1];
        call[0] = method;
        for (int i = 0; i < params.length; i++) {
            call[i + 1] = params[i];
        }
        synchronized (clientCallQueue) {
            clientCallQueue.add(call);
        }
        handler.requestRepaint();
    }

    private void cancelCalls(String methodName) {
        synchronized (clientCallQueue) {
            final List<Object[]> tmp = new ArrayList<Object[]>(clientCallQueue);
            for (Object[] c : tmp) {
                if (c[0].equals(methodName)) {
                    clientCallQueue.remove(c);
                }
            }
        }
    }

    public void paintContent(PaintTarget target) throws PaintException {
        /*Ask init 1) when explicitly asked 2) when no client calls has been
        made AND no pending init data is available*/
        if (!initSent) {
            if (clientInitParams == null) {
                initClientWidget(handler.initRequestFromClient());
            }
            initSent = true;
        } else {
            target.addAttribute(SERVER_HAS_SENT_THE_INIT, true);
        }

        target.startTag("cl");

        if (clientInitParams != null) {
            target.startTag("c");
            target.addAttribute("n", CLIENT_INIT);
            paintCallParameters(target, clientInitParams, 0);
            target.endTag("c");
            clientInitParams = null;
        }

        synchronized (clientCallQueue) {
            try {
                final List<Object[]> tmpCalls = new ArrayList<Object[]>(clientCallQueue); // copy
                for (Object[] aCall : tmpCalls) {
                    target.startTag("c");
                    target.addAttribute("n", (String) aCall[0]);
                    paintCallParameters(target, aCall, 1);
                    target.endTag("c");
                    clientCallQueue.remove(aCall);
                }
            } catch (Throwable e) {
                throw new PaintException(e.getMessage());
            } finally {
                target.endTag("cl");
            }
        }
    }

    private void paintCallParameters(PaintTarget target, Object[] aCall, int start) throws PaintException {
        target.addAttribute("pc", aCall.length - start);
        for (int i = start; i < aCall.length; i++) {
            if (aCall[i] != null) {
                int pi = i - start; // index parameters from start
                paintCallParameter(target, aCall[i], pi);
            }
        }
    }

    private void paintCallParameter(PaintTarget target, Object p, int pi) throws PaintException {
        if (p instanceof String) {
            target.addAttribute("p" + pi, (String) p);
            target.addAttribute("pt" + pi, Param.STRING.code());
        } else if (p instanceof Float) {
            target.addAttribute("p" + pi, (Float) p);
            target.addAttribute("pt" + pi, Param.FLOAT.code());
        } else if (p instanceof Boolean) {
            target.addAttribute("p" + pi, (Boolean) p);
            target.addAttribute("pt" + pi, Param.BOOLEAN.code());
        } else if (p instanceof Integer) {
            target.addAttribute("p" + pi, (Integer) p);
            target.addAttribute("pt" + pi, Param.INT.code());
        } else if (p instanceof Map) {
            target.addAttribute("p" + pi, (Map<?, ?>) p);
            target.addAttribute("pt" + pi, Param.MAP.code());
        } else if (p instanceof Resource) {
            target.addAttribute("p" + pi, (Resource) p);
            target.addAttribute("pt" + pi, Param.RESOURCE.code());
        } else if (p instanceof Paintable) {
            target.addAttribute("p" + pi, (Paintable) p);
            target.addAttribute("pt" + pi, Param.PAINTABLE.code());
            target.startTag("p" + pi);
            ((Paintable) p).paint(target);
            target.endTag("p" + pi);
        }
    }

    public void changeVariables(Object source, Map<String, Object> variables) {
        receiveFromClient(variables);
    }

    public void requestClientSideInit() {
        initSent = false;
        handler.requestRepaint();
    }

    private void initClientWidget(Object... params) {
        clientInitParams = params;
        handler.requestRepaint();
    }

}
