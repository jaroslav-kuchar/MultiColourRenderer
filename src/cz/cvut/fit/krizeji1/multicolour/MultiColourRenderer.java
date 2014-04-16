package cz.cvut.fit.krizeji1.multicolour;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import org.gephi.graph.api.Node;
import org.gephi.preview.api.*;
import org.gephi.preview.plugin.items.NodeItem;
import org.gephi.preview.spi.ItemBuilder;
import org.gephi.preview.spi.Renderer;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Element;
import processing.core.PGraphicsJava2D;
import java.util.logging.Logger;

/**
 *
 * @author James
 */
@ServiceProvider(service = Renderer.class)
public class MultiColourRenderer implements Renderer {
    private static final Logger logger = Logger.getLogger(ClassLoader.class.getName());
    public static final String COLUMN_COLOUR_LIST = "colourList";

    @Override
    public void preProcess(PreviewModel previewModel) {
    }

    @Override
    public void render(Item item, RenderTarget target, PreviewProperties properties) {
        if (target instanceof ProcessingTarget) {
            renderProcessing(item, (ProcessingTarget) target, properties);
        } else if (target instanceof SVGTarget) {
            renderSVG(item, (SVGTarget) target, properties);
        } else if (target instanceof PDFTarget) {
            renderPDF(item, (PDFTarget) target, properties);
        }
    }

    @Override
    public PreviewProperty[] getProperties() {
        return new PreviewProperty[]{
                    PreviewProperty.createProperty(this, "MultiColourNode", Boolean.class,
                    "Multi Colour Nodes",
                    "Make a node be coloured more then one hue",
                    PreviewProperty.CATEGORY_NODES).setValue(false)
                };
    }

    @Override
    public boolean isRendererForitem(Item item, PreviewProperties properties) {
        return item.getType().equals(Item.NODE);
    }

    private void renderProcessing(Item item, ProcessingTarget target, PreviewProperties properties) {
        //Params
        Float xF = item.getData(NodeItem.X);
        Float yF = item.getData(NodeItem.Y);
        Float sizeF = item.getData(NodeItem.SIZE);

        int x = xF.intValue();
        int y = yF.intValue();
        int size = sizeF.intValue();

        Node n = (Node) item.getSource();
        String colourList = (String) n.getNodeData().getAttributes().getValue(COLUMN_COLOUR_LIST);

        ArrayList<Color> colourArray = splitColourList(colourList);
        int radius = size - 4;

        if(colourArray == null) {
          return;
        }


        PGraphicsJava2D graphics = (PGraphicsJava2D) target.getGraphics();
        Graphics2D g2 = graphics.g2;


        // draw colour indices as pie chart around point
        if (colourArray.size() > 0) {
            double arcAngle = 360.0 / colourArray.size();

            // draw 'pieces of pie'
            for (int i = 0; i < colourArray.size(); i++) {
                //System.out.println(Integer.parseInt(n.getColour(i)));
                g2.setColor(colourArray.get(i));
                g2.fillArc((x - radius), (y - radius), (radius * 2), (radius * 2),
                        (int) (arcAngle * i), (int) arcAngle);
            }

            // draw 'spokes' between pieces
            if (colourArray.size() > 1) {
                Stroke oldStroke = g2.getStroke();
                for (int i = 0; i < colourArray.size(); i++) {
                    double spokeAngle = arcAngle * i / 180 * Math.PI;
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(0.5f));
                    g2.drawLine(x, y, (int) (x + radius * Math.cos(spokeAngle)), (int) (y - radius * Math.sin(spokeAngle)));
                }
                g2.setStroke(oldStroke);
            }

        } else {
            // draw filled white circle
            g2.setColor(Color.WHITE);
            g2.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        }

        // draw black circle
        g2.setColor(Color.BLACK);
        g2.drawOval(x - radius, y - radius, radius * 2, radius * 2);


