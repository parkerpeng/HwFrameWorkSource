package org.apache.xpath;

import javax.xml.transform.TransformerException;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class CachedXPathAPI {
    protected XPathContext xpathSupport;

    public CachedXPathAPI() {
        this.xpathSupport = new XPathContext(false);
    }

    public CachedXPathAPI(CachedXPathAPI priorXPathAPI) {
        this.xpathSupport = priorXPathAPI.xpathSupport;
    }

    public XPathContext getXPathContext() {
        return this.xpathSupport;
    }

    public Node selectSingleNode(Node contextNode, String str) throws TransformerException {
        return selectSingleNode(contextNode, str, contextNode);
    }

    public Node selectSingleNode(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        return selectNodeIterator(contextNode, str, namespaceNode).nextNode();
    }

    public NodeIterator selectNodeIterator(Node contextNode, String str) throws TransformerException {
        return selectNodeIterator(contextNode, str, contextNode);
    }

    public NodeIterator selectNodeIterator(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        return eval(contextNode, str, namespaceNode).nodeset();
    }

    public NodeList selectNodeList(Node contextNode, String str) throws TransformerException {
        return selectNodeList(contextNode, str, contextNode);
    }

    public NodeList selectNodeList(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        return eval(contextNode, str, namespaceNode).nodelist();
    }

    public XObject eval(Node contextNode, String str) throws TransformerException {
        return eval(contextNode, str, contextNode);
    }

    public XObject eval(Node contextNode, String str, Node namespaceNode) throws TransformerException {
        PrefixResolver prefixResolver = new PrefixResolverDefault(namespaceNode.getNodeType() == (short) 9 ? ((Document) namespaceNode).getDocumentElement() : namespaceNode);
        return new XPath(str, null, prefixResolver, 0, null).execute(this.xpathSupport, this.xpathSupport.getDTMHandleFromNode(contextNode), prefixResolver);
    }

    public XObject eval(Node contextNode, String str, PrefixResolver prefixResolver) throws TransformerException {
        XPath xpath = new XPath(str, null, prefixResolver, 0, null);
        XPathContext xpathSupport = new XPathContext(false);
        return xpath.execute(xpathSupport, xpathSupport.getDTMHandleFromNode(contextNode), prefixResolver);
    }
}
