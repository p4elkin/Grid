package org.vaadin.addon.grid.rpc;

import java.io.Serializable;

import org.vaadin.addon.grid.client.rpc.ClientSideHandler;

import com.vaadin.ui.Component;

/**
 * Interface for receiver/handler for method invocations from the client-side.
 *
 * @author Sami Ekblad / Vaadin
 *
 */
public interface ServerSideHandler extends Serializable {

    /**
     * Invoked when client-side request full initialization.
     *
     * @return Initialization parameters to the client. These should be handled
     *         in {@link ClientSideHandler#initWidget(Object[])}.
     */
    public Object[] initRequestFromClient();

    /**
     * Invoked to handle a method call from the client-side.
     *
     * @param method
     * @param params
     */
    public void callFromClient(String method, Object[] params);

    /**
     * Invoked to notify the underlying component repaint is needed. This is
     * used to pass the information to the right (hosting) Vaadin component to
     * notify the Vaadin about changes to the client.
     *
     * Typically only calls the component's own
     * {@link Component#requestRepaint()} .
     */
    public void requestRepaint();
}