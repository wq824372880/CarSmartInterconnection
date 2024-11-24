package com.zeekrlife.carlink.utils.xml;

import androidx.annotation.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Wrapper which delegates all calls through to the given {@link XmlPullParser}.
 * @author Lei.Chen29
 */
public class XmlPullParserWrapper implements XmlPullParser {
    private final XmlPullParser mWrapped;

    public XmlPullParserWrapper(@NonNull XmlPullParser wrapped) {
        mWrapped = Objects.requireNonNull(wrapped);
    }

    @Override
    public void setFeature(String name, boolean state) throws XmlPullParserException {
        mWrapped.setFeature(name, state);
    }

    @Override
    public boolean getFeature(String name) {
        return mWrapped.getFeature(name);
    }

    @Override
    public void setProperty(String name, Object value) throws XmlPullParserException {
        mWrapped.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) {
        return mWrapped.getProperty(name);
    }

    @Override
    public void setInput(Reader in) throws XmlPullParserException {
        mWrapped.setInput(in);
    }

    @Override
    public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
        mWrapped.setInput(inputStream, inputEncoding);
    }

    @Override
    public String getInputEncoding() {
        return mWrapped.getInputEncoding();
    }

    @Override
    public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
        mWrapped.defineEntityReplacementText(entityName, replacementText);
    }

    @Override
    public int getNamespaceCount(int depth) throws XmlPullParserException {
        return mWrapped.getNamespaceCount(depth);
    }

    @Override
    public String getNamespacePrefix(int pos) throws XmlPullParserException {
        return mWrapped.getNamespacePrefix(pos);
    }

    @Override
    public String getNamespaceUri(int pos) throws XmlPullParserException {
        return mWrapped.getNamespaceUri(pos);
    }

    @Override
    public String getNamespace(String prefix) {
        return mWrapped.getNamespace(prefix);
    }

    @Override
    public int getDepth() {
        return mWrapped.getDepth();
    }

    @Override
    public String getPositionDescription() {
        return mWrapped.getPositionDescription();
    }

    @Override
    public int getLineNumber() {
        return mWrapped.getLineNumber();
    }

    @Override
    public int getColumnNumber() {
        return mWrapped.getColumnNumber();
    }

    @Override
    public boolean isWhitespace() throws XmlPullParserException {
        return mWrapped.isWhitespace();
    }

    @Override
    public String getText() {
        return mWrapped.getText();
    }

    @Override
    public char[] getTextCharacters(int[] holderForStartAndLength) {
        return mWrapped.getTextCharacters(holderForStartAndLength);
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
    public String getPrefix() {
        return mWrapped.getPrefix();
    }

    @Override
    public boolean isEmptyElementTag() throws XmlPullParserException {
        return mWrapped.isEmptyElementTag();
    }

    @Override
    public int getAttributeCount() {
        return mWrapped.getAttributeCount();
    }

    @Override
    public String getAttributeNamespace(int index) {
        return mWrapped.getAttributeNamespace(index);
    }

    @Override
    public String getAttributeName(int index) {
        return mWrapped.getAttributeName(index);
    }

    @Override
    public String getAttributePrefix(int index) {
        return mWrapped.getAttributePrefix(index);
    }

    @Override
    public String getAttributeType(int index) {
        return mWrapped.getAttributeType(index);
    }

    @Override
    public boolean isAttributeDefault(int index) {
        return mWrapped.isAttributeDefault(index);
    }

    @Override
    public String getAttributeValue(int index) {
        return mWrapped.getAttributeValue(index);
    }

    @Override
    public String getAttributeValue(String namespace, String name) {
        return mWrapped.getAttributeValue(namespace, name);
    }

    @Override
    public int getEventType() throws XmlPullParserException {
        return mWrapped.getEventType();
    }

    @Override
    public int next() throws XmlPullParserException, IOException {
        return mWrapped.next();
    }

    @Override
    public int nextToken() throws XmlPullParserException, IOException {
        return mWrapped.nextToken();
    }

    @Override
    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
        mWrapped.require(type, namespace, name);
    }

    @Override
    public String nextText() throws XmlPullParserException, IOException {
        return mWrapped.nextText();
    }

    @Override
    public int nextTag() throws XmlPullParserException, IOException {
        return mWrapped.nextTag();
    }
}