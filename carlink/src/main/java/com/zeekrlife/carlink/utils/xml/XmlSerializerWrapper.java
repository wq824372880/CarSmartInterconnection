
package com.zeekrlife.carlink.utils.xml;

import androidx.annotation.NonNull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Objects;
import org.xmlpull.v1.XmlSerializer;

/**
 * Wrapper which delegates all calls through to the given {@link XmlSerializer}.
 * @author Lei.Chen29
 */
public class XmlSerializerWrapper implements XmlSerializer {
    private final XmlSerializer mWrapped;

    public XmlSerializerWrapper(@NonNull XmlSerializer wrapped) {
        mWrapped = Objects.requireNonNull(wrapped);
    }

    @Override
    public void setFeature(String name, boolean state) {
        mWrapped.setFeature(name, state);
    }

    @Override
    public boolean getFeature(String name) {
        return mWrapped.getFeature(name);
    }

    @Override
    public void setProperty(String name, Object value) {
        mWrapped.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) {
        return mWrapped.getProperty(name);
    }

    @Override
    public void setOutput(OutputStream os, String encoding) throws IOException {
        mWrapped.setOutput(os, encoding);
    }

    @Override
    public void setOutput(Writer writer) throws IOException, IllegalArgumentException, IllegalStateException {
        mWrapped.setOutput(writer);
    }

    @Override
    public void startDocument(String encoding, Boolean standalone) throws IOException {
        mWrapped.startDocument(encoding, standalone);
    }

    @Override
    public void endDocument() throws IOException {
        mWrapped.endDocument();
    }

    @Override
    public void setPrefix(String prefix, String namespace) throws IOException {
        mWrapped.setPrefix(prefix, namespace);
    }

    @Override
    public String getPrefix(String namespace, boolean generatePrefix) {
        return mWrapped.getPrefix(namespace, generatePrefix);
    }

    @Override
    public int getDepth() {
        return mWrapped.getDepth();
    }

    @Override
    public String getNamespace() {
        return mWrapped.getNamespace();
    }

    @Override
    public String getName() {
        return mWrapped.getName();
    }

    @Override
    public XmlSerializer startTag(String namespace, String name) throws IOException {
        return mWrapped.startTag(namespace, name);
    }

    @Override
    public XmlSerializer attribute(String namespace, String name, String value) throws IOException {
        return mWrapped.attribute(namespace, name, value);
    }

    @Override
    public XmlSerializer endTag(String namespace, String name) throws IOException {
        return mWrapped.endTag(namespace, name);
    }

    @Override
    public XmlSerializer text(String text) throws IOException {
        return mWrapped.text(text);
    }

    @Override
    public XmlSerializer text(char[] buf, int start, int len) throws IOException {
        return mWrapped.text(buf, start, len);
    }

    @Override
    public void cdsect(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        mWrapped.cdsect(text);
    }

    @Override
    public void entityRef(String text) throws IOException {
        mWrapped.entityRef(text);
    }

    @Override
    public void processingInstruction(String text) throws IOException {
        mWrapped.processingInstruction(text);
    }

    @Override
    public void comment(String text) throws IOException {
        mWrapped.comment(text);
    }

    @Override
    public void docdecl(String text) throws IOException {
        mWrapped.docdecl(text);
    }

    @Override
    public void ignorableWhitespace(String text) throws IOException {
        mWrapped.ignorableWhitespace(text);
    }

    @Override
    public void flush() throws IOException {
        mWrapped.flush();
    }
}