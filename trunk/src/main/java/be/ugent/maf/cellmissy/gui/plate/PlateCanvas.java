/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.plate;

import javax.swing.JFrame;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

/**
 *
 * @author Paola
 */
public class PlateCanvas extends JSVGCanvas {

    public SVGDocument doc;

    public PlateCanvas() {
        this.makeSVG();
        this.setSVGDocument(doc);
    }

    public void makeSVG() {
        // Create an SVG document.
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);

        // get the root element (the svg element)
        Element svgRoot = doc.getDocumentElement();

        // set the width and height attribute on the svg root element
        svgRoot.setAttributeNS(null, "width", "500");
        svgRoot.setAttributeNS(null, "height", "500");

        Element circle = doc.createElementNS(svgNS, "path");
        circle.setAttributeNS(null, "sodipodi:cx", "238.57143");
        circle.setAttributeNS(null, "sodipodi:cy", "258.07648");
        circle.setAttributeNS(null, "sodipodi:type", "arc");
        circle.setAttributeNS(null, "sodipodi:rx", "25");
        circle.setAttributeNS(null, "sodipodi:ry", "25");
        circle.setAttributeNS(null, "style", "fill:#ff0000;fill-rule:evenodd;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1");
        circle.setAttributeNS(null, "d", "m 400,258.07648 a 161.42857,157.14285 0 1 1 -322.857147,0 161.42857,157.14285 0 1 1 322.857147,0 z");
        circle.setAttributeNS(null, "sodipodi:type", "arc");
        
        svgRoot.appendChild(circle);
        
    }

    public static void main(String[] args) {
        PlateCanvas plateCanvas = new PlateCanvas();

        JFrame frame = new JFrame();
        frame.getContentPane().add(plateCanvas);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }
}
