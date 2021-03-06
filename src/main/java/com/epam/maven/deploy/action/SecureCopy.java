package com.epam.maven.deploy.action;

import com.epam.maven.deploy.Action;
import com.epam.maven.ssh.SecureCopyProtocol;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.epam.maven.util.Helper.SEPARATOR;
import static com.epam.maven.util.Helper.replaceAll;

/**
 * @author Vitaliy Boyarsky
 */
public class SecureCopy implements Action {

    @Getter
    @Setter
    @JsonProperty("items")
    private List<Bean> items;

    private To to = To.REMOTE;

    @JsonIgnore
    public To getTo() {
        return to;
    }

    @JsonSetter("to")
    public void setTo(String to) {
        this.to = To.valueOf(to.toUpperCase());
    }

    @Override
    public void execute(Session session, Map<Pattern, String> properties, Log logger)
            throws IOException, JSchException {
        logger.info("Start copying files");
        logger.info(SEPARATOR);
        System.out.println();

        for (Bean item : items) {
            switch (to) {
                case REMOTE:
                    SecureCopyProtocol.copyToRemote(session, replaceAll(properties, item.getSource()), replaceAll(properties, item.getTarget()));
                    break;
                case LOCAL:
                    SecureCopyProtocol.copyToLocal(session, replaceAll(properties, item.getSource()), replaceAll(properties, item.getTarget()));
                    break;
            }
        }

        System.out.println();
        logger.info(SEPARATOR);
        logger.info("Copying successfully completed");
    }

    private enum To {

        REMOTE, LOCAL

    }

    public static class Bean {

        @Getter
        @Setter
        @JsonProperty("source")
        private String source;

        @Getter
        @Setter
        @JsonProperty("target")
        private String target = ".";

        public Bean() {
            super();
        }

        public Bean(String source, String target) {
            this.source = source;
            this.target = target;
        }

    }

}
