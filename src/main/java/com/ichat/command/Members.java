package com.ichat.command;

import com.ichat.server.MessageBroker;
import com.ichat.server.SocketConnection;
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.Map;

public class Members extends AbstractStringCommand {

    private MessageBroker messageBroker;

    public Members(MessageBroker messageBroker) {
        super();
        this.messageBroker = messageBroker;
    }

    @Override
    public String process(Map<String, String> paramArgMap) {
        if (MapUtils.isNotEmpty(paramArgMap) && paramArgMap.containsKey(HELP_PARAM)) {
            return help();
        }
        return generateHtmlView(messageBroker.getConnectedClientMap().values());
    }

    @Override
    public String getDescription() {
        return "Displays the currently connected users on the iChat server";
    }


    @Override
    public Character getDefaultParameter() {
        return null;
    }

    private String generateHtmlView(Collection<SocketConnection> socketConnections) {
        StringBuilder htmlViewBuilder = new StringBuilder();
        htmlViewBuilder.append("<div class=\"system-message\">Current users<ul>");
        socketConnections.forEach(c -> {
            htmlViewBuilder.append(String.format("<li>%s</li>", c.getUsername()));
        });
        htmlViewBuilder.append("</ul></div>");
        return htmlViewBuilder.toString();
    }
}
