/*
 * 
 */
package org.smartly.packages.velocity.impl;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.util.RegExUtils;
import org.smartly.packages.velocity.impl.engine.VLCEngine;
import org.smartly.packages.velocity.impl.vtools.util.VLCToolbox;

import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton helper for velocity engine.
 *
 * @author Gian Angelo Geminiani
 */
public class VLCManager implements IVLCCostants {

    private VLCEngine _engine;

    private VLCManager() {
    }

    public String evaluateText(final String templateName,
                               final String vlcText,
                               final Map<String, Object> contextData) throws Exception {
        final VelocityContext context = getContext(contextData);
        return this.evaluate(null, templateName, vlcText, context);
    }

    public String evaluateText(final VelocityEngine engine,
                               final String templateName,
                               final String vlcText,
                               final Map<String, Object> contextData) throws Exception {
        final VelocityContext context = getContext(contextData);
        return this.evaluate(engine, templateName, vlcText, context);
    }

    public String evaluateText(final String templateName,
                               final String vlcText,
                               final VelocityContext context) throws Exception {
        return this.evaluate(null, templateName, vlcText, context);
    }

    public String evaluateText(final VelocityEngine engine,
                               final String templateName,
                               final String vlcText,
                               final VelocityContext context) throws Exception {
        return this.evaluate(engine, templateName, vlcText, context);
    }

    public String mergeTemplate(final String templateName,
                                final Map<String, Object> contextData) throws Exception {
        final VelocityContext context = getContext(contextData);
        return this.merge(null, templateName, context);
    }

    public VLCEngine getEngine() {
        if (null == _engine) {
            _engine = new VLCEngine();
        }
        return _engine;
    }

    public VLCToolbox getToolbox() {
        return VLCToolbox.getInstance();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private VelocityEngine getNativeEngine() throws Exception {
        if (null == _engine) {
            _engine = new VLCEngine();
        }
        return _engine.getNativeEngine();
    }

    private String replaceUnsolvedVariables(final String text) {
        if(!replaceUnsolved){
           return text;
        }
        final StringBuffer sb = new StringBuffer();
        final Pattern p = Pattern.compile(RegExUtils.VELOCITY_VARIABLES);
        final Matcher m = p.matcher(text);
        boolean result = m.find();
        // Loop through and add mathes
        while (result) {
            final String key = m.group();
            m.appendReplacement(sb, key.replaceAll("\\$", "#").
                    replaceAll("\\{", "").
                    replaceAll("\\}", ""));
            result = m.find();
        }
        // Add the last segment of input to 
        // the new String
        m.appendTail(sb);
        return sb.toString();
    }

    private String evaluate(final VelocityEngine engine,
                            final String templateName,
                            final String vlcText,
                            final VelocityContext context) throws Exception {
        //-- evaluate template --//
        final VelocityEngine velocity = null!=engine?engine:this.getNativeEngine();
        final StringWriter writer = new StringWriter();
        velocity.evaluate(context, writer, templateName, vlcText);

        return this.replaceUnsolvedVariables(writer.toString());
    }

    private String merge(final VelocityEngine engine,
                         final String templateName,
                         final VelocityContext context) throws Exception {
        //-- evaluate template --//
        final VelocityEngine velocity = null!=engine?engine:this.getNativeEngine();
        final StringWriter writer = new StringWriter();

        final Template template = velocity.getTemplate(templateName, CharEncoding.getDefault());
        template.merge(context, writer);

        return this.replaceUnsolvedVariables(writer.toString());
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    public static boolean replaceUnsolved = false;
    private static VLCManager _instance;

    public static VLCManager getInstance() {
        if (null == _instance) {
            _instance = new VLCManager();
        }
        return _instance;
    }


    private static VelocityContext getContext(final Map<String, Object> contextData) {
        return null != contextData && !contextData.isEmpty()
                ? new VelocityContext(contextData, VLCToolbox.getInstance().getToolsContext())
                : new VelocityContext(VLCToolbox.getInstance().getToolsContext());
    }
}
