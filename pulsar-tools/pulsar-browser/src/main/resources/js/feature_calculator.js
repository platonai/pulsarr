/**
 * Created by vincent on 16-5-17.
 *
 * NodeVisitor: used with NodeTraversor together
 */

/**
 * Create a new NodeFeatureCalculator
 */
let __pulsar_NodeFeatureCalculator = function() {
    this.stopped = false;

    this.config = __pulsar_CONFIGS || {};

    this.debug = this.config.debug;

    this.sequence = 0;
};

/**
 * Check if stopped
 */
__pulsar_NodeFeatureCalculator.prototype.isStopped = function() {
    return this.stopped;
};

/**
 * Enter the element for the first time
 * @param node {Node} the node to enter
 * @param  depth {Number} the depth in the DOM
 */
__pulsar_NodeFeatureCalculator.prototype.head = function(node, depth) {
    if (node.__pulsar_isIFrame()) {
        return
    }

    ++this.sequence;

    node.__pulsar_nodeExt = new __pulsar_NodeExt(node, this.config);

    this.calcSelfIndicator(node, depth);
};

/**
 * Calculate the features of the Node itself
 * @param node {Node|Text|HTMLElement} the node to enter
 * @param  depth {Number} the depth in the DOM
 */
__pulsar_NodeFeatureCalculator.prototype.calcSelfIndicator = function(node, depth) {
    let nodeExt = node.__pulsar_nodeExt;

    if (node.__pulsar_isText()) {
        this.calcCharacterWidth(node, depth);
    }

    nodeExt.depth = depth;
    nodeExt.sequence = this.sequence;

    if (node.__pulsar_isElement()) {
        // Browser computed styles. Only leaf elements matter
        nodeExt.propertyNames = this.config.propertyNames || [];
        let requiredPropertyNames = nodeExt.propertyNames.concat("overflow");
        nodeExt.styles = __pulsar_utils__.getComputedStyle(node, requiredPropertyNames);
    }

    // Calculate the rectangle of this node
    nodeExt.rect = node.__pulsar_getRect();

    // TODO: since there are too many _hidden nodes, we should simplified it to save space
    if (node.__pulsar_isElement()) {
        // "hidden" seems not defined properly,
        // some parent element is "hidden" and some of there children are not expected to be hidden
        // for example, ul tag often have a zero dimension
        if (nodeExt.isHidden()) {
            // node.toggleAttribute(ATTR_HIDDEN, true);
            node.setAttribute(this.config.ATTR_HIDDEN, '');
        }

        if (nodeExt.isOverflowHidden() || (nodeExt.hasParent() && nodeExt.parent().node.hasAttribute(this.config.ATTR_OVERFLOW_HIDDEN))) {
            // node.toggleAttribute(ATTR_OVERFLOW_HIDDEN, true);
            node.setAttribute(this.config.ATTR_OVERFLOW_HIDDEN, '');
        }
    }

    // all descendant nodes should be smaller than this one
    if (nodeExt.hasOverflowHidden()) {
        // TODO: also update max height
        nodeExt.updateMaxWidth(nodeExt.rect.width);
    } else {
        nodeExt.updateMaxWidth(this.config.viewPortWidth);
    }

    nodeExt.adjustDOMRect();
};

/**
 * Leaving the the element
 *
 * @param node {Node|Element} the node visited
 * @param  depth {Number} the depth in the DOM
 */
__pulsar_NodeFeatureCalculator.prototype.tail = function(node, depth) {
    if (node.__pulsar_isIFrame()) {
        return
    }

    let config = this.config
    let nodeExt = node.__pulsar_nodeExt;
    if (!nodeExt) {
        return
    }

    if (node.__pulsar_isElement()) {
        node.__pulsar_setAttributeIfNotBlank(config.ATTR_COMPUTED_STYLE, nodeExt.formatStyles());
        node.__pulsar_setAttributeIfNotBlank(config.ATTR_ELEMENT_NODE_VI, nodeExt.formatDOMRect());

        // calculate the rectangle of each child text node
        for (let i = 0; i < node.childNodes.length; ++i) {
            let childNodeExt = node.childNodes[i].__pulsar_nodeExt;
            if (childNodeExt && childNodeExt.node.__pulsar_isText()) {
                // 'tv' is short for 'text node vision information'
                node.__pulsar_setAttributeIfNotBlank(config.ATTR_TEXT_NODE_VI + i, childNodeExt.formatDOMRect());
            }
        }
    }

    if (this.debug > 0) {
        this.addDebugInfo()
    }
};

/**
 * Calculate the width of the text node, this is a complement of the rectangle information, can be used for debugging
 *
 * @param node {Node} the node to enter
 * @param  depth {Number} the depth in the DOM
 * @return {Number}
 */
__pulsar_NodeFeatureCalculator.prototype.calcCharacterWidth = function(node, depth) {
    let parent = node.parentElement;
    let cw = parent.getAttribute('_cw');
    let width = 0;
    if (!cw) {
        let text = __pulsar_utils__.getTextContent(node);
        if (text.length > 0) {
            width = __pulsar_utils__.getElementTextWidth(text, parent);
            cw = Math.round(width / text.length * 10) / 10;
            parent.setAttribute('_cw', cw.toString())
        }
    }
    return width
};

__pulsar_NodeFeatureCalculator.prototype.addDebugInfo = function(node) {
    if (!node.__pulsar_nodeExt) {
        return
    }

    let config = this.config;
    let nodeExt = node.__pulsar_nodeExt;

    if (node.__pulsar_isText()) {
        // 'tl' is short for 'text length', it's used to diagnosis
        if (node.textContent) {
            __pulsar_utils__.addTuple(node, config.ATTR_DEBUG, "tl" + i, node.textContent.length);
        }
    } else {
        let descend = __pulsar_utils__.getIntAttribute(node, "_d", 0);
        __pulsar_utils__.increaseIntAttribute(node.parentElement, '_d', 1);
    }
};