        //original
        //g2.setPaint(p);
        //g2.fillOval((int) (x.floatValue() - radius), (int) (y.floatValue() - radius), (int) (radius * 2), (int) (radius * 2));
    }

    private void renderSVG(Item item, SVGTarget target, PreviewProperties properties) {

        //Params
        Float x = item.getData(NodeItem.X);
        Float y = item.getData(NodeItem.Y);
        Float size = item.getData(NodeItem.SIZE);


        Node n = (Node) item.getSource();
        String colourList = (String) n.getNodeData().getAttributes().getValue(COLUMN_COLOUR_LIST);

        ArrayList<Color> colourArray = splitColourList(colourList);
        Float radius = size - 3;
        //float borderSize = properties.getFloatValue(PreviewProperty.NODE_BORDER_WIDTH);
        float alpha = properties.getIntValue(PreviewProperty.NODE_OPACITY) / 100f;
        if (alpha > 1) {
            alpha = 1;
        }

        // draw colour indices as pie chart around point
        if (colourArray.size() > 0) {
            float arcAngle = (float) (360.0 / colourArray.size());

            // draw 'pieces of pie'
            for (int i = 0; i < colourArray.size(); i++) {
                //Arc2D arca = new Arc2D.Float((x - radius), (y - radius), (radius * 2), (radius * 2), (arcAngle * i), (arcAngle), Arc2D.CHORD);
                float[] startXY = toCartesianCoordinate(x, y, radius, arcAngle*i);
                float[] endXY = toCartesianCoordinate(x, y, radius, (arcAngle*i)+arcAngle);
                Element nodeElem = target.createElement("path");
                nodeElem.setAttribute("class", n.getNodeData().getId());
                nodeElem.setAttribute("fill", target.toHexString(colourArray.get(i)));
                
                nodeElem.setAttribute("d", "M" + " " + startXY[0] + " " + startXY[1] + " " + "A" + " " + radius + " " + radius + " " + 0 + " " + 0 + " " + 0 + " " + endXY[0] + " " + endXY[1]);
                //PATH = M startXY[0] startXY[1] A radius radius 0 0 0 endXY[0] endXY[1]
                
                nodeElem.setAttribute("fill-opacity", "" + alpha);
                nodeElem.setAttribute("stroke", "black");
                nodeElem.setAttribute("stroke-width", "2");
            }


        } else {
            Element nodeElem = target.createElement("circle");
            nodeElem.setAttribute("class", n.getNodeData().getId());
            nodeElem.setAttribute("cx", x.toString());
            nodeElem.setAttribute("cy", y.toString());
            nodeElem.setAttribute("r", size.toString());
            nodeElem.setAttribute("fill", target.toHexString(Color.WHITE));
            nodeElem.setAttribute("fill-opacity", "" + alpha);
        }



    }

    private void renderPDF(Item item, PDFTarget pdfTarget, PreviewProperties properties) {
        //TODO
    }

    /**
     * turns a list of numbers into an ArrayList of Colours
     *
     * @param colorList String, list of colours taken from the column
     * "colourList"
     * @return An ArrayList of Colour objects, to be drawn on the node
     */
    private ArrayList<Color> splitColourList(String colourList) {
        if(colourList==null) {
            logger.log(Level.INFO, "colourlist is null");
          return null;
        }
        ArrayList<Color> toReturn = new ArrayList<Color>();

        //-1 denotyetes a default value where no colours were attributed to a node
        if (!colourList.equals("-1")) {
            Scanner scan = new Scanner(colourList);
            scan.useDelimiter(",");
            while (scan.hasNext()) {
                String str = scan.next();
                Integer rgb = Integer.parseInt(str);
                Color col = new Color(rgb);
                toReturn.add(col);
            }
        }

        return toReturn;

    }

    private float[] toCartesianCoordinate(float centerX, float centerY, float radius, float angleInDegrees) {
        float angleInRadians = (float) (angleInDegrees * Math.PI / 180.0);
        float x = (float) (centerX + radius * Math.cos(angleInRadians));
        float y = (float) (centerY + radius * Math.sin(angleInRadians));
        float[] arr = {x, y};
        return arr;
    }    

    @Override
    public String getDisplayName() {
        return "MultiColour";
    }

    @Override
    public boolean needsItemBuilder(ItemBuilder ib, PreviewProperties pp) {
        return false;
    }
}
